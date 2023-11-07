package com.mcxgroup.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mcxgroup.business.domain.TrainCarriage;
import com.mcxgroup.business.domain.TrainCarriageExample;
import com.mcxgroup.common.context.LoginMemberContext;
import com.mcxgroup.common.exception.BusinessException;
import com.mcxgroup.common.exception.BusinessExceptionEnum;
import com.mcxgroup.common.resp.PageResp;
import com.mcxgroup.common.util.SnowUtil;
import com.mcxgroup.business.domain.TrainStation;
import com.mcxgroup.business.domain.TrainStationExample;
import com.mcxgroup.business.mapper.TrainStationMapper;
import com.mcxgroup.business.req.TrainStationQueryReq;
import com.mcxgroup.business.req.TrainStationSaveReq;
import com.mcxgroup.business.resp.TrainStationQueryResp;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author MCXEN
 */
@Service
public class TrainStationService {
    private static final Logger LOG = LoggerFactory.getLogger(TrainStationService.class);

    @Resource
    private TrainStationMapper trainStationMapper;

    public void save(TrainStationSaveReq req) {
        DateTime now = DateTime.now();
        TrainStation trainStation = BeanUtil.copyProperties(req, TrainStation.class);
        if (ObjectUtil.isNull(trainStation.getId())) {
            TrainStation selectByUniqueName = selectByUnique(req.getTrainCode(),req.getName());
            if (ObjectUtil.isNotEmpty(selectByUniqueName)){
                throw new BusinessException(BusinessExceptionEnum.BUSINESS_TRAIN_STATION_NAME_UNIQUE);
            }
            TrainStation selectByUniqueIndex = selectByUnique(req.getTrainCode(),req.getIndex());
            if (ObjectUtil.isNotEmpty(selectByUniqueIndex)){
                throw new BusinessException(BusinessExceptionEnum.BUSINESS_TRAIN_STATION_INDEX_UNIQUE);
            }
            trainStation.setId(SnowUtil.getSnowflakeNextId());
            trainStation.setCreateTime(now);
            trainStation.setUpdateTime(now);
            trainStationMapper.insert(trainStation);
        } else {
            trainStation.setUpdateTime(now);
            trainStationMapper.updateByPrimaryKey(trainStation);
        }
    }

    private TrainStation selectByUnique(String trainCode, Integer index) {
        TrainStationExample example = new TrainStationExample();
        TrainStationExample.Criteria criteria = example.createCriteria();
        criteria.andTrainCodeEqualTo(trainCode);
        criteria.andIndexEqualTo(index);
        List<TrainStation> list = trainStationMapper.selectByExample(example);
        if (CollUtil.isNotEmpty(list)){
            return  list.get(0);
        }
        return null;
    }
    private TrainStation selectByUnique(String trainCode, String name) {
        TrainStationExample example = new TrainStationExample();
        TrainStationExample.Criteria criteria = example.createCriteria();
        criteria.andTrainCodeEqualTo(trainCode);
        criteria.andNameEqualTo(name);
        List<TrainStation> list = trainStationMapper.selectByExample(example);
        if (CollUtil.isNotEmpty(list)){
            return  list.get(0);
        }
        return null;
    }
    public List<TrainStation>  selectByTrainCode(String trainCode) {
        TrainStationExample example = new TrainStationExample();
        TrainStationExample.Criteria criteria = example.createCriteria();
        example.setOrderByClause("`index` asc");
        criteria.andTrainCodeEqualTo(trainCode);
        List<TrainStation> list = trainStationMapper.selectByExample(example);
        return list;
    }

    public PageResp<TrainStationQueryResp> queryList(TrainStationQueryReq req) {
        TrainStationExample example = new TrainStationExample();
        example.setOrderByClause("train_code asc,`index` asc");
        TrainStationExample.Criteria criteria = example.createCriteria();
        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());

        if (ObjectUtil.isNotEmpty(req.getTrainCode())){
            //如果trainCode有传进来的树枝就按照传进来的查询，没有的话就不加这个条件
            criteria.andTrainCodeEqualTo(req.getTrainCode());
//            System.out.println("req.getTrainCode() = " + req.getTrainCode());
        }
        List<TrainStation> trainStationList = trainStationMapper.selectByExample(example);
        PageInfo<TrainStation> pageInfo = new PageInfo<>(trainStationList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        List<TrainStationQueryResp> list = BeanUtil.copyToList(trainStationList, TrainStationQueryResp.class);

        PageResp<TrainStationQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    public void delete(Long id) {
        trainStationMapper.deleteByPrimaryKey(id);
    }
}
