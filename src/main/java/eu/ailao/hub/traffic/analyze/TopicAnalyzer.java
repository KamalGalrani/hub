package eu.ailao.hub.traffic.analyze;

import eu.ailao.hub.Statics;
import eu.ailao.hub.traffic.analyze.dataclases.LoadedReferenceQuestions;
import eu.ailao.hub.traffic.analyze.dataclases.TrafficTopic;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.Normalizer;

/**
 * Created by Petr Marek on 4/11/2016.
 * Class for analyzing topic of the traffic question
 */
public class TopicAnalyzer {

	private double TRESHOLD = 0.9000000134110451;

	/**
	 * Analyze topic of the traffic question
	 * @param question Question
	 * @return Topic
	 */
	public TrafficTopic analyzeTrafficTopic(String question) {
		try {
			JSONObject jsonObject = askDatasetSTS(question);
			JSONArray probabilities = jsonObject.getJSONArray("score");
			double[] normalizedProbabilities = normalizeProbabilities(probabilities);
			TrafficTopic topic = determineTopicMax(normalizedProbabilities);
			return topic;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return TrafficTopic.UNKNOWN;
	}

	/**
	 * Asks DatasetSTS for question distance to reference questions
	 * @param question question
	 * @return Answer from DatasetSTS
	 * @throws IOException
	 */
	public JSONObject askDatasetSTS(String question) throws IOException {
		String response = "";
		URL url = new URL(Statics.datasetSTSURL);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json");
		String input = createQuerry(question);
		OutputStream os = conn.getOutputStream();
		os.write(input.getBytes());
		os.flush();

		BufferedReader br = new BufferedReader(new InputStreamReader(
				conn.getInputStream(), "UTF8"));

		String output;
		while ((output = br.readLine()) != null) {
			response += output;
		}
		conn.disconnect();
		return new JSONObject(response);
	}

	/**
	 * Creates query for DatasetSTS
	 * @param question question
	 * @return Query
	 */
	private String createQuerry(String question) {
		JSONObject querry = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		LoadedReferenceQuestions loadedReferenceQuestions = LoadedReferenceQuestions.getInstance();
		for (int i = 0; i < loadedReferenceQuestions.size(); i++) {
			jsonArray.put(stripAccents(loadedReferenceQuestions.getQuestion(i).toLowerCase().replace("?", "")));
		}
		querry.put("qtext", question);
		querry.put("atext", jsonArray);
		return querry.toString();
	}

	/**
	 * Converts special characters like ěšř to esr
	 * @param s string to convert
	 * @return converted string
	 */
	private static String stripAccents(String s) {
		s = Normalizer.normalize(s, Normalizer.Form.NFD);
		s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
		return s;
	}

	/**
	 * Finds the most probable topic
	 * @param probabilities Probabilities to reference questions
	 * @return The most probable topic
	 */
	private TrafficTopic determineTopicMax(double[] probabilities) {
		TrafficTopic mostProbableTopic = TrafficTopic.UNKNOWN;
		double biggestProbability = -1;
		LoadedReferenceQuestions loadedReferenceQuestions = LoadedReferenceQuestions.getInstance();
		for (int i = 0; i < probabilities.length; i++) {
			if (probabilities[i] < TRESHOLD) {
				continue;
			}
			if (probabilities[i] > biggestProbability) {
				biggestProbability = probabilities[i];
				mostProbableTopic = loadedReferenceQuestions.getTrafficTopic(i);
			}
		}
		return mostProbableTopic;
	}

	/**
	 * Normalize probabilities by sigmoid
	 * @param probabilities probabilities
	 * @return Normalized probabilities
	 */
	public double[] normalizeProbabilities(JSONArray probabilities) {
		double[] normalizedProbabilities = new double[probabilities.length()];
		for (int i = 0; i < probabilities.length(); i++) {
			normalizedProbabilities[i] = sigmoid((Double) probabilities.get(i));
		}
		return normalizedProbabilities;
	}

	/**
	 * Sigmoid function
	 * @param x input
	 * @return output
	 */
	public static double sigmoid(double x) {
		return (1 / (1 + Math.pow(Math.E, (-1 * x))));
	}
}
