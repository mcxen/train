package com.mcxgroup.member.req;

import jakarta.validation.constraints.NotBlank;

/**
 * @author MCXEN
 */
public class MemberRegisterReq {
    @NotBlank(message = "【手机号】不可以为空")
    private String mobile;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Override
    public String toString() {
        return "MemberRegisterReq{" +
                "mobile='" + mobile + '\'' +
                '}';
    }
}
