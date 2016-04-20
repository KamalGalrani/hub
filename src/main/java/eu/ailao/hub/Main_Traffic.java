package eu.ailao.hub;

import eu.ailao.hub.traffic.analyze.QuestionAnalyzer;
import eu.ailao.hub.traffic.analyze.dataclases.TrafficQuestionInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Petr Marek on 14.03.2016.
 * Main class for detecting question topic
 */
public class Main_Traffic {
	public static void main(String[] args) {
		if (args.length < 3) {
			System.err.println("Insert Lookup Service URL, Dataset-STS URL and reference questions TSV file as arguments please.");
			System.exit(-1);
		}
		Statics.labelLookupURL = args[0];
		Statics.datasetSTSURL = args[1];
		Statics.referenceQuestions = args[2];

		BufferedReader buffer=new BufferedReader(new InputStreamReader(System.in));
		String question= null;
		try {
			question = buffer.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}

		QuestionAnalyzer questionAnalyzer=new QuestionAnalyzer();
		TrafficQuestionInfo trafficQuestionInfo = questionAnalyzer.analyzeTrafficQuestion(question);
		System.out.println(trafficQuestionInfo);

		return;
	}
}
