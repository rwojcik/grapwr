package pl.grapwr.data;

public class Base
{

//	String name;
	String httpLocation;
	int questions;
	int version;

	public Base(String httpLocation, int questions, int version)
	{
//		this.name = name;
		this.httpLocation = httpLocation;
		this.questions = questions;
		this.version = version;
	}

//	public String getName()
//	{
//		return name;
//	}
//
//	public void setName(String name)
//	{
//		this.name = name;
//	}

	public String getHttpLocation()
	{
		return httpLocation;
	}

	public void setHttpLocation(String httpLocation)
	{
		this.httpLocation = httpLocation;
	}

	public int getQuestions()
	{
		return questions;
	}

	public void setQuestions(int questions)
	{
		this.questions = questions;
	}

}
