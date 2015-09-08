package com.nextcentury.lorelei;

import java.io.Serializable;
import java.util.Map;

import twitter4j.Status;

public class DataHolder implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8631941828100134118L;

	

	private Long id;
	
	private Status tweet;
	
	private String text;
	
	private String[] sentences;
	
	private Map<Integer, String[]> splitSentences;

	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String[] getSentences() {
		return sentences;
	}

	public void setSentences(String[] sentences) {
		this.sentences = sentences;
	}
	
	public Map<Integer, String[]> getSplitSentences() {
		return splitSentences;
	}

	public void setSplitSentences(Map<Integer, String[]> splitSentences) {
		this.splitSentences = splitSentences;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Status getTweet() {
		return tweet;
	}

	public void setTweet(Status tweet) {
		this.tweet = tweet;
	}

}
