package com.mcxgroup.member.dto;

public class MemberLoginRespDto {
    private Long id;

    private String mobile;

    private String token;

    @Override
    public String toString() {
        return "MemberLoginRespDto{" +
                "id=" + id +
                ", mobile='" + mobile + '\'' +
                ", token='" + token + '\'' +
                '}';
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

}