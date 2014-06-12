package pl.grapwr.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import pl.grapwr.R;
import pl.grapwr.R.id;
import pl.grapwr.data.Answer;
import pl.grapwr.data.Question;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends Activity
{
	private PlaceholderFragment fragment;
	private static final String TAG = "GameActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);

		if (savedInstanceState == null)
		{
			Log.d(TAG, "savedInstanceState == null");

			fragment = (PlaceholderFragment) getFragmentManager().findFragmentById(R.id.container);

			if (fragment == null)
			{
				FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
				fragmentTransaction.add(R.id.container, new PlaceholderFragment());
				fragmentTransaction.commit();
			}

			try
			{
				RotationHandler.createInstance((ArrayList<Question>) getIntent().getSerializableExtra("questions"));
				Log.d(TAG, "questions size: " + RotationHandler.getInstance().questions.size());

			}
			catch (Exception e)
			{
				Log.e(TAG, "onCreate", e);
			}
		}
		else
		{
			Log.d(TAG, "savedInstanceState != null");
			fragment = (PlaceholderFragment) getFragmentManager().findFragmentByTag("PlaceHolder");
		}

	}

	@Override
	public void onBackPressed()
	{
		RotationHandler.dispose();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings)
		{
			Intent i = new Intent(this, FinishActivity.class);
			this.startActivity(i);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment
	{
		View rootView;

		// private ArrayList<Question> questions;
		private Random random = new Random(System.nanoTime());
		private TextView textViewGame1;
		private TextView textViewGame2;
		private ProgressBar progressBarGame1;
		private ProgressBar progressBarGame2;
		private CheckBox[] checkBoxes;
		private Button buttonGame1;
		private Question currentQuestion;
		private ArrayList<Answer> answers;
		private boolean answered = false;

		public PlaceholderFragment()
		{
			// questions = ((GameActivity) getActivity()).questions;
			Log.d(TAG + ".Fragment.Rotation", "constructor");
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			rootView = inflater.inflate(R.layout.fragment_game, container, false);

			Log.d(TAG + ".Fragment.Rotation", "onCreateView");

			initWidgets();
			fillQuestion();

			return rootView;
		}

		@Override
		public void onStop()
		{
			Log.d(TAG + ".Fragment.Rotation", "onStop");

			RotationHandler.getInstance().setRotated();

			for (int i = 0; i < 8; i++)
			{
				if (checkBoxes[i].isChecked())
					RotationHandler.getInstance().checkAnswer(i);
			}

			super.onStop();
		}

		private void initWidgets()
		{
			textViewGame1 = (TextView) rootView.findViewById(id.textViewGame1);
			textViewGame2 = (TextView) rootView.findViewById(id.textViewGame2);
			progressBarGame1 = (ProgressBar) rootView.findViewById(id.progressBarGame1);
			progressBarGame2 = (ProgressBar) rootView.findViewById(id.progressBarGame2);
			progressBarGame2.setMax(RotationHandler.getInstance().completitionMaxProgress);

			checkBoxes = new CheckBox[] { (CheckBox) rootView.findViewById(id.checkBoxGame1), (CheckBox) rootView.findViewById(id.checkBoxGame2),
					(CheckBox) rootView.findViewById(id.checkBoxGame3), (CheckBox) rootView.findViewById(id.checkBoxGame4),
					(CheckBox) rootView.findViewById(id.checkBoxGame5), (CheckBox) rootView.findViewById(id.checkBoxGame6),
					(CheckBox) rootView.findViewById(id.checkBoxGame7), (CheckBox) rootView.findViewById(id.checkBoxGame8) };
			buttonGame1 = (Button) rootView.findViewById(id.buttonGame1);

			if (buttonGame1 != null)
				buttonGame1.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v)
					{
						if (answered)
						{
							RotationHandler.getInstance().hasBeenChecked = false;
							fillQuestion();
						}
						else
						{
							RotationHandler.getInstance().hasBeenChecked = true;
							checkQuestion();
						}
					}
				});
		}

		private void fillQuestion()
		{
			Log.d("ROTATOR", "answered = " + answered);

			RotationHandler rh = RotationHandler.getInstance();

			if (rh.hasRotated() && rh.getLastQuestionNumber() != -1)
			{
				currentQuestion = rh.getLastQuestion();
				answers = rh.getAnswers();
			}
			else
			{
				currentQuestion = rh.getRandomQuestion(random);
				while (currentQuestion.getRemainAnswers() < 1)
					currentQuestion = rh.getRandomQuestion(random);

				answers = currentQuestion.getAnswers();
				Collections.shuffle(answers, random);
				rh.setAnswers(answers);
				buttonGame1.setText(R.string.check_answer);
			}

			answered = false;
			textViewGame1.setText(currentQuestion.getText());
			textViewGame2.setText("");

			int activeCheckBoxes = answers.size();

			for (int i = 8; i > activeCheckBoxes; i--)
			{
				CheckBox checkBox = checkBoxes[i - 1];
				checkBox.setChecked(false);
				checkBox.setVisibility(View.INVISIBLE);

			}

			for (int i = 0; i < activeCheckBoxes; i++)
			{

				CheckBox checkBox = checkBoxes[i];
				checkBox.setChecked(rh.wasAnswerChecked(i));
				checkBox.setVisibility(View.VISIBLE);
				((TextView) checkBox).setTextColor(Color.BLACK);
				checkBox.setClickable(true);
				checkBox.setText((CharSequence) answers.get(i).getAnswer());
				checkBox.setTypeface(null, Typeface.NORMAL);
			}

			if (rh.hasRotated() && rh.hasBeenChecked)
				checkQuestion();

			rh.resetRotated();
		}

		private void checkQuestion()
		{
			int activeCheckBoxes = answers.size();
			boolean correct = true;

			for (int i = 1; i <= activeCheckBoxes; i++)
			{
				Answer tempAnswer = answers.get(i - 1);
				CheckBox checkBox = checkBoxes[i - 1];
				checkBox.setClickable(false);
				if (tempAnswer.isCorrect())
					checkBox.setTypeface(null, Typeface.BOLD);

				if (checkBox.isChecked() ^ tempAnswer.isCorrect())
				{
					((TextView) checkBox).setTextColor(Color.RED);
					correct = false;
				}
				else
				{
					((TextView) checkBox).setTextColor(0xFF00E300);
				}

			}

			if (!RotationHandler.getInstance().hasRotated())
			{
				if (correct)
				{
					textViewGame2.setText(R.string.good_answer);
					RotationHandler.getInstance().increaseGoodAnswers();
					currentQuestion.goodAnswer();
					if (currentQuestion.getRemainAnswers() < 1)
					{
						progressBarGame2.setProgress(++RotationHandler.getInstance().completitionProgress);
						RotationHandler.getInstance().questions.remove(currentQuestion);
					}

				}
				else
				{
					textViewGame2.setText(R.string.wrong_answer);
					RotationHandler.getInstance().increaseBadAnswers();
					currentQuestion.wrongAnswer();
				}
			}

			if (RotationHandler.getInstance().questions.isEmpty())
			{
				Toast.makeText(this.getActivity(), "Koniec nauki", Toast.LENGTH_LONG).show();

				Intent i = new Intent(this.getActivity(), FinishActivity.class);
				i.putExtra("goodAnswers", RotationHandler.getInstance().getGoodAnswers());
				i.putExtra("badAnswers", RotationHandler.getInstance().getBadAnswers());
				this.startActivity(i);
			}

			progressBarGame1.setMax((RotationHandler.getInstance().getBadAnswers() + RotationHandler.getInstance().getGoodAnswers()) * 100);
			if (RotationHandler.getInstance().getBadAnswers() == 0)
				progressBarGame1.setProgress(progressBarGame1.getMax());
			else
				progressBarGame1.setProgress(RotationHandler.getInstance().getGoodAnswers() * 100 / RotationHandler.getInstance().getBadAnswers() * 100);

			buttonGame1.setText(R.string.next_question);
			answered = true;
		}
	}

	private static class RotationHandler
	{
		private static RotationHandler instance;

		private ArrayList<Question> questions;
		private ArrayList<Answer> lastAnswers;
		private boolean[] lastCheckedAnswers;
		private int lastQuestion;
		private boolean rotation;
		private int goodAnswers;
		private int badAnswers;
		private int completitionProgress, completitionMaxProgress;
		private boolean hasBeenChecked;

		public static RotationHandler createInstance(ArrayList<Question> questions)
		{
			if (instance == null)
			{
				instance = new RotationHandler();
				instance.questions = questions;
				instance.lastQuestion = -1;
				instance.rotation = false;
				instance.goodAnswers = 0;
				instance.badAnswers = 0;
				instance.completitionProgress = 0;
				instance.completitionMaxProgress = questions.size();
				instance.hasBeenChecked = false;
			}

			return instance;
		}

		public static void dispose()
		{
			instance = null;
		}

		public static RotationHandler getInstance()
		{
			return instance;
		}

		public int getLastQuestionNumber()
		{
			return lastQuestion;
		}

		public Question getLastQuestion()
		{
			return questions.get(lastQuestion);
		}

		public void randomizeQuestion(Random random)
		{
			lastQuestion = random.nextInt(questions.size());
		}

		public Question getRandomQuestion(Random random)
		{
			randomizeQuestion(random);
			return getLastQuestion();
		}

		public void setRotated()
		{
			rotation = true;
		}

		public void resetRotated()
		{
			rotation = false;
		}

		public boolean hasRotated()
		{
			return rotation;
		}

		public void setAnswers(ArrayList<Answer> lastAnswers)
		{
			this.lastAnswers = lastAnswers;
			lastCheckedAnswers = new boolean[lastAnswers.size()];
		}

		public ArrayList<Answer> getAnswers()
		{
			return lastAnswers;
		}

		public void checkAnswer(int i)
		{
			lastCheckedAnswers[i] = true;
		}

		public boolean wasAnswerChecked(int i)
		{
			return lastCheckedAnswers[i];
		}

		public void increaseGoodAnswers()
		{
			goodAnswers++;
		}

		public void increaseBadAnswers()
		{
			badAnswers++;
		}

		public int getGoodAnswers()
		{
			return goodAnswers;
		}

		public int getBadAnswers()
		{
			return badAnswers;
		}

	}

}
