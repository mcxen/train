package com.mcxgroup.business.req;

import com.mcxgroup.common.req.PageReq;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class DailyTrainSeatQueryReq extends PageReq {

    @DateTimeFormat(pattern = "yyyy-MM-dd")

    private Date date;
    private String trainCode;
}