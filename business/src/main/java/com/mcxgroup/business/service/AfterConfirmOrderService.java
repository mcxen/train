package com.mcxgroup.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.mcxgroup.business.domain.ConfirmOrder;
import com.mcxgroup.business.domain.DailyTrainSeat;
import com.mcxgroup.business.mapper.ConfirmOrderMapper;
import com.mcxgroup.business.mapper.DailyTrainSeatMapper;
import com.mcxgroup.business.req.ConfirmOrderDoReq;
import com.mcxgroup.common.util.SnowUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @ClassName AfterConfirmOrderService
 * @Description
 * @Author McXen@2023/11/11
 **/
@Service
public class AfterConfirmOrderService {
    private static final Logger LOG = LoggerFactory.getLogger(ConfirmOrderService.class);

//    @Resource
//    private ConfirmOrderMapper confirmOrderMapper;
//    @Resource
//    private DailyTrainTicketService dailyTrainTicketService;
//    @Resource
//    private DailyTrainCarriageService dailyTrainCarriageService;
    @Resource
    private DailyTrainSeatMapper dailyTrainSeatMapper;

    @Transactional
    public void afterDoConfirm(List<DailyTrainSeat> finalSeatList) {
        // 选好之后处理
        //修改售卖情况
        //增加售票记录
        //更新订单为成功
        for (DailyTrainSeat seat : finalSeatList) {
            DailyTrainSeat seatForUpdate = new DailyTrainSeat();
            seatForUpdate.setId(seat.getId());
            seatForUpdate.setSell(seat.getSell());
            seatForUpdate.setUpdateTime(new Date());
            // updateByPrimaryKeySelective: 这个方法会对传入的参数对象进行非空检查，
            // 只有非空的属性才会被包含到UPDATE语句中，
            // 也就是说，这个方法只会更新那些不为null的字段。
            // 这样可以避免覆盖掉数据库中原有的值。
            // updateByPrimaryKey: 这个方法则会更新所有字段，
            // 不论其是否为null。如果某个字段的值为null，
            // 那么在数据库中对应的值也会被更新为null。
            dailyTrainSeatMapper.updateByPrimaryKeySelective(seatForUpdate);
        }
    }
}
