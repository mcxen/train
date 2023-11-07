package com.mcxgroup.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mcxgroup.common.exception.BusinessException;
import com.mcxgroup.common.exception.BusinessExceptionEnum;
import com.mcxgroup.common.resp.PageResp;
import com.mcxgroup.common.util.SnowUtil;
import com.mcxgroup.business.domain.Station;
import com.mcxgroup.business.domain.StationExample;
import com.mcxgroup.business.mapper.StationMapper;
import com.mcxgroup.business.req.StationQueryReq;
import com.mcxgroup.business.req.StationSaveReq;
import com.mcxgroup.business.resp.StationQueryResp;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author MCXEN
 */
@Service
public class StationService {
    private static final Logger LOG = LoggerFactory.getLogger(StationService.class);

    @Resource
    private StationMapper stationMapper;

    public void save(StationSaveReq req) {
        DateTime now = DateTime.now();
        Station station = BeanUtil.copyProperties(req, Station.class);
        if (ObjectUtil.isNull(station.getId())) {
            Station selectByUnique = selectByUnique(req.getName());
            if (ObjectUtil.isNotEmpty(selectByUnique)){
                throw new BusinessException(BusinessExceptionEnum.BUSINESS_STATION_NAME_UNIQUE);
            }
            station.setId(SnowUtil.getSnowflakeNextId());
            station.setCreateTime(now);
            station.setUpdateTime(now);
            stationMapper.insert(station);
        } else {
            station.setUpdateTime(now);
            stationMapper.updateByPrimaryKey(station);
        }
    }

    private Station selectByUnique(String name) {
        StationExample example = new StationExample();
        StationExample.Criteria criteria = example.createCriteria();
        criteria.andNameEqualTo(name);
        List<Station> list = stationMapper.selectByExample(example);
        if (CollUtil.isNotEmpty(list)){
            return  list.get(0);
        }
        return null;
    }

    public PageResp<StationQueryResp> queryList(StationQueryReq req) {
        StationExample example = new StationExample();
        example.setOrderByClause("id desc");
        StationExample.Criteria criteria = example.createCriteria();
        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Station> stationList = stationMapper.selectByExample(example);

        PageInfo<Station> pageInfo = new PageInfo<>(stationList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        List<StationQueryResp> list = BeanUtil.copyToList(stationList, StationQueryResp.class);

        PageResp<StationQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    public List<StationQueryResp> queryAll() {
        StationExample example = new StationExample();
        example.setOrderByClause("name_pinyin asc");
        List<Station> list = stationMapper.selectByExample(example);
        return BeanUtil.copyToList(list, StationQueryResp.class);
    }
    public void delete(Long id) {
        stationMapper.deleteByPrimaryKey(id);
    }
}
