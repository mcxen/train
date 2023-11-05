package com.mcxgroup.business.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mcxgroup.common.req.PageReq;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class DailyTrainTicketQueryReq extends PageReq {
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;
    private String trainCode;
    private String start;
    private String end;
}