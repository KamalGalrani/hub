package eu.ailao.hub.questions;

import java.util.HashMap;
import java.util.Queue;

/**
 * Created by Petr Marek on 30.12.2015.
 * Class, which remembers questions and their id in HashMap
 */
public class QuestionMapper {
	HashMap<Integer, Question> questionMap=new HashMap<>();

	public Question getQuestionByID(int id){
		Question question=questionMap.get(id);
		return question;
	}

	public void addQuestion(int id, Question question){
		questionMap.put(id,question);
	}
}
