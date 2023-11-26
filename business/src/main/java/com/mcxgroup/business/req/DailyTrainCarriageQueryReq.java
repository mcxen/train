package com.mcxgroup.business.req;

import com.mcxgroup.common.req.PageReq;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


public class DailyTrainCarriageQueryReq extends PageReq {

    private String trainCode;
    //凡是POST日期的可以用Json来实现
//    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
//    Get的请求用自带的
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;

    @Override
    public String toString() {
        return "DailyTrainCarriageQueryReq{" +
                "trainCode='" + trainCode + '\'' +
                ", date=" + date +
                "} " + super.toString();
    }

    public String getTrainCode() {
        return trainCode;
    }

    public void setTrainCode(String trainCode) {
        this.trainCode = trainCode;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}