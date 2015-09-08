package com.nextcentury.lorelei.bolts;

import java.util.Map;

import com.nextcentury.lorelei.DataHolder;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import twitter4j.Status;

public class SentenceSplitterBolt extends BaseRichBolt{

	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// To output tuples from this bolt to the next stage bolts, if any
    OutputCollector _collector;
    
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector){
	      // save the output collector for emitting tuples
	      _collector = collector;
	    }

	public void execute(Tuple input) {
		//Looks like each bolt REALLY needs to know whats coming to it
		//can abstract into wrapper classes- but can work from that
		/**
		 *  What if: we create an abstract data class- some basic functions and data elements
		 *  Some suggested functions: toXmlString, toJsonString, getId, getData
		 *  can we pass this through our system nicely?
		 *  
		 */
		Long id = input.getLongByField("id");
		Status tweet = (Status) input.getValueByField("tweet");
		System.out.println(" SENTENCE SPLITTER has input text "+tweet.getText());
		String[] sentences = tweet.getText().split("\\.");
		DataHolder holder = new DataHolder();
		holder.setId(id);
		holder.setTweet(tweet);
		holder.setText(tweet.getText());
		holder.setSentences(sentences);
		_collector.emit(input,new Values(id,holder));
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("id","dataHolder"));
		}

}
