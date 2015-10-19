package com.nextcentury.lorelei.service;

import java.io.CharArrayWriter;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextcentury.lorelei.domain.LoreleiSentiment;
import com.nextcentury.lorelei.domain.dto.AnnotationDTO;
import com.nextcentury.lorelei.domain.dto.SentenceAnnotationDTO;

import edu.stanford.nlp.io.StringOutputStream;
import edu.stanford.nlp.ling.CoreAnnotations.MentionsAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.AnnotationOutputter.Options;
import edu.stanford.nlp.pipeline.JSONOutputter;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.ArrayCoreMap;
import edu.stanford.nlp.util.CoreMap;

@Service
public class LoreleiNLPService {
	private Map<String, Double> sentimentMap;
	
	private  StanfordCoreNLP pipeline;
	
	private DTOService dtoService;
	
	@PostConstruct
	public void init(){
		ClassPathResource cpr = new ClassPathResource("properties/Ratings_Warriner_et_al.csv");
		//File csvData = new File("properties/Ratings_Warriner_et_al.csv");
		 try {
			CSVParser parser = CSVParser.parse(cpr.getFile(), Charset.forName("UTF-8"),CSVFormat.RFC4180);
			sentimentMap = new HashMap<String, Double>();
			
			double sum =0.0;
			Iterator<CSVRecord> iter = parser.iterator();
			iter.next();
			while(iter.hasNext()){
				CSVRecord record = iter.next();
				Double happVal = Double.valueOf(record.get(2));
				sum+=happVal;
				sentimentMap.put(record.get(1).toLowerCase().trim(), happVal);
			}
			sum/= sentimentMap.size();
			for(String key:sentimentMap.keySet()){
				sentimentMap.put(key, sentimentMap.get(key)-sum);
			}
		 }catch(Exception e){
			 System.out.println(e.getLocalizedMessage());
		 }
		 
		 Properties props = new Properties();
		    props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref, entitymentions");
		    pipeline = new StanfordCoreNLP(props);
		    
		    dtoService = new DTOService();
		
	}
	
	public LoreleiSentiment generateSentiment(String text){
		LoreleiSentiment result = new LoreleiSentiment();
		String[] tokens = text.split(" ");
		double sentiment = 0.0;
		double arousal = 0.0;
		for(String token: tokens){
			Double tokenSent = sentimentMap.get(token.toLowerCase().trim());
			if(tokenSent == null){
				tokenSent = 0.0;
			}
			sentiment += tokenSent;
			arousal += Math.abs(tokenSent);
		}
		sentiment /= tokens.length;
		arousal /= tokens.length;
		result.setSentiment(sentiment);
		result.setArousal(arousal);
		return result;
	}
	
	
	public AnnotationDTO generateStanfordAnnotation(String text){
		Annotation document = new Annotation(text);
		AnnotationDTO dto = new AnnotationDTO();
		
	    AtomicReference<Throwable> cause = new AtomicReference<Throwable>();
	    // run all Annotators on this text
	    pipeline.annotate(document);
	    List<SentenceAnnotationDTO> sentenceDtos = new ArrayList<SentenceAnnotationDTO>();
	    for(CoreMap sentence:document.get(SentencesAnnotation.class)){
	    	sentenceDtos.add(dtoService.generateSentenceDTO(sentence));
	    }
	    dto.setSentences(sentenceDtos);
	  
	    
	    return dto;
	}

}
