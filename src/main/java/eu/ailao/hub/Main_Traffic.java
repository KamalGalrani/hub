package eu.ailao.hub;

import eu.ailao.hub.traffic.analyze.QuestionAnalyzer;
import eu.ailao.hub.traffic.analyze.dataclases.TrafficQuestionInfo;

/**
 * Created by Petr Marek on 14.03.2016.
 * Main class for detecting question topic
 */
public class Main_Traffic {
	public static void main(String[] args) {
		if (args.length < 4) {
			System.err.println("Insert question, Lookup Service URL, Dataset-STS URL and reference questions TSV file as arguments please.");
			System.exit(-1);
		}
		String question = args[0];
		Statics.labelLookupURL = args[1];
		Statics.datasetSTSURL = args[2];
		Statics.referenceQuestions = args[3];

		QuestionAnalyzer questionAnalyzer=new QuestionAnalyzer();
		TrafficQuestionInfo trafficQuestionInfo = questionAnalyzer.analyzeTrafficQuestion(question);
		System.out.println(trafficQuestionInfo);

		return;
	}
}
