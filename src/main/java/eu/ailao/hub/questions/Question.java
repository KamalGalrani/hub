package eu.ailao.hub.questions;

import eu.ailao.hub.Statics;
import eu.ailao.hub.dialog.Dialog;
import eu.ailao.hub.transformations.Transformation;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;

import java.util.ArrayList;

/**
 * Created by Petr Marek on 30.12.2015.
 * Question class storing original text of answer, applied transformations and transformed text of answer
 */
public class Question {
	final Logger logger = LoggerFactory.getLogger(Question.class);
	private int serviceQuestionID;
	private int clientQuestionID;
	private String originalQuestionText;
	private String transformedQuestionText;
	private ArrayList<Transformation> transformations = new ArrayList<>();
	private Statics.Services service;
	private Request request;
	private Dialog dialog;

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

	public int getServiceQuestionID() {
		return serviceQuestionID;
	}

	public void setServiceQuestionID(int serviceQuestionID) {
		this.serviceQuestionID = serviceQuestionID;
	}

	public int getClientQuestionID() {
		return clientQuestionID;
	}

	public void setClientQuestionID(int clientQuestionID) {
		this.clientQuestionID = clientQuestionID;
	}

	public Statics.Services getService() {
		return service;
	}

	public void setService(Statics.Services service) {
		this.service = service;
	}

	public Request getRequest() {
		return request;
	}

	public Dialog getDialog() {
		return dialog;
	}

	public void setRequest(Request request) {
		this.request = request;
	}

	public void setDialog(Dialog dialog) {
		this.dialog = dialog;
	}

	/***
	 * Test if transformation can be applied and applies it if so
	 * @param transformation Transformation to apply
	 * @return TRUE if transformation was applied
	 */
	public boolean applyTransformationIfUseful(Transformation transformation) {
		if (isTransformationUseful(transformation)) {
			String questionBeforeTransform=transformedQuestionText;
			this.transformedQuestionText = transformation.transform(questionBeforeTransform);
			this.transformations.add(transformation);
			logger.info("Getting id| Question: {}, Transformed to: {}, By: {}", questionBeforeTransform, transformedQuestionText, transformation.getClass().getName());
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
