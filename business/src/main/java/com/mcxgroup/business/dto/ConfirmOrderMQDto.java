package com.mcxgroup.business.dto;

import lombok.Data;

import java.util.Date;

/**
 * @ClassName ConfirmOrderMQDto
 * @Description MQ的购票消息的必要字段的封装类，和{ConfirmOrderDoReq}相比，
 * 是简化后的消息体，仅封装了必要的字段
 * @Author McXen@2023/11/24
 **/

public class ConfirmOrderMQDto {
    /**
     * 日志流程号，用于同转异时，用同一个流水号
     */
    private String logId;

    /**
     * 日期
     */
    private Date date;

    /**
     * 车次编号
     */
    private String trainCode;

    @Override
    public String toString() {
        return "ConfirmOrderMQDto{" +
                "logId='" + logId + '\'' +
                ", date=" + date +
                ", trainCode='" + trainCode + '\'' +
                '}';
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTrainCode() {
        return trainCode;
    }

    public void setTrainCode(String trainCode) {
        this.trainCode = trainCode;
    }
}
