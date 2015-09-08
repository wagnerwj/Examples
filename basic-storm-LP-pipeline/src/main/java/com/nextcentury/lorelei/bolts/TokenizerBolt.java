package com.nextcentury.lorelei.bolts;

import java.util.LinkedHashMap;
import java.util.Map;

import com.nextcentury.lorelei.DataHolder;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class TokenizerBolt extends BaseRichBolt {

	/**
	 * 
	 */
	private static final long serialVersionUID = 923756663956423650L;
	// Is this necessary? Really?
    OutputCollector _collector;
    
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
	      _collector = collector;
	}

	public void execute(Tuple input) {
		DataHolder holder = (DataHolder) input.getValueByField("dataHolder");
		Long id = input.getLongByField("id");
		System.out.println("TOKENIZER has text "+holder.getText());
		Map<Integer, String[]> tokenizedSentences = new LinkedHashMap<Integer, String[]>();
		//just going to order stupidly simple: increment through and store the integer as the key
		for(int indx = 0; indx< holder.getSentences().length; indx++){
			String[] splitSentence = holder.getSentences()[indx].split(" ,");
			tokenizedSentences.put(indx, splitSentence);
		}
		holder.setSplitSentences(tokenizedSentences);
		//lets just make sure I can do this
		_collector.emit(input,new Values(id, holder));

	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("id","dataHolder"));
		}

}
