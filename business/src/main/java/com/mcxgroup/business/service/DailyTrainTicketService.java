package com.mcxgroup.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mcxgroup.business.domain.*;
import com.mcxgroup.business.enums.SeatTypeEnum;
import com.mcxgroup.business.enums.TrainTypeEnum;
import com.mcxgroup.common.context.LoginMemberContext;
import com.mcxgroup.common.resp.PageResp;
import com.mcxgroup.common.util.SnowUtil;
import com.mcxgroup.business.mapper.DailyTrainTicketMapper;
import com.mcxgroup.business.req.DailyTrainTicketQueryReq;
import com.mcxgroup.business.req.DailyTrainTicketSaveReq;
import com.mcxgroup.business.resp.DailyTrainTicketQueryResp;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

/**
 * @author MCXEN
 */
@Service
public class DailyTrainTicketService {
    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainTicketService.class);

    @Resource
    private DailyTrainTicketMapper dailyTrainTicketMapper;
    @Resource
    private TrainStationService trainStationService;
    @Resource
    private DailyTrainSeatService dailyTrainSeatService;
    public void save(DailyTrainTicketSaveReq req) {
        DateTime now = DateTime.now();
        DailyTrainTicket dailyTrainTicket = BeanUtil.copyProperties(req, DailyTrainTicket.class);
        if (ObjectUtil.isNull(dailyTrainTicket.getId())) {
            dailyTrainTicket.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainTicket.setCreateTime(now);
            dailyTrainTicket.setUpdateTime(now);
            dailyTrainTicketMapper.insert(dailyTrainTicket);
        } else {
            dailyTrainTicket.setUpdateTime(now);
            dailyTrainTicketMapper.updateByPrimaryKey(dailyTrainTicket);
        }
    }

    public PageResp<DailyTrainTicketQueryResp> queryList(DailyTrainTicketQueryReq req) {
        DailyTrainTicketExample example = new DailyTrainTicketExample();
        example.setOrderByClause("id desc");
        DailyTrainTicketExample.Criteria criteria = example.createCriteria();
        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        //确保加入的查询的条件生效
        if (ObjectUtil.isNotEmpty(req.getDate())) criteria.andDateEqualTo(req.getDate());
        if (ObjectUtil.isNotEmpty(req.getTrainCode())) criteria.andTrainCodeEqualTo(req.getTrainCode());
        if (ObjectUtil.isNotEmpty(req.getStart())) criteria.andStartEqualTo(req.getStart());
        if (ObjectUtil.isNotEmpty(req.getEnd())) criteria.andEndEqualTo(req.getEnd());
        //查询结构：
        List<DailyTrainTicket> dailyTrainTicketList = dailyTrainTicketMapper.selectByExample(example);
        PageInfo<DailyTrainTicket> pageInfo = new PageInfo<>(dailyTrainTicketList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());
        List<DailyTrainTicketQueryResp> list = BeanUtil.copyToList(dailyTrainTicketList, DailyTrainTicketQueryResp.class);
        PageResp<DailyTrainTicketQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    public void delete(Long id) {
        dailyTrainTicketMapper.deleteByPrimaryKey(id);
    }

    public void genDaily(DailyTrain dailyTrain,Date date, String trainCode) {
        String taskName = "'车票'";
        LOG.info("生成每日车次车厢，日期【{}】车次【{}】的{}信息开始", DateUtil.formatDate(date), trainCode,taskName);
        //删除车票
        DailyTrainTicketExample dailyTrainTicketExample = new DailyTrainTicketExample();
        dailyTrainTicketExample.createCriteria()
                .andDateEqualTo(date)
                .andTrainCodeEqualTo(trainCode);
        dailyTrainTicketMapper.deleteByExample(dailyTrainTicketExample);

    //查出车次车站的信息
        List<TrainStation> stationList = trainStationService.selectByTrainCode(trainCode);
        if (CollUtil.isEmpty(stationList)){
            LOG.info("没有车次车站的数据，生成{}的任务结束",taskName);
            return;
        }
        DateTime now = DateTime.now();
        //单向的潜逃的循环
        for (int i = 0; i < stationList.size(); i++) {
            //得到出发的站
            TrainStation start = stationList.get(i);
            BigDecimal sumKm = BigDecimal.ZERO;
            for (int j = i+1; j < stationList.size(); j++) {
                TrainStation trainStationEnd = stationList.get(j);
                sumKm = sumKm.add(trainStationEnd.getKm());
                DailyTrainTicket dailyTrainTicket = new DailyTrainTicket();
                dailyTrainTicket.setId(SnowUtil.getSnowflakeNextId());
                dailyTrainTicket.setDate(date);
                dailyTrainTicket.setTrainCode(trainCode);
                //起始站的信息
                dailyTrainTicket.setStart(start.getName());
                dailyTrainTicket.setStartPinyin(start.getNamePinyin());
                dailyTrainTicket.setStartTime(start.getOutTime());
                dailyTrainTicket.setStartIndex(start.getIndex());
                //终点站的信息
                dailyTrainTicket.setEnd(trainStationEnd.getName());
                dailyTrainTicket.setEndPinyin(trainStationEnd.getNamePinyin());
                dailyTrainTicket.setEndTime(trainStationEnd.getInTime());
                dailyTrainTicket.setEndIndex(trainStationEnd.getIndex());
                String dailyTrainType = dailyTrain.getType();
                /**
                 * 可以For循环遍历枚举类型，然后确定价格倍率
                 * getCode 是 TrainTypeEnum 枚举中的另一个方法，用于获取火车类型的代码。
                 * dailyTrainType 是一个变量，表示每日选择的火车类型。
                 * 该行代码的作用是从 TrainTypeEnum 枚举中获取指定火车类型（dailyTrainType）的价格倍率（priceRate）
                 */
                BigDecimal typePrice = EnumUtil.getFieldBy(TrainTypeEnum::getPriceRate, TrainTypeEnum::getCode, dailyTrainType);
                BigDecimal YDZPrice = sumKm.multiply(SeatTypeEnum.YDZ.getPrice()).multiply(typePrice).setScale(2, RoundingMode.HALF_UP);
                BigDecimal EDZPrice = sumKm.multiply(SeatTypeEnum.EDZ.getPrice()).multiply(typePrice).setScale(2, RoundingMode.HALF_UP);
                BigDecimal RWPrice = sumKm.multiply(SeatTypeEnum.RW.getPrice()).multiply(typePrice).setScale(2, RoundingMode.HALF_UP);
                BigDecimal YWPrice = sumKm.multiply(SeatTypeEnum.YW.getPrice()).multiply(typePrice).setScale(2, RoundingMode.HALF_UP);
                dailyTrainTicket.setYdz(dailyTrainSeatService.countSeat(date,trainCode, SeatTypeEnum.YDZ.getCode()));
                dailyTrainTicket.setYdzPrice(YDZPrice);
                dailyTrainTicket.setEdz(dailyTrainSeatService.countSeat(date,trainCode, SeatTypeEnum.EDZ.getCode()));
                dailyTrainTicket.setEdzPrice(EDZPrice);
                dailyTrainTicket.setRw(dailyTrainSeatService.countSeat(date,trainCode, SeatTypeEnum.RW.getCode()));
                dailyTrainTicket.setRwPrice(RWPrice);
                dailyTrainTicket.setYw(dailyTrainSeatService.countSeat(date,trainCode, SeatTypeEnum.YW.getCode()));
                dailyTrainTicket.setYwPrice(YWPrice);
                dailyTrainTicket.setCreateTime(now);
                dailyTrainTicket.setUpdateTime(now);
                //保存到数据库
                dailyTrainTicketMapper.insert(dailyTrainTicket);
            }
        }
        LOG.info("生成每日车次车厢，日期【{}】车次【{}】的{}信息结束", DateUtil.formatDate(date), trainCode,taskName);

    }
}
