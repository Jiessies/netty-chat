package com.jie.space.nettychat.config.msg;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RetCodeEnum {

    SUCC(0, "SUCC"),

    FAIL(1, "FAIL"),

    /* 14 - 网络错误 */
    READ_TIME_OUT(1401, "获取数据超时 -_-"),
    CONNECTION_TIME_OUT(1402, "连接超时 -_-"),

    /* 2开头 — 环境错误 */
    ENV_ERROR(2000, "环境错误 >_<"),

    /* 21 - Redis相关错误 */
    ENV_ERROR_REDIS(2100, "Redis错误 -_-"),
    ENV_ERROR_REDIS_CONNECT(2101, "Redis连接失败 -_-"),

    /* 22 Mysql相关错误 */
    ENV_ERROR_MYSQL(2200, "MySql错误 +_+"),

    /* 3开头 - 输入错误*/
    INPUT_ERROR(3000, "入参错误 o_O???"),

    /* 4开头 - 数据相关错误*/
    CINEMA_NOTEXIST(4001, "数据不存在 -_-#"),

    /* 9999 - 为定义错误 */
    UNKNOWN(9999, "未知错误 Q_Q");

    private final int value;
    private final String msg;

    RetCodeEnum(int v, String m) {
        value = v;
        msg = m;
    }

    /**
     * @JsonCreator -> 反序列化
     */
    @JsonCreator
    public static RetCodeEnum fromValue(int typeCode) {
        for (RetCodeEnum retCode : RetCodeEnum.values()) {
            if (retCode.toValue() == typeCode) {
                return retCode;
            }
        }
        throw new IllegalArgumentException("Invalid type code: " + typeCode);
    }

    /**
     * @JsonValue -> 指定json序列化该值
     */
    @JsonValue
    public int toValue() {
        return value;
    }

    public String toMsg() {
        return msg;
    }

    public String toString() {
        return String.valueOf(value);
    }

}
