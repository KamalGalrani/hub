package eu.ailao.hub.questions;

import java.util.HashMap;
import java.util.Queue;
import java.util.Random;

/**
 * Created by Petr Marek on 30.12.2015.
 * Class, which remembers questions and their id in HashMap
 */
public class QuestionMapper {
	private Random idgen=new Random();
	HashMap<Integer, Question> questionMap=new HashMap<>();

	public Question getQuestionByID(int id){
		Question question=questionMap.get(id);
		return question;
	}

	public int addQuestion(Question question){
		int clientQuestionID = idgen.nextInt(Integer.MAX_VALUE);
		question.setClientQuestionID(clientQuestionID);
		questionMap.put(clientQuestionID,question);
		return clientQuestionID;
	}
}
