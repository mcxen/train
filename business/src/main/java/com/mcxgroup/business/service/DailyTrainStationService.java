package com.mcxgroup.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mcxgroup.business.domain.*;
import com.mcxgroup.common.context.LoginMemberContext;
import com.mcxgroup.common.resp.PageResp;
import com.mcxgroup.common.util.SnowUtil;
import com.mcxgroup.business.mapper.DailyTrainStationMapper;
import com.mcxgroup.business.req.DailyTrainStationQueryReq;
import com.mcxgroup.business.req.DailyTrainStationSaveReq;
import com.mcxgroup.business.resp.DailyTrainStationQueryResp;
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
public class DailyTrainStationService {
    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainStationService.class);

    @Resource
    private DailyTrainStationMapper dailyTrainStationMapper;
    @Resource
    private TrainStationService trainStationService;

    public void save(DailyTrainStationSaveReq req) {
        DateTime now = DateTime.now();
        DailyTrainStation dailyTrainStation = BeanUtil.copyProperties(req, DailyTrainStation.class);
        if (ObjectUtil.isNull(dailyTrainStation.getId())) {
            dailyTrainStation.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainStation.setCreateTime(now);
            dailyTrainStation.setUpdateTime(now);
            dailyTrainStationMapper.insert(dailyTrainStation);
        } else {
            dailyTrainStation.setUpdateTime(now);
            dailyTrainStationMapper.updateByPrimaryKey(dailyTrainStation);
        }
    }

    public PageResp<DailyTrainStationQueryResp> queryList(DailyTrainStationQueryReq req) {
        DailyTrainStationExample example = new DailyTrainStationExample();
        example.setOrderByClause("date desc,train_code asc,`index` asc");
        DailyTrainStationExample.Criteria criteria = example.createCriteria();
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
        List<DailyTrainStation> dailyTrainStationList = dailyTrainStationMapper.selectByExample(example);

        PageInfo<DailyTrainStation> pageInfo = new PageInfo<>(dailyTrainStationList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        List<DailyTrainStationQueryResp> list = BeanUtil.copyToList(dailyTrainStationList, DailyTrainStationQueryResp.class);

        PageResp<DailyTrainStationQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    public void delete(Long id) {
        dailyTrainStationMapper.deleteByPrimaryKey(id);
    }
    public void genDaily(Date date,String trainCode) {
        LOG.info("生成日期【{}】车次【{}】的信息开始", DateUtil.formatDate(date), trainCode);
        //删除车次的信息
        // 删除该车次已有的数据
        // 删除该车次已有的数据
        DailyTrainStationExample dailyTrainStationExample = new DailyTrainStationExample();
        dailyTrainStationExample.createCriteria()
                .andDateEqualTo(date)
                .andTrainCodeEqualTo(trainCode);
        dailyTrainStationMapper.deleteByExample(dailyTrainStationExample);
        //查出车次车站的信息
        List<TrainStation> stationList = trainStationService.selectByTrainCode(trainCode);
        if (CollUtil.isEmpty(stationList)){
            LOG.info("没有车次车站的数据，生成的任务结束");
            return;
        }
        for (TrainStation trainStation : stationList) {
            // 生成该车次车站的数据
            DateTime now = DateTime.now();
            DailyTrainStation dailyTrainStation = BeanUtil.copyProperties(trainStation, DailyTrainStation.class);
            dailyTrainStation.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainStation.setCreateTime(now);
            dailyTrainStation.setUpdateTime(now);
            dailyTrainStation.setDate(date);
            dailyTrainStationMapper.insert(dailyTrainStation);
        }
    }
}
