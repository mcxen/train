package com.mcxgroup.business.req;

import com.mcxgroup.common.req.PageReq;

public class TrainStationQueryReq extends PageReq {

    private  String trainCode;


    @Override
    public String toString() {
        return "TrainStationQueryReq{" +
                "trainCode='" + trainCode + '\'' +
                "} " + super.toString();
    }

    public String getTrainCode() {
        return trainCode;
    }

    public void setTrainCode(String trainCode) {
        this.trainCode = trainCode;
    }

}