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

public class TopicIdentifierBolt extends BaseRichBolt {

	OutputCollector _collector;
    
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector){
	      // save the output collector for emitting tuples
	      _collector = collector;
	    }
	public void execute(Tuple input) {
		DataHolder holder = (DataHolder) input.getValueByField("dataHolder");
		Long id = input.getLongByField("id");
		System.out.println("Topic Identifier Bolt has text "+ holder.getText());
		//Parse it here- too lazy to bring in
		//store data back into holder...
		_collector.emit(input,new Values(id, holder));
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("id","dataHolder"));
		}

}
