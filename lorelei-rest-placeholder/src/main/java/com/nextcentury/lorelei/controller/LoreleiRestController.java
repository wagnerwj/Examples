package com.nextcentury.lorelei.controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nextcentury.lorelei.domain.TopicData;
import com.nextcentury.lorelei.service.LoreleiNLPService;

import edu.stanford.nlp.classify.Classifier;
import edu.stanford.nlp.classify.ColumnDataClassifier;
import edu.stanford.nlp.ling.Datum;
import edu.stanford.nlp.pipeline.Annotation;

@RestController
public class LoreleiRestController {
	
	
	private Classifier<String, String> lc;
	private ColumnDataClassifier cdc;
	
	
	private LoreleiNLPService sentimentService;
	
	@PostConstruct
	public void init(){
		sentimentService = new LoreleiNLPService();
		sentimentService.init();
		try {
			Properties prop = new Properties();
			ClassPathResource cpr = new ClassPathResource("properties/topicTrain.prop");
			prop.load(cpr.getInputStream());
					
		cdc=new ColumnDataClassifier(prop);
			cpr = new ClassPathResource("properties/topicDemo.classifier");
			ObjectInputStream objectInput = new ObjectInputStream(cpr.getInputStream());
			lc = (Classifier<String, String>)objectInput.readObject();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@RequestMapping(value="/lorelei-rest/topic/classify", method = RequestMethod.GET)
	public TopicData topicClassifyString(@RequestParam(value="text") String text){

		
		  Datum<String, String> example = cdc.makeDatumFromLine("\t"+text);
		  String dataClass = lc.classOf(example);
		  
		  TopicData topicData = new TopicData();
		  topicData.setTopicClass(dataClass);
		  topicData.setOriginalText(text);
		  topicData.setLoreleiSentiment(sentimentService.generateSentiment(text));
	
		  topicData.setAnnotatedDocument(sentimentService.generateStanfordAnnotation(text));
		return topicData;
	}

}
