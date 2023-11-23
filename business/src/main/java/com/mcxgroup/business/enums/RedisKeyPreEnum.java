package com.mcxgroup.business.enums;

/**
 * @ClassName RedisKeyPreEnum
 * @Description
 * @Author McXen@2023/11/23
 **/
public enum RedisKeyPreEnum {
    CONFIRM_ORDER("LOCK_CONFIRM_ORDER","一等座"),
    SK_TOKEN("LOCK_SK_TOKEN","二等座");
    private final String code;
    private final String desc;

    RedisKeyPreEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
