package eu.ailao.hub.traffic.analyze;

import eu.ailao.hub.traffic.analyze.dataclases.LoadedDataset;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.Normalizer;
import java.util.HashMap;

/**
 * Created by Petr Marek on 4/11/2016.
 */
public class TopicAnalyzer {

	public TrafficTopic analyzeTrafficTopic(String question, LoadedDataset loadedDataset) {
		try {
			JSONObject jsonObject = askDatasetSTS(question, loadedDataset);
			JSONArray probabilities = jsonObject.getJSONArray("score");
			HashMap<TrafficTopic, Float> topicsProbabilities = getProbabilitiesOfTopics(loadedDataset, probabilities);
			//TrafficTopic topic = determineTopicAverage(topicsProbabilities);
			TrafficTopic topic = determineTopicMax(probabilities, loadedDataset);
			return topic;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return TrafficTopic.UNKNOWN;
	}

	private JSONObject askDatasetSTS(String question, LoadedDataset loadedDataset) throws IOException {
		String response = "";
		URL url = new URL("http://pichl.ailao.eu:5000/score");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json");

		String input = createQuerry(question, loadedDataset);
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

	private String createQuerry(String question, LoadedDataset loadedDataset) {
		JSONObject querry = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		for (int i = 0; i < loadedDataset.size(); i++) {
			jsonArray.put(stripAccents(loadedDataset.getQuestion(i).toLowerCase()));
		}
		querry.put("qtext", question);
		querry.put("atext", jsonArray);
		return querry.toString();
	}

	private static String stripAccents(String s) {
		s = Normalizer.normalize(s, Normalizer.Form.NFD);
		s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
		return s;
	}

	private HashMap<TrafficTopic, Float> getProbabilitiesOfTopics(LoadedDataset loadedDataset, JSONArray probabilities) {
		HashMap<TrafficTopic, Float> sumOfProbabilities = new HashMap<>();
		HashMap<TrafficTopic, Integer> numberOfQuestionsWithTopic = new HashMap<>();
		for (int i = 0; i < loadedDataset.size(); i++) {
			TrafficTopic trafficTopic = loadedDataset.getTrafficTopic(i);
			if (sumOfProbabilities.containsKey(trafficTopic)) {
				sumOfProbabilities.put(trafficTopic, (float) (sumOfProbabilities.get(trafficTopic) + probabilities.getDouble(i)));
				numberOfQuestionsWithTopic.put(trafficTopic, numberOfQuestionsWithTopic.get(trafficTopic) + 1);
			} else {
				sumOfProbabilities.put(trafficTopic, (float) probabilities.getDouble(i));
				numberOfQuestionsWithTopic.put(trafficTopic, 1);
			}
		}

		HashMap<TrafficTopic, Float> probabilitiesOfTopics = new HashMap<>();
		for (TrafficTopic topic : sumOfProbabilities.keySet()) {
			probabilitiesOfTopics.put(topic, sumOfProbabilities.get(topic) / numberOfQuestionsWithTopic.get(topic));
		}
		return probabilitiesOfTopics;
	}

	private TrafficTopic determineTopicAverage(HashMap<TrafficTopic, Float> topicsProbabilities) {
		TrafficTopic mostProbableTopic = null;
		float biggestProbability = -1;
		for (TrafficTopic topic : topicsProbabilities.keySet()) {
			float probability = topicsProbabilities.get(topic);
			if (probability > biggestProbability) {
				biggestProbability = probability;
				mostProbableTopic = topic;
			}
		}
		return mostProbableTopic;
	}

	private TrafficTopic determineTopicMax(JSONArray probabilities, LoadedDataset loadedDataset) {
		TrafficTopic mostProbableTopic = null;
		float biggestProbability = -1;
		for (int i = 0; i < probabilities.length(); i++){
			if (probabilities.getDouble(i) > biggestProbability) {
				biggestProbability = (float) probabilities.getDouble(i);
				mostProbableTopic = loadedDataset.getTrafficTopic(i);
			}
		}
		return mostProbableTopic;
	}
}
