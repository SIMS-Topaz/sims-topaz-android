package com.sims.topaz.network.modele;

public class ApiError {
	
	private Integer Code;
	private String msg;
	private String details;
	
	public ApiError() {
	}
	
	public Integer getCode() {
		return Code;
	}
	
	public String getMsg() {
		return msg;
	}
	
	public String getDetails() {
		return details;
	}
	
	public void setCode(Integer code) {
		Code = code;
	}
	
	public void setMsg(String msg) {
		this.msg = msg;
	}
}
