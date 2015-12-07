package eu.ailao.hub.concepts;

/**
 * Created by Petr Marek on 01.12.2015.
 * Class of question concept
 */
public class Concept {
	private int pageID;
	private String fullLabel;
	private int questionNumber;

	public Concept(int pageID, String fullLabel, int questionNumber) {
		this.pageID = pageID;
		this.fullLabel = fullLabel;
		this.questionNumber = questionNumber;
	}

	public int getPageID() {
		return pageID;
	}

	public String getFullLabel() {
		return fullLabel;
	}

	public  int getQuestionNumber(){
		return questionNumber;
	}
}
