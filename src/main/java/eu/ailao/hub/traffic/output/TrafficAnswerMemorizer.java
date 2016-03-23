package eu.ailao.hub.traffic.output;

import java.util.LinkedHashMap;

/**
 * Created by Petr Marek on 17.03.2016.
 * Class for memorizing answers for traffic
 */
public class TrafficAnswerMemorizer {
	private LinkedHashMap<Integer,String> answerMap= new LinkedHashMap<>();

	public void addToAnswerMap(int id, String answer){
		answerMap.put(id,answer);
	}

	public String getAnswer(int id){
		return answerMap.get(id);
	}
}
