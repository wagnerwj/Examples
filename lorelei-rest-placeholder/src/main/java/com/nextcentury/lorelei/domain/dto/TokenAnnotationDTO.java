package com.nextcentury.lorelei.domain.dto;

import java.io.Serializable;

public class TokenAnnotationDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4209950254377838374L;

	private Integer index;
	
	private String word;
	
	private String lemma;
	
	private Integer characterOffsetBegin;
	
	private Integer characterOffsetEnd;
	
	private String pos;
	
	private String ner;
	
	private String normalizedNER;
	
	private String speaker;
	
	private String truecase;
	
	private String truecaseText;
	
	private TimexAnnotationDTO timex;

	public int getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getLemma() {
		return lemma;
	}

	public void setLemma(String lemma) {
		this.lemma = lemma;
	}

	public Integer getCharacterOffsetBegin() {
		return characterOffsetBegin;
	}

	public void setCharacterOffsetBegin(Integer characterOffsetBegin) {
		this.characterOffsetBegin = characterOffsetBegin;
	}

	public Integer getCharacterOffsetEnd() {
		return characterOffsetEnd;
	}

	public void setCharacterOffsetEnd(Integer characterOffsetEnd) {
		this.characterOffsetEnd = characterOffsetEnd;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getNer() {
		return ner;
	}

	public void setNer(String ner) {
		this.ner = ner;
	}

	public String getNormalizedNER() {
		return normalizedNER;
	}

	public void setNormalizedNER(String normalizedNER) {
		this.normalizedNER = normalizedNER;
	}

	public String getSpeaker() {
		return speaker;
	}

	public void setSpeaker(String speaker) {
		this.speaker = speaker;
	}

	public String getTruecase() {
		return truecase;
	}

	public void setTruecase(String truecase) {
		this.truecase = truecase;
	}

	public String getTruecaseText() {
		return truecaseText;
	}

	public void setTruecaseText(String truecaseText) {
		this.truecaseText = truecaseText;
	}

	public TimexAnnotationDTO getTimex() {
		return timex;
	}

	public void setTimex(TimexAnnotationDTO timex) {
		this.timex = timex;
	}

}
