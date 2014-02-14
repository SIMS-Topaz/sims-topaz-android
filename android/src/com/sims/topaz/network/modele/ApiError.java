package com.sims.topaz.network.modele;

public class ApiError {
	
	private Integer Code;
	private String msg;
	
	public ApiError() {
	}
	
	public Integer getCode() {
		return Code;
	}
	
	public String getMsg() {
		return msg;
	}
	
	public void setCode(Integer code) {
		Code = code;
	}
	
	public void setMsg(String msg) {
		this.msg = msg;
	}
}
