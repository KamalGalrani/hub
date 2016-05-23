package eu.ailao.hub.questions;

import java.util.HashMap;
import java.util.Random;

/**
 * Created by Petr Marek on 30.12.2015.
 * Class, which remembers questions and their id in HashMap
 */
public class QuestionMapper {

	private Random idgen=new Random();
	HashMap<Integer, Question> questionMapByClientID =new HashMap<>();
	HashMap<Integer, Integer> serviceIDtoClientID =new HashMap<>();

	private static QuestionMapper instance = null;
	protected QuestionMapper() {
		// Exists only to defeat instantiation.
	}
	public static QuestionMapper getInstance() {
		if(instance == null) {
			instance = new QuestionMapper();
		}
		return instance;
	}

	public Question getQuestionByClientID(int id){
		Question question= questionMapByClientID.get(id);
		return question;
	}

	public int addQuestion(Question question){
		int clientQuestionID = idgen.nextInt(Integer.MAX_VALUE);
		question.setClientQuestionID(clientQuestionID);
		questionMapByClientID.put(clientQuestionID, question);
		return clientQuestionID;
	}

	public void setServiceIdAndQuestionIDpair(int serviceID, int clientID){
		serviceIDtoClientID.put(serviceID,clientID);
	}

	public int getClientIdByServiceID(int serviceID){
		return serviceIDtoClientID.get(serviceID);
	}
}
