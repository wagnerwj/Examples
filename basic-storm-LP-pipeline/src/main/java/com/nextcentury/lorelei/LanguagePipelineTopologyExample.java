package com.nextcentury.lorelei;

import java.util.Collections;

import com.nextcentury.lorelei.bolts.CoreferenceResolverBolt;
import com.nextcentury.lorelei.bolts.EventDetectorBolt;
import com.nextcentury.lorelei.bolts.NamedEntityExtractorBolt;
import com.nextcentury.lorelei.bolts.POSTaggerBolt;
import com.nextcentury.lorelei.bolts.SentenceSplitterBolt;
import com.nextcentury.lorelei.bolts.SentimentAnalyzerBolt;
import com.nextcentury.lorelei.bolts.SingleJoinBolt;
import com.nextcentury.lorelei.bolts.TokenizerBolt;
import com.nextcentury.lorelei.bolts.TopicIdentifierBolt;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.testing.FeederSpout;
import backtype.storm.testing.TestWordSpout;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;

public class LanguagePipelineTopologyExample {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	    Config conf = new Config();
	    
		TopologyBuilder builder= new TopologyBuilder();
		String apiKey = "XQyVtFOMr6D769CXI4spOV4NA";
		String apiSecret = "Y6Zgm6hplUd8QurDey8cJyITTmRPnmvBoiLatYx6B8sYsXfjcc";
		
		String accessToken = "545061248-aLVzVPf5KCzK5OVLEBR0hWpiiEkVc8gaylvJplug";
		
		String accessTokenSecret =  "9b3V0elIoZETxhyQfYGhaS34WXWFWU8aVbnIrEoSMzt0j";
		ImprovedTwitterSampleSpout inputSpout =  new ImprovedTwitterSampleSpout(apiKey, apiSecret, accessToken, accessTokenSecret,new String[] {"#migrants"});
		
		builder.setSpout("feeder", inputSpout);
		
		SentenceSplitterBolt sentenceSplitterBolt=new SentenceSplitterBolt();
		TokenizerBolt tokenizerBolt = new TokenizerBolt();
		SentimentAnalyzerBolt sentimentAnalyzerBolt = new SentimentAnalyzerBolt();
		POSTaggerBolt posTaggerBolt = new POSTaggerBolt();
		NamedEntityExtractorBolt nerBolt = new NamedEntityExtractorBolt();
		CoreferenceResolverBolt corefResolveBolt = new CoreferenceResolverBolt();
		TopicIdentifierBolt topicIdentBolt = new TopicIdentifierBolt();
		EventDetectorBolt eventDetectorBolt = new EventDetectorBolt();
		
		builder.setBolt("sentenceSplitter", sentenceSplitterBolt, 4).shuffleGrouping("feeder");
		
		builder.setBolt("tokenizer", tokenizerBolt,4).shuffleGrouping("sentenceSplitter");
		builder.setBolt("posTagger", posTaggerBolt,4).shuffleGrouping("tokenizer");
		builder.setBolt("nerBolt", nerBolt,4).shuffleGrouping("posTagger");
		builder.setBolt("corefBolt", corefResolveBolt,4).shuffleGrouping("nerBolt");
		builder.setBolt("topicId", topicIdentBolt,4).shuffleGrouping("nerBolt");
		builder.setBolt("eventDetect", eventDetectorBolt,4).shuffleGrouping("nerBolt");
		
		builder.setBolt("sentimentAnalyzer", sentimentAnalyzerBolt,4).shuffleGrouping("sentenceSplitter");
		
		SingleJoinBolt joinerBolt = new SingleJoinBolt(new Fields("dataHolder"));
		builder.setBolt("join", joinerBolt,1).fieldsGrouping("corefBolt", new Fields("id"))
		.fieldsGrouping("topicId", new Fields("id"))
		.fieldsGrouping("eventDetect", new Fields("id"))
		.fieldsGrouping("sentimentAnalyzer", new Fields("id"));
		
	      LocalCluster cluster = new LocalCluster();

	      // submit the topology to the local cluster
	      cluster.submitTopology("exclamation", conf, builder.createTopology());

	      // let the topology run for 20 seconds. note topologies never terminate!
	      try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	      // kill the topology
	      cluster.killTopology("exclamation");

	      // we are done, so shutdown the local cluster
	      cluster.shutdown();
		
	}

}
