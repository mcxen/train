package com.mcxgroup.business.service;

import com.mcxgroup.business.domain.DailyTrainSeat;
import com.mcxgroup.business.domain.DailyTrainTicket;
import com.mcxgroup.business.mapper.DailyTrainSeatMapper;
import com.mcxgroup.business.mapper.cust.DailyTrainTicketMapperCust;
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
    @Resource
    private DailyTrainTicketMapperCust dailyTrainTicketMapperCust;
    @Transactional
    public void afterDoConfirm(DailyTrainTicket dailyTrainTicket,List<DailyTrainSeat> finalSeatList) {
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
            // 计算这个站卖出去后，影响了哪些站的余票库存
            // 参照2-3节 如何保证不超卖、不少卖，还要能承受极高的并发 10:30左右
            // 影响的库存：本次选座之前没卖过票的，和本次购买的区间有交集的区间
            // 假设10个站，本次买4~7站
            // 原售：001000001
            // 购买：000011100
            // 新售：001011101
            // 影响：XXX11111X
            // Integer startIndex = 4;
            // Integer endIndex = 7;
            // Integer minStartIndex = startIndex - 往前碰到的最后一个0;
            // Integer maxStartIndex = endIndex - 1;
            // Integer minEndIndex = startIndex + 1;
            // Integer maxEndIndex = endIndex + 往后碰到的最后一个0;
            Integer startIdx =dailyTrainTicket.getStartIndex();
            Integer endIdx = dailyTrainTicket.getEndIndex();
            char[] sellChar = seatForUpdate.getSell().toCharArray();
            Integer maxStartIndex = endIdx-1;////最大的为0的位置
            Integer minStartIndex = 0;//最小的开始为0的位置
            Integer minEndIndex = endIdx-1;//最小结束影响区间
            Integer maxEndIndex =seatForUpdate.getSell().length();;//最大结束影响区间
            for (int i = startIdx-1; i >= 0; i--) {
                char c = sellChar[i];
                if (c=='1'){
                    minStartIndex=i+1;
                    break;//最小的开始为0的位置
                }
            }
            LOG.info("影响出发站区间：" + minStartIndex + "-" + maxStartIndex);
            for (int i = endIdx; i < seatForUpdate.getSell().length(); i++) {
                char c = sellChar[i];
                if (c=='1'){
                    maxEndIndex=i;
                    break;
                }
            }
            LOG.info("影响到达站区间：" + minEndIndex + "-" + maxEndIndex);
            dailyTrainTicketMapperCust.updateCountBySell(
                    //根据每个最终的seat设置
                    seat.getDate(),
                    seat.getTrainCode(),
                    seat.getSeatType(),
                    minStartIndex,
                    maxStartIndex,
                    minEndIndex,
                    maxEndIndex
            );

        }
    }
}
