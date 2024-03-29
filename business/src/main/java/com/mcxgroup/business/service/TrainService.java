package com.mcxgroup.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mcxgroup.business.domain.TrainStation;
import com.mcxgroup.business.domain.TrainStationExample;
import com.mcxgroup.common.context.LoginMemberContext;
import com.mcxgroup.common.exception.BusinessException;
import com.mcxgroup.common.exception.BusinessExceptionEnum;
import com.mcxgroup.common.resp.PageResp;
import com.mcxgroup.common.util.SnowUtil;
import com.mcxgroup.business.domain.Train;
import com.mcxgroup.business.domain.TrainExample;
import com.mcxgroup.business.mapper.TrainMapper;
import com.mcxgroup.business.req.TrainQueryReq;
import com.mcxgroup.business.req.TrainSaveReq;
import com.mcxgroup.business.resp.TrainQueryResp;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author MCXEN
 */
@Service
public class TrainService {
    private static final Logger LOG = LoggerFactory.getLogger(TrainService.class);

    @Resource
    private TrainMapper trainMapper;

    public void save(TrainSaveReq req) {
        DateTime now = DateTime.now();
        Train train = BeanUtil.copyProperties(req, Train.class);
        if (ObjectUtil.isNull(train.getId())) {
            Train selectByUniqueCode = selectByUnique(req.getCode());
            if (ObjectUtil.isNotEmpty(selectByUniqueCode)){
                throw new BusinessException(BusinessExceptionEnum.BUSINESS_TRAIN_NAME_UNIQUE);
            }
            train.setId(SnowUtil.getSnowflakeNextId());
            train.setCreateTime(now);
            train.setUpdateTime(now);
            trainMapper.insert(train);
        } else {
            train.setUpdateTime(now);
            trainMapper.updateByPrimaryKey(train);
        }
    }

    private Train selectByUnique(String code) {
        TrainExample example = new TrainExample();
        TrainExample.Criteria criteria = example.createCriteria();
        criteria.andCodeEqualTo(code);
        List<Train> list = trainMapper.selectByExample(example);
        if (CollUtil.isNotEmpty(list)){
            return  list.get(0);
        }
        return null;
    }

    public PageResp<TrainQueryResp> queryList(TrainQueryReq req) {
        TrainExample example = new TrainExample();
        example.setOrderByClause("id desc");
        TrainExample.Criteria criteria = example.createCriteria();
        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Train> trainList = trainMapper.selectByExample(example);

        PageInfo<Train> pageInfo = new PageInfo<>(trainList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        List<TrainQueryResp> list = BeanUtil.copyToList(trainList, TrainQueryResp.class);

        PageResp<TrainQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    /**
     * 查询所有的车次
     * @param
     * @return
     */
    public List<TrainQueryResp> queryAll() {
        List<Train> trainList = selectAll();
        return BeanUtil.copyToList(trainList, TrainQueryResp.class);
    }

    public List<Train> selectAll() {
        TrainExample example = new TrainExample();
        example.setOrderByClause("code asc");
        TrainExample.Criteria criteria = example.createCriteria();
        List<Train> trainList = trainMapper.selectByExample(example);
        return trainList;
    }

    public void delete(Long id) {
        trainMapper.deleteByPrimaryKey(id);
    }
}
