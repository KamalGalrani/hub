package eu.ailao.hub;

import eu.ailao.hub.traffic.analyze.QuestionAnalyzer;
import eu.ailao.hub.traffic.analyze.TrafficQuestionInfo;
import eu.ailao.hub.traffic.analyze.dataclases.LoadedDataset;

/**
 * Created by Petr Marek on 14.03.2016.
 * Main class for detecting question topic
 */
public class Main_Traffic {
	public static void main(String[] args) {
		if (args.length < 2) {
			System.err.println("Insert question and Lookup Service URL as arguments please.");
			System.exit(-1);
		}
		String question = args[0];
		Statics.labelLookupURL = args[1];

		LoadedDataset loadedDataset= new LoadedDataset();
		QuestionAnalyzer questionAnalyzer=new QuestionAnalyzer();
		TrafficQuestionInfo trafficQuestionInfo = questionAnalyzer.analyzeTrafficQuestion(question,loadedDataset);
		System.out.println(trafficQuestionInfo);

		return;
	}
}
