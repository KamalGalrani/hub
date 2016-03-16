package eu.ailao.hub.hereapi;

import java.util.LinkedHashMap;
import java.util.Random;

/**
 * Created by Petr Marek on 16.03.2016.
 */
public class Traffic {

	private Random idgen=new Random();

	LinkedHashMap<Integer,String> questionMap= new LinkedHashMap<>();

	public int askQuestion(String question){
		int id= idgen.nextInt(Integer.MAX_VALUE);
		//TODO add answer
		questionMap.put(id, "");
		return idgen.nextInt(Integer.MAX_VALUE);
	}

	public String getAnswer(int id){
		return questionMap.get(id);
	}

}
