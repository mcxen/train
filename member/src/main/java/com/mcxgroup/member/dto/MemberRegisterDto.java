package com.mcxgroup.member.dto;

import lombok.Data;

@Data
public class MemberRegisterDto {
    private String mobile;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
