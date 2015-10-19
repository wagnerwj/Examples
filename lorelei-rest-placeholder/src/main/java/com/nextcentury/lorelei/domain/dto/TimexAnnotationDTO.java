package com.nextcentury.lorelei.domain.dto;

import java.io.Serializable;

public class TimexAnnotationDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4192829433724544669L;

	private String tid;
	
	private String type;
	
	private String value;
	
	private String altValue;

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getAltValue() {
		return altValue;
	}

	public void setAltValue(String altValue) {
		this.altValue = altValue;
	}
	

}
