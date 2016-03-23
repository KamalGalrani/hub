package eu.ailao.hub;

import eu.ailao.hub.traffic.analyze.QuestionAnalyzer;
import eu.ailao.hub.traffic.analyze.TrafficQuestionInfo;

/**
 * Created by Petr Marek on 14.03.2016.
 * Main class for detecting question topic
 */
public class Main_Traffic {
	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("Insert question as argument please.");
			System.exit(-1);
		}
		String question = args[0];

		QuestionAnalyzer questionAnalyzer=new QuestionAnalyzer();
		TrafficQuestionInfo trafficQuestionInfo = questionAnalyzer.analyzeTrafficQuestion(question);
		System.out.println(trafficQuestionInfo);

		return;
	}
}
