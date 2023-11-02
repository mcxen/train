package com.mcxgroup.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mcxgroup.business.domain.*;
import com.mcxgroup.business.enums.SeatColEnum;
import com.mcxgroup.common.context.LoginMemberContext;
import com.mcxgroup.common.resp.PageResp;
import com.mcxgroup.common.util.SnowUtil;
import com.mcxgroup.business.mapper.DailyTrainCarriageMapper;
import com.mcxgroup.business.req.DailyTrainCarriageQueryReq;
import com.mcxgroup.business.req.DailyTrainCarriageSaveReq;
import com.mcxgroup.business.resp.DailyTrainCarriageQueryResp;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author johnconstantine
 */
@Service
public class DailyTrainCarriageService {
    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainCarriageService.class);

    @Resource
    private DailyTrainCarriageMapper dailyTrainCarriageMapper;
    @Resource
    TrainCarriageService trainCarriageService;

    public void save(DailyTrainCarriageSaveReq req) {
        DateTime now = DateTime.now();
        List<SeatColEnum> seatColEnums = SeatColEnum.getColsByType(req.getSeatType());
        req.setColCount(seatColEnums.size());
        req.setSeatCount(req.getColCount()*req.getRowCount());//行数乘以列数
        DailyTrainCarriage dailyTrainCarriage = BeanUtil.copyProperties(req, DailyTrainCarriage.class);
        if (ObjectUtil.isNull(dailyTrainCarriage.getId())) {
            dailyTrainCarriage.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainCarriage.setCreateTime(now);
            dailyTrainCarriage.setUpdateTime(now);
            dailyTrainCarriageMapper.insert(dailyTrainCarriage);
        } else {
            dailyTrainCarriage.setUpdateTime(now);
            dailyTrainCarriageMapper.updateByPrimaryKey(dailyTrainCarriage);
        }
    }

    public PageResp<DailyTrainCarriageQueryResp> queryList(DailyTrainCarriageQueryReq req) {
        DailyTrainCarriageExample example = new DailyTrainCarriageExample();
        example.setOrderByClause("date desc, train_code asc, `index` asc");
        DailyTrainCarriageExample.Criteria criteria = example.createCriteria();
        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        if (ObjectUtil.isNotEmpty(req.getDate())){
            //如果trainCode有传进来的树枝就按照传进来的查询，没有的话就不加这个条件
            criteria.andDateEqualTo(req.getDate());
//            System.out.println("req.getTrainCode() = " + req.getTrainCode());
        }

        if (ObjectUtil.isNotEmpty(req.getTrainCode())){
            //如果trainCode有传进来的树枝就按照传进来的查询，没有的话就不加这个条件
            criteria.andTrainCodeEqualTo(req.getTrainCode());
//            System.out.println("req.getTrainCode() = " + req.getTrainCode());
        }
        List<DailyTrainCarriage> dailyTrainCarriageList = dailyTrainCarriageMapper.selectByExample(example);

        PageInfo<DailyTrainCarriage> pageInfo = new PageInfo<>(dailyTrainCarriageList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        List<DailyTrainCarriageQueryResp> list = BeanUtil.copyToList(dailyTrainCarriageList, DailyTrainCarriageQueryResp.class);

        PageResp<DailyTrainCarriageQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    public void delete(Long id) {
        dailyTrainCarriageMapper.deleteByPrimaryKey(id);
    }
    public void genDaily(Date date, String trainCode) {
        LOG.info("生成每日车次车厢，日期【{}】车次【{}】的信息开始", DateUtil.formatDate(date), trainCode);
        //删除车次的信息
        // 删除该车次已有的数据
        // 删除该车次已有的数据
        DailyTrainCarriageExample dailyTrainCarriageExample = new DailyTrainCarriageExample();
        dailyTrainCarriageExample.createCriteria()
                .andDateEqualTo(date)
                .andTrainCodeEqualTo(trainCode);
        dailyTrainCarriageMapper.deleteByExample(dailyTrainCarriageExample);
        //查出车次车站的信息
        List<TrainCarriage> stationList = trainCarriageService.selectByTrainCode(trainCode);
        if (CollUtil.isEmpty(stationList)){
            LOG.info("没有车次车厢的数据，生成的任务结束");
            return;
        }
        for (TrainCarriage trainCarriage : stationList) {
            // 生成该车次车站的数据
            DateTime now = DateTime.now();
            DailyTrainCarriage dailyTrainCarriage = BeanUtil.copyProperties(trainCarriage, DailyTrainCarriage.class);
            dailyTrainCarriage.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainCarriage.setCreateTime(now);
            dailyTrainCarriage.setUpdateTime(now);
            dailyTrainCarriage.setDate(date);
            dailyTrainCarriageMapper.insert(dailyTrainCarriage);
        }
        LOG.info("生成每日车次车厢，日期【{}】车次【{}】的信息结束", DateUtil.formatDate(date), trainCode);

    }
}
