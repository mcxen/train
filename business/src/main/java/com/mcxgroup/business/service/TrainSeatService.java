package com.mcxgroup.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mcxgroup.business.domain.TrainCarriage;
import com.mcxgroup.business.domain.TrainCarriageExample;
import com.mcxgroup.business.enums.SeatColEnum;
import com.mcxgroup.business.mapper.TrainCarriageMapper;
import com.mcxgroup.common.context.LoginMemberContext;
import com.mcxgroup.common.resp.PageResp;
import com.mcxgroup.common.util.SnowUtil;
import com.mcxgroup.business.domain.TrainSeat;
import com.mcxgroup.business.domain.TrainSeatExample;
import com.mcxgroup.business.mapper.TrainSeatMapper;
import com.mcxgroup.business.req.TrainSeatQueryReq;
import com.mcxgroup.business.req.TrainSeatSaveReq;
import com.mcxgroup.business.resp.TrainSeatQueryResp;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author MCXEN
 */
@Service
public class TrainSeatService {
    private static final Logger LOG = LoggerFactory.getLogger(TrainSeatService.class);

    @Resource
    private TrainSeatMapper trainSeatMapper;
    @Resource
    private TrainCarriageService trainCarriageService;

    public void save(TrainSeatSaveReq req) {
        DateTime now = DateTime.now();
        TrainSeat trainSeat = BeanUtil.copyProperties(req, TrainSeat.class);
        if (ObjectUtil.isNull(trainSeat.getId())) {
            trainSeat.setId(SnowUtil.getSnowflakeNextId());
            trainSeat.setCreateTime(now);
            trainSeat.setUpdateTime(now);
            trainSeatMapper.insert(trainSeat);
        } else {
            trainSeat.setUpdateTime(now);
            trainSeatMapper.updateByPrimaryKey(trainSeat);
        }
    }

    public PageResp<TrainSeatQueryResp> queryList(TrainSeatQueryReq req) {
        TrainSeatExample example = new TrainSeatExample();
        example.setOrderByClause("train_code asc, carriage_index asc, carriage_seat_index asc");
        TrainSeatExample.Criteria criteria = example.createCriteria();
        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        if (ObjectUtil.isNotEmpty(req.getTrainCode())){
            //如果trainCode有传进来的树枝就按照传进来的查询，没有的话就不加这个条件
            criteria.andTrainCodeEqualTo(req.getTrainCode());
//            System.out.println("req.getTrainCode() = " + req.getTrainCode());
        }
        List<TrainSeat> trainSeatList = trainSeatMapper.selectByExample(example);
        PageInfo<TrainSeat> pageInfo = new PageInfo<>(trainSeatList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        List<TrainSeatQueryResp> list = BeanUtil.copyToList(trainSeatList, TrainSeatQueryResp.class);

        PageResp<TrainSeatQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }
    public List<TrainSeat> selectByTrainCode(String trainCode){
        /**
         * 对于一些简单的SQL查询，可以直接使用Example来构造查询条件；
         * 而对于一些相对复杂的查询，则需要使用Criteria进行细粒度的约束设置。
         */
        TrainSeatExample example = new TrainSeatExample();
        TrainSeatExample.Criteria criteria = example.createCriteria();
        example.setOrderByClause("`id` asc");
        criteria.andTrainCodeEqualTo(trainCode);
        List<TrainSeat> list = trainSeatMapper.selectByExample(example);
        return list;
    }

    public void delete(Long id) {
        trainSeatMapper.deleteByPrimaryKey(id);
    }

    @Transactional
    public void genTrainSeat(String trainCode){
        DateTime now = DateTime.now();
        TrainSeatExample example = new TrainSeatExample();
        TrainSeatExample.Criteria criteria = example.createCriteria();
        criteria.andTrainCodeEqualTo(trainCode);
        // 先晴空再生产
        trainSeatMapper.deleteByExample(example);
        //查找当前车次的车厢
        List<TrainCarriage> carriageList = trainCarriageService.selectByTrainCode(trainCode);
        //循环生成每个车厢的座位
        for (TrainCarriage carriage : carriageList) {
            int rowCount = carriage.getRowCount();
            String seatType = carriage.getSeatType();
            int seatIndex = 1;
            List<SeatColEnum> colEnumList = SeatColEnum.getColsByType(seatType);
            for (int row = 1; row <= rowCount; row++) {
                for (SeatColEnum seatColEnum : colEnumList) {
                    //构造座椅
                    TrainSeat trainSeat = new TrainSeat();
                    trainSeat.setId(SnowUtil.getSnowflakeNextId());
                    trainSeat.setCreateTime(now);
                    trainSeat.setUpdateTime(now);
                    trainSeat.setTrainCode(trainCode);
                    trainSeat.setCarriageIndex(carriage.getIndex());
                    trainSeat.setRow(StrUtil.fillBefore(String.valueOf(row), '0', 2));
                    trainSeat.setCol(seatColEnum.getCode());
                    trainSeat.setSeatType(seatType);
                    trainSeat.setCarriageSeatIndex(seatIndex++);
                    trainSeatMapper.insert(trainSeat);
                }
            }
        }
        //根据座位类型，筛选所有的类
    }
}
