package eu.ailao.hub.questions;

import eu.ailao.hub.transformations.Transformation;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Petr Marek on 30.12.2015.
 */
public class Question {
	private String originalQuestionText;
	private String transformedQuestionText;
	private ArrayList<Transformation> transformations=new ArrayList<>();

	public Question(String questionText) {
		this.originalQuestionText = questionText;
		this.transformedQuestionText = questionText;
	}

	public String getTransformedQuestionText() {
		return transformedQuestionText;
	}

	public boolean applyTransformationIfUseful(Transformation transformation){
		if (isTransformationUseful(transformation)){
			this.transformedQuestionText = transformation.transform(transformedQuestionText);
			this.transformations.add(transformation);
			return true;
		}
		return false;
	}

	private boolean isTransformationUseful(Transformation transformation){
		return transformation.transformDetection(originalQuestionText);
	}

	public JSONObject transformBack(JSONObject answer){
		answer.put("text", originalQuestionText);
		for (int i = transformations.size() - 1; i >= 0; i--) {
			answer=transformations.get(i).transformBack(answer);
		}
		return answer;
	}
}
