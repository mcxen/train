package com.mcxgroup.member.dto;

import lombok.Data;

import java.util.Date;

@Data
public class PassengerRespDto {
    private Long id;

    private Long memberId;

    private String name;

    private String idCard;

    private String type;

    private Date createTime;

    private Date updateTime;


}