package eu.ailao.hub.transformations;

import eu.ailao.hub.questions.Question;
import org.json.JSONObject;

/**
 * Created by Petr Marek on 30.12.2015.
 * Base class of transformations
 */
public abstract class Transformation {

	/***
	 * Detects if transformation can be used
	 * @param questionText Text of question to transform
	 * @return TRUE if it can be used
	 */
	public abstract boolean transformDetection(String questionText);

	/***
	 * Transforms question text
	 * @param questionText Text of question to be transformed
	 * @return Text of question after transformation
	 */
	public abstract String transform(String questionText);

	/***
	 * Transforms answer to question back
	 * @param answer Answer to question to be transformed back
	 * @return answer transformed back
	 */
	public abstract JSONObject transformBack(JSONObject answer);

	/***
	 * Transforms answer sentence by applying back transformations in reverse order
	 * @param answerSentence Sentence of answer to transform back
	 * @return Sentence transformed back
	 */
	public abstract String transformBackAnswerSentence(String answerSentence);
}
