package pl.grapwr.data;

import java.io.Serializable;

public class Answer implements Serializable
{
	private static final long serialVersionUID = 1L;
	private boolean correct;
	private String answer;

	public Answer()
	{
		this.correct = false;
		this.answer = "";
	}

	public boolean isCorrect()
	{
		return correct;
	}

	public void setCorrect(boolean correct)
	{
		this.correct = correct;
	}

	public String getAnswer()
	{
		return answer;
	}

	public void setAnswer(String answer)
	{
		this.answer = answer;
	}

}
