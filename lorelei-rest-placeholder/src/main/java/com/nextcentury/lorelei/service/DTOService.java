package com.nextcentury.lorelei.service;

import java.util.ArrayList;
import java.util.List;

import com.nextcentury.lorelei.domain.dto.SentenceAnnotationDTO;
import com.nextcentury.lorelei.domain.dto.TimexAnnotationDTO;
import com.nextcentury.lorelei.domain.dto.TokenAnnotationDTO;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.time.TimeAnnotations;
import edu.stanford.nlp.time.Timex;
import edu.stanford.nlp.util.CoreMap;

public class DTOService {
	
	
	//Yes, should static this stuff. 
	
	
	
	
	public SentenceAnnotationDTO generateSentenceDTO(CoreMap sentence){
		
		SentenceAnnotationDTO sentenceDto = new SentenceAnnotationDTO();
		sentenceDto.setId(sentence.get(CoreAnnotations.SentenceIDAnnotation.class));
		sentenceDto.setIndex(sentence.get(CoreAnnotations.SentenceIndexAnnotation.class));
		sentenceDto.setLine(sentence.get(CoreAnnotations.LineNumberAnnotation.class));
		List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
		List<TokenAnnotationDTO> tokenDtos = new ArrayList<TokenAnnotationDTO>();
		if(tokens!=null){
			for(CoreLabel token:tokens){
				tokenDtos.add(this.generateTokenDTO(token));
			}
			sentenceDto.setTokens(tokenDtos);
		}
		return sentenceDto;
	}
	
	
	public TokenAnnotationDTO generateTokenDTO(CoreLabel token){
		TokenAnnotationDTO tokenDto = new TokenAnnotationDTO();
		tokenDto.setIndex(token.index());
		tokenDto.setWord(token.word());
		tokenDto.setLemma(token.lemma());
		tokenDto.setCharacterOffsetBegin(token.beginPosition());
		tokenDto.setCharacterOffsetEnd(token.endPosition());
		tokenDto.setPos(token.tag());
		tokenDto.setNer(token.ner());
		tokenDto.setNormalizedNER(token.get(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class));
		tokenDto.setSpeaker(token.get(CoreAnnotations.SpeakerAnnotation.class));
		tokenDto.setTruecase(token.get(CoreAnnotations.TrueCaseAnnotation.class));
		tokenDto.setTruecaseText(token.get(CoreAnnotations.TrueCaseTextAnnotation.class));
		Timex time = token.get(TimeAnnotations.TimexAnnotation.class);
		if(time != null){
			tokenDto.setTimex(this.generateTimexDTO(time));
		}
		return tokenDto;
	}
	
	public TimexAnnotationDTO generateTimexDTO(Timex timex){
		TimexAnnotationDTO timexDto = new TimexAnnotationDTO();
		timexDto.setTid(timex.tid());
		timexDto.setType(timex.timexType());
		timexDto.setValue(timex.value());
		timexDto.setAltValue(timex.altVal());
		return timexDto;
	}

}
