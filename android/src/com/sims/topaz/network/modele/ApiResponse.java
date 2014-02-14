package com.sims.topaz.network.modele;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponse<TypeData> {

	@JsonProperty("success")
	private ApiSuccess success;
	
	@JsonProperty("error")
	private ApiError error;
	private TypeData data;
	
	public ApiResponse() {
	}

	public ApiSuccess getSuccess() {
		return success;
	}
	
	public ApiError getError() {
		return error;
	}
	
	public TypeData getData() {
		return data;
	}
	
	public void setData(TypeData data) {
		this.data = data;
	}
	
	public void setSuccess(ApiSuccess success) {
		this.success = success;
	}
	
	public void setError(ApiError error) {
		this.error = error;
	}

}
