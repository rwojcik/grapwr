package pl.grapwr.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.acl.Permission;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import pl.grapwr.R;
import pl.grapwr.activity.GameActivity;
import pl.grapwr.appsettings.Settings;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class QuestionBankLoader
{
	private QuestionBankLoader()
	{
	}

	public static AsyncTask<String, Integer, ArrayList<Question>> loadBase(Context context, int answerTimes, int answerTimesFail)
	{
		// TODO SprawdŸ czy jest dostêpny internet jak tak to pobiedz XML i mo¿e aktualizuj bazê

		String location = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + Settings.PIO_W_LOCATION_OFFLINE;

		Log.d("Path", "Path: " + location);

		return downloadBase(context, Settings.PIO_W_LOCATION, answerTimes, answerTimesFail);
	}

	static AsyncTask<String, Integer, ArrayList<Question>> loadBaseFromSD(Context context, String location, int answerTimes, int answerTimesFail)
	{
		class DownloadingTask extends AsyncTask<String, Integer, ArrayList<Question>>
		{
			private ProgressDialog progressDialog;
			private String location;
			private Context context;
			private String TAG = "MainActivity.loadBase";
			private int answerTimes, answerTimesFail;

			public DownloadingTask(Context context, int answerTimes, int answerTimesFail)
			{
				this.context = context;
				this.answerTimes = answerTimes;
				this.answerTimesFail = answerTimesFail;
			}

			@Override
			protected void onPreExecute()
			{
				super.onPreExecute();
				Thread.currentThread().setName("SearchingTask");
				progressDialog = new ProgressDialog(context);
				progressDialog.setMessage(context.getText(R.string.downloading));
				progressDialog.setTitle(context.getText(R.string.please_wait));
				progressDialog.show();
			}

			@Override
			protected ArrayList<Question> doInBackground(String... params)
			{
				location = params[0];
				ArrayList<Question> questions = new ArrayList<Question>(81);
				FileInputStream inputStream = null;

				try
				{
					File inputFile = new File(location);
					inputStream = new FileInputStream(inputFile);
					BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

					String inputLine;
					while ((inputLine = in.readLine()) != null)
					{

						if (inputLine.length() > 0 && inputLine.charAt(0) == 'X')
						{
							Question question = new Question();

							char[] chars = inputLine.toCharArray();

							question.setText(in.readLine());
							for (int i = 1; i < chars.length; i++)
							{
								if (chars[i] == '1' || chars[i] == '0')
								{
									Answer answer = new Answer();
									answer.setCorrect(chars[i] == '1');
									answer.setAnswer(in.readLine());
									question.addAnswer(answer);
								}
							}
							question.setRemainAnswers(answerTimes);
							question.setAnswerTimesFail(answerTimesFail);
							questions.add(question);
						}
					}
					in.close();
				}
				catch (Exception e)
				{
					Log.e(TAG, "AsyncTask.doInBackground", e);
					Toast.makeText(context, R.string.fail, Toast.LENGTH_LONG).show();

					try
					{
						inputStream.close();
					}
					catch (IOException e1)
					{
						Log.e(TAG, "AsyncTask.doInBackground", e1);
					}

					this.cancel(true);
				}

				return questions;
			}

			@Override
			protected void onPostExecute(ArrayList<Question> result)
			{
				super.onPostExecute(result);
				progressDialog.dismiss();

				try
				{
					Intent i = new Intent(context, GameActivity.class);
					i.putExtra("questions", result);
					// Log.d(TAG, "result size: " + result.size());
					context.startActivity(i);
				}
				catch (Exception e)
				{
					Toast.makeText(context, R.string.fail, Toast.LENGTH_LONG).show();
					Log.e(TAG, "AsyncTask.onPostExecute", e);
					this.cancel(true);
				}
			}
		}

		return new DownloadingTask(context, answerTimes, answerTimesFail).execute(location);
	}

	static AsyncTask<String, Integer, ArrayList<Question>> downloadBase(Context context, String location, int answerTimes, int answerTimesFail)
	{
		class DownloadingTask extends AsyncTask<String, Integer, ArrayList<Question>>
		{
			private ProgressDialog progressDialog;
			private String location;
			private Context context;
			private String TAG = "MainActivity.downloadBase";
			private int answerTimes, answerTimesFail;

			public DownloadingTask(Context context, int answerTimes, int answerTimesFail)
			{
				this.context = context;
				this.answerTimes = answerTimes;
				this.answerTimesFail = answerTimesFail;
			}

			@Override
			protected void onPreExecute()
			{
				super.onPreExecute();
				Thread.currentThread().setName("SearchingTask");
				progressDialog = new ProgressDialog(context);
				progressDialog.setMessage(context.getText(R.string.downloading));
				progressDialog.setTitle(context.getText(R.string.please_wait));
				progressDialog.show();
			}

			@Override
			protected ArrayList<Question> doInBackground(String... params)
			{
				location = params[0];
				ArrayList<Question> questions = new ArrayList<Question>(81);

				try
				{
					URL url = new URL(location);
					BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
					String inputLine;
					while ((inputLine = in.readLine()) != null)
					{

						if (inputLine.length() > 0 && inputLine.charAt(0) == 'X')
						{
							Question question = new Question();

							char[] chars = inputLine.toCharArray();

							question.setText(in.readLine());
							for (int i = 1; i < chars.length; i++)
							{
								Answer answer = new Answer();
								answer.setCorrect(chars[i] == '1');
								answer.setAnswer(in.readLine());
								question.addAnswer(answer);
							}
							question.setRemainAnswers(answerTimes);
							question.setAnswerTimesFail(answerTimesFail);
							questions.add(question);
						}
					}
					in.close();
				}
				catch (Exception e)
				{
					Log.e(TAG, "AsyncTask.doInBackground", e);
					Toast.makeText(context, R.string.fail, Toast.LENGTH_LONG).show();
					this.cancel(true);
				}

				return questions;
			}

			@Override
			protected void onPostExecute(ArrayList<Question> result)
			{
				super.onPostExecute(result);
				progressDialog.dismiss();

				try
				{
					Intent i = new Intent(context, GameActivity.class);
					i.putExtra("questions", result);
					// Log.d(TAG, "result size: " + result.size());
					context.startActivity(i);
				}
				catch (Exception e)
				{
					Toast.makeText(context, R.string.fail, Toast.LENGTH_LONG).show();
					Log.e(TAG, "AsyncTask.onPostExecute", e);
					this.cancel(true);
				}
			}

		}

		return new DownloadingTask(context, answerTimes, answerTimesFail).execute(location);
	}

	static AsyncTask<String, Integer, HashMap<String, Base>> downloadXML(Context context, String location)
	{
		class DownloadingTask extends AsyncTask<String, Integer, HashMap<String, Base>>
		{
			private String location;
			private Context context;
			private String TAG = "MainActivity.downloadXML";

			public DownloadingTask(Context context, String location)
			{
				this.context = context;
				this.location = location;
			}

			@Override
			protected void onPreExecute()
			{
				super.onPreExecute();
				Thread.currentThread().setName("DownloadingTask");
			}

			@Override
			protected HashMap<String, Base> doInBackground(String... params)
			{
				HashMap<String, Base> locations = new HashMap<String, Base>(2);
				InputStream stream = null;
				try
				{
					// URL url = new URL(location);
					// URI uri = new URI(location);
					// File xmlFile = new File(uri);
					// BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
					// String inputLine;

					URL url = new URL(location);
					stream = url.openStream();
					// Document doc = docBuilder.parse(stream);

					DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
					DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
					Document doc = dBuilder.parse(stream);

					// optional, but recommended
					// read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
					doc.getDocumentElement().normalize();

					// Log.d(TAG, "Root element :" + doc.getDocumentElement().getNodeName());

					NodeList nList = doc.getElementsByTagName("base");

					// Log.d(TAG, "----------------------------");

					for (int i = 0; i < nList.getLength(); i++)
					{

						Node nNode = nList.item(i);

						// Log.d(TAG, "\nCurrent Element :" + nNode.getNodeName());

						if (nNode.getNodeType() == Node.ELEMENT_NODE)
						{

							Element eElement = (Element) nNode;
							String name = eElement.getElementsByTagName("name").item(0).getTextContent();
							String httpLocation = eElement.getElementsByTagName("location").item(0).getTextContent();
							int version = Integer.parseInt(eElement.getElementsByTagName("version").item(0).getTextContent());
							int questions = Integer.parseInt(eElement.getElementsByTagName("capacity").item(0).getTextContent());

							locations.put(name, new Base(httpLocation, questions, version));

							// Log.d(TAG, "Base name : " + eElement.getElementsByTagName("name").item(0).getTextContent());
							// Log.d(TAG, "Base location : " + eElement.getElementsByTagName("location").item(0).getTextContent());
							// Log.d(TAG, "Base version : " + eElement.getElementsByTagName("version").item(0).getTextContent());
							// Log.d(TAG, "Base capacity : " + eElement.getElementsByTagName("capacity").item(0).getTextContent());

						}
					}

				}
				catch (Exception e)
				{
					Log.e(TAG, "doInBackground", e);
				}
				try
				{
					stream.close();
				}
				catch (IOException e)
				{
					Log.e(TAG, "doInBackground", e);
				}

				return locations;
			}

			@Override
			protected void onPostExecute(HashMap<String, Base> result)
			{
				super.onPostExecute(result);

				Log.d(TAG, "onPostExecute, result contains");

				Toast.makeText(context, String.format((String) context.getText(R.string.updates_downloaded), result.keySet().size()), Toast.LENGTH_LONG).show();

			}

		}

		return new DownloadingTask(context, location).execute();
	}
}
