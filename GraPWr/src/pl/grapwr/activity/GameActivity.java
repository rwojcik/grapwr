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
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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

	private static final String TAG = "GameActivity";
	private ArrayList<Question> questions;
	private Random random = new Random(System.nanoTime());
	private TextView textViewGame1;
	private TextView textViewGame2;
	private ProgressBar progressBarGame1;
	private ProgressBar progressBarGame2;
	private CheckBox[] checkBoxes;
	private Button buttonGame1;
	private Question currentQuestion;
	private int goodAnswers = 0;
	private int badAnswers = 0;
	private ArrayList<Answer> answers;
	private boolean answered = false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);

		if (savedInstanceState == null)
		{
			getFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}

		try
		{
			questions = (ArrayList<Question>) getIntent().getSerializableExtra("questions");
			Log.d(TAG, "questions size: " + questions.size());

		}
		catch (Exception e)
		{
			Log.e(TAG, "onCreate", e);
		}
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		initWidgets();
		fillQuestion();
	}

	private void initWidgets()
	{
		textViewGame1 = (TextView) findViewById(id.textViewGame1);
		textViewGame2 = (TextView) findViewById(id.textViewGame2);
		progressBarGame1 = (ProgressBar) findViewById(id.progressBarGame1);
		// progressBarGame1.getProgressDrawable().setColorFilter(Color.GREEN, Mode.CLEAR);
		// progressBarGame1.setProgressDrawable(this.getResources().getDrawable(R.drawable.game_progressbar));

		progressBarGame2 = (ProgressBar) findViewById(id.progressBarGame2);
		// progressBarGame2.getProgressDrawable().setColorFilter(Color.GREEN, Mode.CLEAR);
		progressBarGame2.setMax(questions.size());

		checkBoxes = new CheckBox[] { (CheckBox) findViewById(id.checkBoxGame1), (CheckBox) findViewById(id.checkBoxGame2),
				(CheckBox) findViewById(id.checkBoxGame3), (CheckBox) findViewById(id.checkBoxGame4), (CheckBox) findViewById(id.checkBoxGame5),
				(CheckBox) findViewById(id.checkBoxGame6), (CheckBox) findViewById(id.checkBoxGame7), (CheckBox) findViewById(id.checkBoxGame8) };
		buttonGame1 = (Button) findViewById(id.buttonGame1);

		if (buttonGame1 != null)
			buttonGame1.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v)
				{
					if (answered)
						fillQuestion();
					else
						checkQuestion();
				}
			});
	}

	private void fillQuestion()
	{
		answered = false;
		buttonGame1.setText(R.string.check_answer);
		currentQuestion = questions.get(random.nextInt(questions.size()));
		while (currentQuestion.getRemainAnswers() < 1)
			currentQuestion = questions.get(random.nextInt(questions.size()));

		textViewGame1.setText(currentQuestion.getText());
		textViewGame2.setText("");

		answers = currentQuestion.getAnswers();
		Collections.shuffle(answers, random);

		int activeCheckBoxes = answers.size();

		for (int i = 8; i > activeCheckBoxes; i--)
		{
			CheckBox checkBox = checkBoxes[i - 1];
			checkBox.setChecked(false);
			checkBox.setVisibility(View.INVISIBLE);

		}

		for (int i = 1; i <= activeCheckBoxes; i++)
		{

			CheckBox checkBox = checkBoxes[i - 1];
			checkBox.setChecked(false);
			checkBox.setVisibility(View.VISIBLE);
			((TextView) checkBox).setTextColor(Color.BLACK);
			checkBox.setClickable(true);
			checkBox.setText((CharSequence) answers.get(i - 1).getAnswer());
			checkBox.setTypeface(null, Typeface.NORMAL);
		}

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

		if (correct)
		{
			textViewGame2.setText(R.string.good_answer);
			goodAnswers++;
			currentQuestion.goodAnswer();
			if (currentQuestion.getRemainAnswers() < 1)
			{
				progressBarGame2.setProgress(progressBarGame2.getProgress() + 1);
				questions.remove(currentQuestion);
			}

		}
		else
		{
			textViewGame2.setText(R.string.wrong_answer);
			badAnswers++;
			currentQuestion.wrongAnswer();
		}

		if (questions.isEmpty())
		{
			Toast.makeText(this, "Koniec nauki", Toast.LENGTH_LONG).show();

			Intent i = new Intent(this, FinishActivity.class);
			i.putExtra("goodAnswers", goodAnswers);
			i.putExtra("badAnswers", badAnswers);
			this.startActivity(i);
		}

		progressBarGame1.setMax(badAnswers + goodAnswers);
		if (badAnswers == 0)
			progressBarGame1.setProgress(goodAnswers);
		else
			progressBarGame1.setProgress(goodAnswers / badAnswers);

		buttonGame1.setText(R.string.next_question);
		answered = true;
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	//
	// // Inflate the menu; this adds items to the action bar if it is present.
	// getMenuInflater().inflate(R.menu.game, menu);
	// return true;
	// }
	//
	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// // Handle action bar item clicks here. The action bar will
	// // automatically handle clicks on the Home/Up button, so long
	// // as you specify a parent activity in AndroidManifest.xml.
	// int id = item.getItemId();
	// if (id == R.id.action_settings) {
	// Intent i = new Intent(this, FinishActivity.class);
	// this.startActivity(i);
	// return true;
	// }
	// return super.onOptionsItemSelected(item);
	// }

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment
	{

		public PlaceholderFragment()
		{
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			View rootView = inflater.inflate(R.layout.fragment_game, container, false);
			return rootView;
		}
	}

}
