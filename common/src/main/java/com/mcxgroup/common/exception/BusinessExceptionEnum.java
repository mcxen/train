package com.mcxgroup.common.exception;


public enum BusinessExceptionEnum {

    MEMBER_MOBILE_EXIST("手机号已存在"),//相当于带参数的构造函数了，所以必须得创建带参数构造函数
    MEMBER_MOBILE_NOT_EXIST("手机号不存在，先获取验证码"),//相当于带参数的构造函数了，所以必须得创建带参数构造函数
    MEMBER_MOBILE_CODE_ERROR("验证码错误"),//相当于带参数的构造函数了，所以必须得创建带参数构造函数
    BUSINESS_STATION_NAME_UNIQUE("车站已经存在"),
    BUSINESS_TRAIN_NAME_UNIQUE("车次已经存在"),
    BUSINESS_TRAIN_CARRIAGE_NAME_UNIQUE("当前车次的该车厢已经存在"),
//    火车车站的唯一有两个
    BUSINESS_TRAIN_STATION_INDEX_UNIQUE("当前车次的该站序已经存在"),
    BUSINESS_TRAIN_STATION_NAME_UNIQUE("当前车次的该站名已经存在"),
    CONFIRM_ORDER_TICKET_COUNT_ERROR("余票不足"),
    CONFIRM_SK_TOKEN_TICKET_COUNT_ERROR("令牌不足，余票卖光"),
    CONFIRM_ORDER_FLOW_EXCEPTION("当前抢票人数过多，请稍后重试"),
    CONFIRM_ORDER_LOCK_FAIL("服务器忙，稍后重试");

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
