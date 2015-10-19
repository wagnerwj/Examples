package com.nextcentury.lorelei.domain;

import java.io.Serializable;

public class LoreleiSentiment implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2041373428858076501L;
	
	
	private Double sentiment;
	
	private Double arousal;

	public Double getSentiment() {
		return sentiment;
	}

	public void setSentiment(Double sentiment) {
		this.sentiment = sentiment;
	}

	public Double getArousal() {
		return arousal;
	}

	public void setArousal(Double arousal) {
		this.arousal = arousal;
	}
	
	

}
