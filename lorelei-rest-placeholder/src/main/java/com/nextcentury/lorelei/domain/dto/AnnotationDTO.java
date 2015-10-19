package com.nextcentury.lorelei.domain.dto;

import java.io.Serializable;
import java.util.List;

public class AnnotationDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6096202066161473828L;
	private List<SentenceAnnotationDTO> sentences;

	public List<SentenceAnnotationDTO> getSentences() {
		return sentences;
	}

	public void setSentences(List<SentenceAnnotationDTO> sentences) {
		this.sentences = sentences;
	}

}
