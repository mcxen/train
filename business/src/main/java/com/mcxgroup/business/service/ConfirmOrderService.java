package com.mcxgroup.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mcxgroup.business.domain.*;
import com.mcxgroup.business.dto.ConfirmOrderMQDto;
import com.mcxgroup.business.enums.ConfirmOrderStatusEnum;
import com.mcxgroup.business.enums.RedisKeyPreEnum;
import com.mcxgroup.business.enums.SeatColEnum;
import com.mcxgroup.business.enums.SeatTypeEnum;
import com.mcxgroup.business.req.ConfirmOrderTicketReq;
import com.mcxgroup.common.context.LoginMemberContext;
import com.mcxgroup.common.exception.BusinessException;
import com.mcxgroup.common.exception.BusinessExceptionEnum;
import com.mcxgroup.common.resp.PageResp;
import com.mcxgroup.common.util.SnowUtil;
import com.mcxgroup.business.mapper.ConfirmOrderMapper;
import com.mcxgroup.business.req.ConfirmOrderQueryReq;
import com.mcxgroup.business.req.ConfirmOrderDoReq;
import com.mcxgroup.business.resp.ConfirmOrderQueryResp;
import jakarta.annotation.Resource;
import net.sf.jsqlparser.statement.select.Offset;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author MCXEN
 * @project Train
 */
@Service
public class ConfirmOrderService {
    private static final Logger LOG = LoggerFactory.getLogger(ConfirmOrderService.class);

    @Resource
    private ConfirmOrderMapper confirmOrderMapper;
    @Resource
    private DailyTrainTicketService dailyTrainTicketService;
    @Resource
    private DailyTrainCarriageService dailyTrainCarriageService;
    @Resource
    private DailyTrainSeatService dailyTrainSeatService;
    @Resource
    private AfterConfirmOrderService afterConfirmOrderService;
    @Autowired//AutoWired是优先按照类去找
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;
    @Resource
    private SkTokenService skTokenService;
    public void save(ConfirmOrderDoReq req) {

        DateTime now = DateTime.now();
        ConfirmOrder confirmOrder = BeanUtil.copyProperties(req, ConfirmOrder.class);
        if (ObjectUtil.isNull(confirmOrder.getId())) {
            confirmOrder.setId(SnowUtil.getSnowflakeNextId());
            confirmOrder.setCreateTime(now);
            confirmOrder.setUpdateTime(now);
            confirmOrderMapper.insert(confirmOrder);
        } else {
            confirmOrder.setUpdateTime(now);
            confirmOrderMapper.updateByPrimaryKey(confirmOrder);
        }
    }
//    @SentinelResource("doConfirm")
//    @SentinelResource(value = "doConfirm",blockHandler = "doConfirmBlock")
    @Async
    public void doConfirm(ConfirmOrderMQDto dto) {
        MDC.put("LOG_ID",dto.getLogId());
        LOG.info("开启异步的出票：「{}」",dto);
//        //分布式锁，调用redisson
//        RLock lock = null;
//        LOG.info("------Redisson 自带的看门狗 -------");
//        lock = redissonClient.getLock(key);
//        boolean tryLock = lock.tryLock(0, TimeUnit.SECONDS);//看门狗
//        if (tryLock) {
//            LOG.info("恭喜，抢到了Redisson锁");
//        }else {
//            LOG.info("遗憾，没有抢到抢到了Redisson锁，稍后重试");
//            throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_LOCK_FAIL);
//        }
        //获取分布式锁
        String lockKey = RedisKeyPreEnum.CONFIRM_ORDER + "-" + DateUtil.formatDate(dto.getDate()) + "-" + dto.getTrainCode();
        Boolean setIfAbsent = redisTemplate.opsForValue().setIfAbsent(lockKey, lockKey, 10, TimeUnit.SECONDS);
        if (Boolean.TRUE.equals(setIfAbsent)) {
            LOG.info("恭喜，抢到车次锁了！lockKey：{}", lockKey);
        } else {
            LOG.info("没抢到锁，有其它消费线程正在出票，不做任何处理");
//            throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_LOCK_FAIL);
            return;
        }
        try{
            while (true){
                //数据库查询订单表格
                ConfirmOrderExample confirmOrderExample = new ConfirmOrderExample();
                confirmOrderExample.setOrderByClause("id asc");
                ConfirmOrderExample.Criteria criteria = confirmOrderExample.createCriteria();
                criteria.andDateEqualTo(dto.getDate())
                        .andTrainCodeEqualTo(dto.getTrainCode())
                        .andStatusEqualTo(ConfirmOrderStatusEnum.INIT.getCode());
                PageHelper.startPage(1,5);
                List<ConfirmOrder> list = confirmOrderMapper.selectByExampleWithBLOBs(confirmOrderExample);
                if (CollUtil.isEmpty(list)) {
                    LOG.info("找不到订单，循环结束");
                    break;
                } else {
                    LOG.info("本次处理{}条确认订单", list.size());
                }
                for (ConfirmOrder confirmOrder : list) {
                    try{
                        sell(confirmOrder);//一条一条卖
                    }catch (BusinessException e){
                        if (e.getE().equals(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR)){
                            LOG.info("本订单余票不足，继续售卖下一个订单");
                            confirmOrder.setStatus(ConfirmOrderStatusEnum.EMPTY.getCode());
                            updateStatus(confirmOrder);
                        }else {
                            throw e;
                        }
                    }
                }
            }
        }finally {
            LOG.info("购票流程结束，释放锁！key：{}", lockKey);
            redisTemplate.delete(lockKey);
        }


    }

    private void sell(ConfirmOrder confirmOrder) {
        // 重新构建req
        ConfirmOrderDoReq req = new ConfirmOrderDoReq();
//        BeanUtil.copyProperties(confirmOrder,req);//复制属性
        req.setMemberId(confirmOrder.getMemberId());
        req.setDate(confirmOrder.getDate());
        req.setTrainCode(confirmOrder.getTrainCode());
        req.setStart(confirmOrder.getStart());
        req.setEnd(confirmOrder.getEnd());
        req.setDailyTrainTicketId(confirmOrder.getDailyTrainTicketId());
        req.setTickets(JSON.parseArray(confirmOrder.getTickets(),ConfirmOrderTicketReq.class));
        req.setLogId("");// 将确认订单的状态更新为处理中，避免重复处理
        confirmOrder.setStatus(ConfirmOrderStatusEnum.PENDING.getCode());
        updateStatus(confirmOrder);
        LOG.info("confirm_order[{}]状态更新为处理中", confirmOrder.getId());

        Date date = req.getDate();
        String trainCode = req.getTrainCode();
        String start = req.getStart();
        String end = req.getEnd();

        // 查出余票记录，需要得到真实的库存
        DailyTrainTicket dailyTrainTicket = dailyTrainTicketService.selectByUnique(date, trainCode, start, end);
        LOG.info("查出来余票的记录：{}",dailyTrainTicket);
        //反射的性能不如直接switch
        //扣减余票记录，判断是否充足
        reduceTicket(req, dailyTrainTicket);
        //最终购票记录
        List<DailyTrainSeat> finalSeatList = new ArrayList<>();
        List<ConfirmOrderTicketReq> tickets = req.getTickets();
        //计算偏移的值，
        // 比如选择的是C1，D2，则偏移值是：[0.5]
        // 比如选择的是A1,B1,C1，则偏移值是：[0,1,2]
        ConfirmOrderTicketReq ticketReq0 = tickets.get(0);
        if (StrUtil.isBlank(ticketReq0.getSeat())){
            LOG.info("本次购票没有选座");
            for (ConfirmOrderTicketReq ticket : tickets) {
                //遍历传进来需要买票的参数
                getSeat(finalSeatList,
                        date,
                        trainCode,
                        ticket.getSeatTypeCode(),
                        null,
                        null,
                        dailyTrainTicket.getStartIndex(),
                        dailyTrainTicket.getEndIndex()
                );
            }
        }else {
            LOG.info("本次购票有指定选座");
            List<SeatColEnum> colEnumList = SeatColEnum.getColsByType(ticketReq0.getSeatTypeCode());
            ArrayList<String> referSeatList = new ArrayList<>();
            //referSeatList = {A1, C1, D1, F1, A2, C2, D2. F2}
            for (int i = 1; i <= 2; i++) {
                for (SeatColEnum seatColEnum : colEnumList) {
                    referSeatList.add(seatColEnum.getCode()+i);
                }
            }
            LOG.info("用于作参照的两排座位：{}", referSeatList);
            LOG.info("本次选座选座包含的列为：{}",colEnumList.toString());
            ArrayList<Integer> absoOffsetList = new ArrayList<>();
            ArrayList<Integer> OffsetList = new ArrayList<>();
            for (ConfirmOrderTicketReq ticketReq : tickets) {
                int index = referSeatList.indexOf(ticketReq.getSeat());
                absoOffsetList.add(index);
            }
            LOG.info("本次选座绝对偏移：{}",absoOffsetList);
            for (Integer idx : absoOffsetList) {
                idx-=absoOffsetList.get(0);//得到绝对的
                OffsetList.add(idx);
            }
            LOG.info("本次选座相对的偏移：{}",OffsetList);
            //根据第0个座位查找座位,
            getSeat(finalSeatList,
                    date,
                    trainCode,
                    ticketReq0.getSeatTypeCode(),
                    ticketReq0.getSeat().split("")[0],//从A1得到A
                    OffsetList,
                    dailyTrainTicket.getStartIndex(),
                    dailyTrainTicket.getEndIndex()
            );

        }
        LOG.info("本次选座最终选座列表：");
        for (DailyTrainSeat seat : finalSeatList) {
            LOG.info("车厢号：「{}」，座位号：「{}」",seat.getCarriageIndex(),seat.getCarriageSeatIndex());
        }
        afterConfirmOrderService.afterDoConfirm(dailyTrainTicket,finalSeatList,tickets,confirmOrder);
        // 选好之后处理
        //修改售卖情况
        //增加售票记录
        //更新订单为成功
//            LOG.info("购票流程结束，释放锁");
//            redisTemplate.delete(key);

    }

    /**
     * 更新订单状态
     */
    public void updateStatus(ConfirmOrder confirmOrder) {
        ConfirmOrder confirmOrderForUpdate = new ConfirmOrder();
        confirmOrderForUpdate.setId(confirmOrder.getId());
        confirmOrderForUpdate.setUpdateTime(new Date());
        confirmOrderForUpdate.setStatus(confirmOrder.getStatus());
        confirmOrderMapper.updateByPrimaryKeySelective(confirmOrderForUpdate);
    }

    private void getSeat(List<DailyTrainSeat> finalSeatList,Date date,String trainCode,String seatType,String column, List<Integer> offsetList,Integer startIdx,Integer endIdx){
        List<DailyTrainSeat> getSeatList=new ArrayList<>();//选出一批座位
        //一个一个车厢的获取所有车的座位
        List<DailyTrainCarriage> dailyTrainCarriages = dailyTrainCarriageService.selectBySeatType(date, trainCode, seatType);
        LOG.info("共查出{}个符合条件的车厢",dailyTrainCarriages.size());
        for (DailyTrainCarriage carriage : dailyTrainCarriages) {
            LOG.info("******开始从车厢{}选座******", carriage.getIndex());
            getSeatList=new ArrayList<>();//重新开始一个车厢就开始重新保存座位列表
            //每个车厢的座位的列表
            List<DailyTrainSeat> seatList = dailyTrainSeatService.selectByCarriage(date, trainCode, carriage.getIndex());
            LOG.info("车厢[{}]的座位数：{}",carriage.getIndex(),seatList.size());
            for (int i = 0; i < seatList.size(); i++) {
                DailyTrainSeat dailyTrainSeat=seatList.get(i);
                Integer seatIndex = dailyTrainSeat.getCarriageSeatIndex();
                String curColumn = dailyTrainSeat.getCol();
                // 判断当前座位不能被选中过
                boolean alreadyChooseFlag = false;
                for (DailyTrainSeat seat : finalSeatList) {
                    if (seat.getId().equals(dailyTrainSeat.getId())){
                        alreadyChooseFlag=true;
                        break;
                    }
                }
                if (alreadyChooseFlag){
                    LOG.info("座位{}被选中过，不能重复选中，继续判断下一个座位", seatIndex);
                    continue;
                }
                //获取传进来的column，也就是乘客要选的，看看有无值
                if (StrUtil.isBlank(column)){
                    LOG.info("乘客未指定座位-column为空");
                }else {
                    if (!column.equals(curColumn)){
                        LOG.info("乘客指定座位-当前的column「{}」,不符合要求「{}」",curColumn,column);
                        continue;
                    }
                }
                boolean isChooose = callSell(dailyTrainSeat, startIdx, endIdx);
                if (isChooose){
                    LOG.info("选中座位");
                    getSeatList.add(dailyTrainSeat);//加入临时的选中的座位
                }else {
                    LOG.info("未选中座位");
                    continue;
                }
                //根据offset选择座位
                boolean isGetAllSeat = true;
                if (CollUtil.isNotEmpty(offsetList)){
                    LOG.info("存在乘客选择的偏移的列表「{}」，校验是否可选",offsetList);
                    for (int j = 1; j < offsetList.size(); j++) {
                        Integer offset = offsetList.get(j);
                        //下一个座位的偏移的量，也就是当前座位可选的时候看看offset后那个可以不
//                        int nextIdx = offset + seatIndex-1;
                        int nextIdx = i + offset;//选中当前的序列号j再加offse就可以
                        //判断车厢
                        if (nextIdx>=seatList.size()){
                            LOG.info("无法选座位「{}」，超出车厢长度",nextIdx);
                            isGetAllSeat=false;
                            break;
                        }

                        DailyTrainSeat nextSeat = seatList.get(nextIdx);
                        boolean isChoooseNext = callSell(nextSeat, startIdx, endIdx);
                        if (isChoooseNext){
                            LOG.info("选中座位「{}」",dailyTrainSeat);
                            getSeatList.add(nextSeat);
                        }else {
                            LOG.info("无法选座位「{}」",nextSeat);
                            isGetAllSeat=false;
                            break;
                        }
                    }
                }
                if (!isGetAllSeat) {
                    getSeatList = new ArrayList<>();
                    continue;
                }
                //保存选好的座位
                finalSeatList.addAll(getSeatList);
                return;
            }
        }
    }

    /**
     * 起始站是0，所以10001表示：第0~1卖出去了，第4~5卖出去了
     * 计算某座位在区间内是否可卖
     * 例：se11=10001 本次购买区间站1~4，则区间已售000
     * 全部是0，表示这个区间可买；只要有1，就表示区间内已售过票
     */
    private boolean callSell(DailyTrainSeat dailyTrainSeat, Integer startIndex, Integer endIndex) {
        // 00001, 00000
        String sell = dailyTrainSeat.getSell();
        //  000, 000
        String sellPart = sell.substring(startIndex, endIndex);
        if (Integer.parseInt(sellPart) > 0) {
            LOG.info("座位{}在本次车站区间{}~{}已售过票，不可选中该座位", dailyTrainSeat.getCarriageSeatIndex(), startIndex, endIndex);
            return false;
        } else {
            LOG.info("座位{}在本次车站区间{}~{}未售过票，可选中该座位", dailyTrainSeat.getCarriageSeatIndex(), startIndex, endIndex);
            //  111,   111
            String curSell = sellPart.replace('0', '1');
            // 0111,  0111
            curSell = StrUtil.fillBefore(curSell, '0', endIndex);
            // 01110, 01110
            curSell = StrUtil.fillAfter(curSell, '0', sell.length());

            // 当前区间售票信息curSell 01110与库里的已售信息sell 00001按位与，即可得到该座位卖出此票后的售票详情
            // 15(01111), 14(01110 = 01110|00000)
            int newSellInt = NumberUtil.binaryToInt(curSell) | NumberUtil.binaryToInt(sell);
            //  1111,  1110
            String newSell = NumberUtil.getBinaryStr(newSellInt);
            // 01111, 01110
            newSell = StrUtil.fillBefore(newSell, '0', sell.length());
            LOG.info("座位「{}」被选中，原售票信息：「{}」，车站区间：{}~{}，即：{}，最终售票信息：{}"
                    , dailyTrainSeat.getCarriageSeatIndex(), sell, startIndex, endIndex, curSell, newSell);
            dailyTrainSeat.setSell(newSell);
            return true;

        }
    }
    private static void reduceTicket(ConfirmOrderDoReq req, DailyTrainTicket dailyTrainTicket) {
        for (ConfirmOrderTicketReq ticketReq : req.getTickets()) {
            //循环请求的ticket
            String seatTypeCode = ticketReq.getSeatTypeCode();
            SeatTypeEnum seatTypeEnum = EnumUtil.getBy(SeatTypeEnum::getCode, seatTypeCode);
            switch (seatTypeEnum){
                case YDZ -> {
                    int countLeft = dailyTrainTicket.getYdz() - 1;
                    if (countLeft<0){
                        //异常
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setYdz(countLeft);
                }
                case EDZ -> {
                    int countLeft = dailyTrainTicket.getEdz() - 1;
                    if (countLeft<0){
                        //异常
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setEdz(countLeft);

                }
                case RW -> {
                    int countLeft = dailyTrainTicket.getRw() - 1;
                    if (countLeft<0){
                        //异常
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setRw(countLeft);

                }
                case YW -> {
                    int countLeft = dailyTrainTicket.getYw() - 1;
                    if (countLeft<0){
                        //异常
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setYw(countLeft);

                }
            }
        }
    }

    public PageResp<ConfirmOrderQueryResp> queryList(ConfirmOrderQueryReq req) {
        ConfirmOrderExample example = new ConfirmOrderExample();
        example.setOrderByClause("id desc");
        ConfirmOrderExample.Criteria criteria = example.createCriteria();
        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        //获取查询结果：
        List<ConfirmOrder> confirmOrderList = confirmOrderMapper.selectByExample(example);
        PageInfo<ConfirmOrder> pageInfo = new PageInfo<>(confirmOrderList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());
        List<ConfirmOrderQueryResp> list = BeanUtil.copyToList(confirmOrderList, ConfirmOrderQueryResp.class);
        PageResp<ConfirmOrderQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    public void delete(Long id) {
        confirmOrderMapper.deleteByPrimaryKey(id);
    }

    /**
     * 限流的处理方法
     * @param req
     * @param e
     */
    public void doConfirmBlock(ConfirmOrderDoReq req, BlockException e){
        LOG.info("《《《《购票请求被限制流量：「{}」》》》",req);
        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_FLOW_EXCEPTION);
    }
    //查询前面接个人在排队
    /**
     * 查询前面有几个人在排队
     * @param id
     */
    public Integer queryLineCount(Long id) {
        ConfirmOrder confirmOrder = confirmOrderMapper.selectByPrimaryKey(id);
        ConfirmOrderStatusEnum statusEnum = EnumUtil.getBy(ConfirmOrderStatusEnum::getCode, confirmOrder.getStatus());
        int result = switch (statusEnum) {
            case PENDING -> 0; // 排队0
            case SUCCESS -> -1; // 成功
            case FAILURE -> -2; // 失败
            case EMPTY -> -3; // 无票
            case CANCEL -> -4; // 取消
            case INIT -> 999; // 需要查表得到实际排队数量
        };

        if (result == 999) {
            // 排在第几位，下面的写法：where a=1 and (b=1 or c=1) 等价于 where (a=1 and b=1) or (a=1 and c=1)
            ConfirmOrderExample confirmOrderExample = new ConfirmOrderExample();
            confirmOrderExample.or().andDateEqualTo(confirmOrder.getDate())
                    .andTrainCodeEqualTo(confirmOrder.getTrainCode())
                    .andCreateTimeLessThan(confirmOrder.getCreateTime())
                    .andStatusEqualTo(ConfirmOrderStatusEnum.INIT.getCode());
            confirmOrderExample.or().andDateEqualTo(confirmOrder.getDate())
                    .andTrainCodeEqualTo(confirmOrder.getTrainCode())
                    .andCreateTimeLessThan(confirmOrder.getCreateTime())
                    .andStatusEqualTo(ConfirmOrderStatusEnum.PENDING.getCode());
            return Math.toIntExact(confirmOrderMapper.countByExample(confirmOrderExample));
        } else {
            return result;
        }
    }

    /**
     * 取消排队，只有I状态才能取消排队，所以按状态更新
     * @param id
     */
    public Integer cancel(Long id) {
        ConfirmOrderExample confirmOrderExample = new ConfirmOrderExample();
        ConfirmOrderExample.Criteria criteria = confirmOrderExample.createCriteria();
        criteria.andIdEqualTo(id).andStatusEqualTo(ConfirmOrderStatusEnum.INIT.getCode());
        ConfirmOrder confirmOrder = new ConfirmOrder();
        confirmOrder.setStatus(ConfirmOrderStatusEnum.CANCEL.getCode());
        return confirmOrderMapper.updateByExampleSelective(confirmOrder, confirmOrderExample);
    }
}
