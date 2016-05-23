package eu.ailao.hub.traffic;

import eu.ailao.hub.Statics;
import eu.ailao.hub.communication.CommunicationHandler;
import eu.ailao.hub.communication.WebInterface;
import eu.ailao.hub.corefresol.concepts.Concept;
import eu.ailao.hub.dialog.Dialog;
import eu.ailao.hub.questions.Question;
import eu.ailao.hub.questions.QuestionMapper;
import eu.ailao.hub.transformations.Transformation;
import eu.ailao.hub.transformations.TransformationArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;

import java.util.ArrayDeque;

import static eu.ailao.hub.Statics.isContain;

/**
 * Created by Petr Marek on 4/14/2016.
 * Thread that check for new questions and process them
 */
public class QuestionProcessor implements Runnable {

	private Thread t;

	final Logger logger = LoggerFactory.getLogger(QuestionProcessor.class);
	private Traffic traffic;
	private String yodaQAURL;
	private WebInterface webInterface;

	public QuestionProcessor(Traffic traffic, String yodaQAURL, WebInterface webInterface) {
		this.traffic = traffic;
		this.yodaQAURL = yodaQAURL;
		this.webInterface = webInterface;
	}

	public void run()
	{
		while(true)
		{
			//check incoming messages
			if (!webInterface.getNotYetHandledQuestions().isEmpty()){
				Question question = webInterface.getNotYetHandledQuestions().poll();
				int questionID = askQuestionTraffic(question);
				//Traffic can't help, ask YodaQA
				if (questionID == -1) {
					questionID = askQuestionYodaQA(question);
				}
				question.setServiceQuestionID(questionID);
				QuestionMapper questionMapper = QuestionMapper.getInstance();
				questionMapper.setServiceIdAndQuestionIDpair(question.getServiceQuestionID(),question.getClientQuestionID());
			}
			try {
				Thread.sleep(10); // for 100 FPS
			} catch (InterruptedException ignore) {
			}
		}
	}

	public void start ()
	{
		if (t == null)
		{
			t = new Thread (this, "QuestionProccessor");
			t.start ();
		}
	}

	/**
	 * Ask traffic
	 * @param question question to ask
	 * @return service ID
	 */
	private int askQuestionTraffic(Question question) {
		question.setService(Statics.Services.TRAFFIC);
		return traffic.askQuestion(question.getTransformedQuestionText());
	}

	/**
	 * Ask yodaQA
	 * @param question Question to ask
	 * @return service ID
	 */
	private int askQuestionYodaQA(Question question) {
		Dialog dialog = question.getDialog();
		Request request = question.getRequest();
		transformQuestion(question);
		dialog.getConceptMemorizer().updateConceptsDuringAsking(request.queryMap().toMap());

		String questionID = askQuestion(question, request, dialog);
		question.setService(Statics.Services.YODA_QA);
		int questionIDint = getQuestionIDFromAnswer(questionID);
		return questionIDint;
	}

	/***
	 * Gets ID of question from YodaQA's answer to this question
	 * @param answer YodaQA's answer
	 * @return id of question
	 */
	private int getQuestionIDFromAnswer(String answer) {
		return Integer.parseInt(answer.replaceAll("[\\D]", ""));
	}

	/***
	 * Applies transformations to question
	 * Change question text according to transformations defined in TransformationArray.transformationsList
	 * @param question Question to transform
	 * @return Transformed question
	 */
	private void transformQuestion(Question question) {
		for (Transformation transformation : TransformationArray.transformationsList) {
			question.applyTransformationIfUseful(transformation);
		}
	}

	/***
	 * Detect third person pronoun in question. If it is presented, it uses concepts from older questions
	 * @param question Text of question
	 * @param request Request from web interface
	 * @return response of yodaQA
	 */
	private String askQuestion(Question question, Request request, Dialog dialog) {
		ArrayDeque<Concept> _concepts = new ArrayDeque<>();
		String artificialClue = "";
		if (isThirdPersonPronouns(question.getTransformedQuestionText())) {
			logger.info("Coreference resolurion used for: {}", question.getTransformedQuestionText());
			_concepts = dialog.getConceptMemorizer().getConcepts();
			artificialClue = dialog.getClueMemorizer().getClue();
		}
		CommunicationHandler communicationHandler = new CommunicationHandler();
		return communicationHandler.getPOSTResponse(yodaQAURL + "/q", request, question.getTransformedQuestionText(), _concepts, artificialClue);
	}

	/***
	 * Detects if there is pronouns in the third person in question text.
	 * @param question Question to check for pronoun
	 * @return TRUE if there is third person pronoun in question
	 */
	private boolean isThirdPersonPronouns(String question) {
		String[] thirdPersonPronouns = {"he", "she", "it", "its", "his", "hers", "him", "her", "they", "them", "their", "theirs"};
		for (int i = 0; i < thirdPersonPronouns.length; i++) {
			if (isContain(question.toLowerCase(), thirdPersonPronouns[i])) {
				return true;
			}
		}
		return false;
	}
}
