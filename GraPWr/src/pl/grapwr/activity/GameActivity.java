package pl.grapwr.activity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

import pl.grapwr.R;
import pl.grapwr.R.id;
import pl.grapwr.data.Answer;
import pl.grapwr.data.Question;
import android.app.Activity;
import android.app.Fragment;
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
import android.widget.TextView;

public class GameActivity extends Activity
{

	private static final String TAG = "GameActivity";
	private ArrayList<Question> questions = null;
	private Random random = new Random(System.nanoTime());
	private TextView textViewGame1;
	// private CheckBox checkBoxGame1;
	// private CheckBox checkBoxGame2;
	// private CheckBox checkBoxGame3;
	// private CheckBox checkBoxGame4;
	// private CheckBox checkBoxGame5;
	// private CheckBox checkBoxGame6;
	// private CheckBox checkBoxGame7;
	// private CheckBox checkBoxGame8;
	private CheckBox[] checkBoxes;
	private Button buttonGame1;
	private Question currentQuestion;

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
		checkBoxes = new CheckBox[] { (CheckBox) findViewById(id.checkBoxGame1), (CheckBox) findViewById(id.checkBoxGame2),
				(CheckBox) findViewById(id.checkBoxGame3), (CheckBox) findViewById(id.checkBoxGame4), (CheckBox) findViewById(id.checkBoxGame5),
				(CheckBox) findViewById(id.checkBoxGame6), (CheckBox) findViewById(id.checkBoxGame7), (CheckBox) findViewById(id.checkBoxGame8) };
		buttonGame1 = (Button) findViewById(id.buttonGame1);

		if (buttonGame1 != null)
			buttonGame1.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v)
				{

				}
			});
	}

	private void fillQuestion()
	{
		currentQuestion = questions.get(random.nextInt(questions.size()));
		while (currentQuestion.getRemainAnswers() < 1)
			currentQuestion = questions.get(random.nextInt(questions.size()));

		textViewGame1.setText(currentQuestion.getText());

		ArrayList<Answer> answers = currentQuestion.getAnswers();
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
			checkBox.setText((CharSequence) answers.get(i - 1).getAnswer());

		}

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
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

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
