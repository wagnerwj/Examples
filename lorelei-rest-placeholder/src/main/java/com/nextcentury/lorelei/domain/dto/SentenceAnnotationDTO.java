package com.nextcentury.lorelei.domain.dto;

import java.io.Serializable;
import java.util.List;


public class SentenceAnnotationDTO implements Serializable {
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7681356134268175067L;

	private String id;
	
	private Integer index;
	
	private Integer line;
	
	private List<TokenAnnotationDTO> tokens;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public Integer getLine() {
		return line;
	}

	public void setLine(Integer line) {
		this.line = line;
	}

	public List<TokenAnnotationDTO> getTokens() {
		return tokens;
	}

	public void setTokens(List<TokenAnnotationDTO> tokens) {
		this.tokens = tokens;
	}


}
