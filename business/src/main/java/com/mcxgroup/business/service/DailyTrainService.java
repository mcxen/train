package com.mcxgroup.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mcxgroup.common.context.LoginMemberContext;
import com.mcxgroup.common.resp.PageResp;
import com.mcxgroup.common.util.SnowUtil;
import com.mcxgroup.business.domain.DailyTrain;
import com.mcxgroup.business.domain.DailyTrainExample;
import com.mcxgroup.business.mapper.DailyTrainMapper;
import com.mcxgroup.business.req.DailyTrainQueryReq;
import com.mcxgroup.business.req.DailyTrainSaveReq;
import com.mcxgroup.business.resp.DailyTrainQueryResp;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author johnconstantine
 */
@Service
public class DailyTrainService {
    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainService.class);

    @Resource
    private DailyTrainMapper dailyTrainMapper;

    public void save(DailyTrainSaveReq req) {
        DateTime now = DateTime.now();
        DailyTrain dailyTrain = BeanUtil.copyProperties(req, DailyTrain.class);
        if (ObjectUtil.isNull(dailyTrain.getId())) {
            dailyTrain.setId(SnowUtil.getSnowflakeNextId());
            dailyTrain.setCreateTime(now);
            dailyTrain.setUpdateTime(now);
            dailyTrainMapper.insert(dailyTrain);
        } else {
            dailyTrain.setUpdateTime(now);
            dailyTrainMapper.updateByPrimaryKey(dailyTrain);
        }
    }

    public PageResp<DailyTrainQueryResp> queryList(DailyTrainQueryReq req) {
        DailyTrainExample example = new DailyTrainExample();
        example.setOrderByClause("date desc,code asc");
        DailyTrainExample.Criteria criteria = example.createCriteria();
        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        if (ObjectUtil.isNotEmpty(req.getDate())){
            //如果trainCode有传进来的树枝就按照传进来的查询，没有的话就不加这个条件
            criteria.andDateEqualTo(req.getDate());
//            System.out.println("req.getTrainCode() = " + req.getTrainCode());
        }

        if (ObjectUtil.isNotEmpty(req.getCode())){
            //如果trainCode有传进来的树枝就按照传进来的查询，没有的话就不加这个条件
            criteria.andCodeEqualTo(req.getCode());
//            System.out.println("req.getTrainCode() = " + req.getTrainCode());
        }

        List<DailyTrain> dailyTrainList = dailyTrainMapper.selectByExample(example);

        PageInfo<DailyTrain> pageInfo = new PageInfo<>(dailyTrainList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        List<DailyTrainQueryResp> list = BeanUtil.copyToList(dailyTrainList, DailyTrainQueryResp.class);

        PageResp<DailyTrainQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    public void delete(Long id) {
        dailyTrainMapper.deleteByPrimaryKey(id);
    }
}
