package com.mcxgroup.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mcxgroup.business.domain.*;
import com.mcxgroup.business.enums.ConfirmOrderStatusEnum;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    public void doConfirm(ConfirmOrderDoReq req) {
        //省略业务校验
        //保存确认表格
        DateTime now = DateTime.now();
        Date date = req.getDate();
        String start = req.getStart();
        String end = req.getEnd();
        String trainCode = req.getTrainCode();
        ConfirmOrder confirmOrder = BeanUtil.copyProperties(req, ConfirmOrder.class);
        confirmOrder.setId(SnowUtil.getSnowflakeNextId());
        confirmOrder.setCreateTime(now);
        confirmOrder.setUpdateTime(now);
        confirmOrder.setMemberId(LoginMemberContext.getId());
        confirmOrder.setStatus(ConfirmOrderStatusEnum.INIT.getCode());
        List<ConfirmOrderTicketReq> tickets = req.getTickets();
        confirmOrder.setTickets(JSON.toJSONString(tickets));
        confirmOrderMapper.insert(confirmOrder);
        //查询每日车票表
        DailyTrainTicket dailyTrainTicket = dailyTrainTicketService.selectByUnique(date, trainCode, start, end);
        LOG.info("查出来余票的记录：{}",dailyTrainTicket);
        //反射的性能不如直接switch
        //扣减余票记录，判断是否充足
        reduceTicket(req, dailyTrainTicket);
        //计算偏移的值，
        // 比如选择的是C1，D2，则偏移值是：[0.5]
        // 比如选择的是A1,B1,C1，则偏移值是：[0,1,2]
        ConfirmOrderTicketReq ticketReq0 = tickets.get(0);
        if (StrUtil.isBlank(ticketReq0.getSeat())){
            getSeat(date,
                    trainCode,
                    ticketReq0.getSeatTypeCode(),
                    null,
                    null,
                    dailyTrainTicket.getStartIndex(),
                    dailyTrainTicket.getEndIndex()
            );
            LOG.info("本次选座没有玄");
        }else {
            List<SeatColEnum> colEnumList = SeatColEnum.getColsByType(ticketReq0.getSeatTypeCode());
            ArrayList<String> referSeatList = new ArrayList<>();
            //referSeatList = {A1, C1, D1, F1, A2, C2, D2. F2}
            for (int i = 1; i < 2; i++) {
                for (SeatColEnum seatColEnum : colEnumList) {
                    referSeatList.add(seatColEnum.getCode()+i);
                }
            }
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
            getSeat(date,
                    trainCode,
                    ticketReq0.getSeatTypeCode(),
                    ticketReq0.getSeat().split("")[0],//从A1得到A
                    OffsetList,
                    dailyTrainTicket.getStartIndex(),
                    dailyTrainTicket.getEndIndex()
            );

            LOG.info("本次选座选座了");
        }
        //选座
            //一个一个车厢的获取所有车的座位
            //挑选符合条件的座位

        // 选好之后处理
            //修改售卖情况
            //增加售票记录
            //更新订单为成功

    }
    private void getSeat(Date date,String trainCode,String seatType,String column, List<Integer> offsetList,Integer startIdx,Integer endIdx){
        //一个一个车厢的获取所有车的座位
        List<DailyTrainCarriage> dailyTrainCarriages = dailyTrainCarriageService.selectBySeatType(date, trainCode, seatType);
        LOG.info("共查出{}个符合条件的车厢",dailyTrainCarriages.size());
        for (DailyTrainCarriage carriage : dailyTrainCarriages) {
            //每个车厢的座位的列表
            List<DailyTrainSeat> seatList = dailyTrainSeatService.selectByCarriage(date, trainCode, carriage.getIndex());
            LOG.info("车厢[{}]的座位数：{}",carriage.getIndex(),seatList.size());
            for (DailyTrainSeat dailyTrainSeat : seatList) {
                boolean isChooose = callSell(dailyTrainSeat, startIdx, endIdx);
                if (isChooose){
                    LOG.info("选中座位");
                    return;
                }else {
                    LOG.info("未选中座位");
                    continue;
                }
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
}
