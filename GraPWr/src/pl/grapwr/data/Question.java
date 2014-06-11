package pl.grapwr.data;

import java.io.Serializable;
import java.util.ArrayList;

public class Question implements Serializable
{
	private static final long serialVersionUID = 1L;
	private ArrayList<Answer> answers;
	private String text;
	private int remainAnswers;
	private int answerTimesFail;

	public Question()
	{
		this.text = "";
		this.answers = new ArrayList<Answer>(8);
	}

	public int getRemainAnswers()
	{
		return remainAnswers;
	}

	public void setRemainAnswers(int remainAnswers)
	{
		this.remainAnswers = remainAnswers;
	}

	public int getAnswerTimesFail()
	{
		return answerTimesFail;
	}

	public void setAnswerTimesFail(int answerTimesFail)
	{
		this.answerTimesFail = answerTimesFail;
	}

	public void wrongAnswer()
	{
		remainAnswers += answerTimesFail;
	}

	public void goodAnswer()
	{
		remainAnswers--;
	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public ArrayList<Answer> getAnswers()
	{
		return answers;
	}

	public void addAnswer(Answer answer)
	{
		answers.add(answer);
	}

}
