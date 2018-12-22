package com.jie.space.nettychat.config.msg;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResMsg<T> {

	private final static String SUCC_MSG = "succ";
	private final static String SUCC_CODE = "0";

	private String ret;
	private String sub;
	private String msg;
	private T data;

	public static ResMsg succ() {
		return ResMsg.builder().ret(RetCodeEnum.SUCC.toString()).sub(SUCC_CODE).msg(SUCC_MSG).build();
	}

	public static ResMsg succWithData(Object data) {
		return ResMsg.builder().ret(RetCodeEnum.SUCC.toString()).sub(SUCC_CODE).msg(SUCC_MSG).data(data).build();
	}

	public static ResMsg fail(RetCodeEnum ret, String sub, String msg) {
		return ResMsg.builder().ret(ret.toString()).sub(sub).msg(msg).build();
	}

	public static ResMsg fail(String ret, String sub, String msg) {
		return ResMsg.builder().ret(ret).sub(sub).msg(msg).build();
	}

	public static ResMsg unknowWithMsg(String msg) {
		return ResMsg.builder().ret(RetCodeEnum.UNKNOWN.toString()).sub("").msg(msg).build();
	}

}
