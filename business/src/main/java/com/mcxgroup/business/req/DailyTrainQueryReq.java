package com.mcxgroup.business.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mcxgroup.common.req.PageReq;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class DailyTrainQueryReq extends PageReq {

    private String code;
    //凡是POST日期的可以用Json来实现
//    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
//    Get的请求用自带的
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;

    @Override
    public String toString() {
        return "DailyTrainQueryReq{" +
                "code='" + code + '\'' +
                ", date=" + date +
                "} " + super.toString();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}