package com.mcxgroup.common.exception;


public enum BusinessExceptionEnum {

    MEMBER_MOBILE_EXIST("手机号已存在");//相当于带参数的构造函数了，所以必须得创建带参数构造函数
    private String desc;

    BusinessExceptionEnum(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "BusinessExceptionEnum{" +
                "desc='" + desc + '\'' +
                '}';
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
