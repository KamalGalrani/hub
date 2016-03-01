package eu.ailao.hub.questions;

import eu.ailao.hub.transformations.Transformation;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Petr Marek on 30.12.2015.
 * Question class storing original text of answer, applied transformations and transformed text of answer
 */
public class Question {
	private int yodaQuestionID;
	private String originalQuestionText;
	private String transformedQuestionText;
	private ArrayList<Transformation> transformations = new ArrayList<>();

	public Question(String questionText) {
		this.originalQuestionText = questionText;
		this.transformedQuestionText = questionText;
	}

	public String getTransformedQuestionText() {
		return transformedQuestionText;
	}

	public String getOriginalQuestionText(){
		return originalQuestionText;
	}

	public int getYodaQuestionID() {
		return yodaQuestionID;
	}

	public void setYodaQuestionID(int yodaQuestionID) {
		this.yodaQuestionID = yodaQuestionID;
	}

	/***
	 * Test if transformation can be applied and applies it if so
	 * @param transformation Transformation to apply
	 * @return TRUE if transformation was applied
	 */
	public boolean applyTransformationIfUseful(Transformation transformation) {
		if (isTransformationUseful(transformation)) {
			this.transformedQuestionText = transformation.transform(transformedQuestionText);
			this.transformations.add(transformation);
			return true;
		}
		return false;
	}

	/***
	 * Tests if is transformation useful and can be applied
	 * @param transformation
	 * @return TRUE if it is useful
	 */
	private boolean isTransformationUseful(Transformation transformation) {
		return transformation.transformDetection(originalQuestionText);
	}

	/***
	 * Transforms answer to question back by applying back transformations in reverse order
	 * @param answer Answer to question to transform back
	 * @return Answer transformed back
	 */
	public JSONObject transformBack(JSONObject answer) {
		answer.put("text", originalQuestionText);
		for (int i = transformations.size() - 1; i >= 0; i--) {
			answer = transformations.get(i).transformBack(answer);
		}
		return answer;
	}

	/***
	 * Transforms answer sentence by applying back transformations in reverse order
	 * @param answerSentence Sentence of answer to transform back
	 * @return Sentence transformed back
	 */
	public String transformBackAnswerSentence(String answerSentence) {
		for (int i = transformations.size() - 1; i >= 0; i--) {
			answerSentence = transformations.get(i).transformBackAnswerSentence(answerSentence);
		}
		return answerSentence;
	}
}
