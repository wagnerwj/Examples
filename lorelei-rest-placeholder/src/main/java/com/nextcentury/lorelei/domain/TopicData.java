package com.nextcentury.lorelei.domain;

import java.io.Serializable;
import java.util.Date;

import com.nextcentury.lorelei.domain.dto.AnnotationDTO;

import edu.stanford.nlp.pipeline.Annotation;

public class TopicData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8376566652742323434L;
	
	//Note- this will probably change to an ontology class as we move along
	private String topicClass;
	
	private Date timeOfEvent;
	
	//Note- the next two fields will possibly be rolled into a better Geolocation class
	//For the beginning, not needed
	private Double latitude;
	
	private Double longitude;
	
	private String originalText;
	
	
	private LoreleiSentiment loreleiSentiment;
	
	private AnnotationDTO annotatedDocument;


	public String getTopicClass() {
		return topicClass;
	}


	public void setTopicClass(String topicClass) {
		this.topicClass = topicClass;
	}


	public Date getTimeOfEvent() {
		return timeOfEvent;
	}


	public void setTimeOfEvent(Date timeOfEvent) {
		this.timeOfEvent = timeOfEvent;
	}


	public Double getLatitude() {
		return latitude;
	}


	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}


	public Double getLongitude() {
		return longitude;
	}


	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}


	public String getOriginalText() {
		return originalText;
	}


	public void setOriginalText(String originalText) {
		this.originalText = originalText;
	}


	public LoreleiSentiment getLoreleiSentiment() {
		return loreleiSentiment;
	}


	public void setLoreleiSentiment(LoreleiSentiment loreleiSentiment) {
		this.loreleiSentiment = loreleiSentiment;
	}


	public AnnotationDTO getAnnotatedDocument() {
		return annotatedDocument;
	}


	public void setAnnotatedDocument(AnnotationDTO annotatedDocument) {
		this.annotatedDocument = annotatedDocument;
	}

}
