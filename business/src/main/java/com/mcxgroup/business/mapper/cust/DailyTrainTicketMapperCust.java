package com.mcxgroup.business.mapper.cust;

import java.util.Date;

/**
 * @ClassName DailyTrainTicketMapperCust
 * @Description
 * @Author McXen@2023/11/12
 **/
public interface DailyTrainTicketMapperCust {

    void updateCountBySell(Date date,
                           String trainCode,
                           String seatTypeCode,
                           Integer minStartIndex,
                           Integer maxStartIndex,
                           Integer minEndIndex,
                           Integer maxEndIndex);
}
