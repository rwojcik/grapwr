package pl.grapwr.activity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;

import pl.grapwr.R;
import pl.grapwr.R.id;
import pl.grapwr.appsettings.Settings;
import pl.grapwr.data.Answer;
import pl.grapwr.data.Question;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static String TAG = "MainActivity";
	private Button buttonMainStart;
	private Spinner spinnerMain;
	private TextView textViewMain3;
	private TextView textViewMain5;
	private SeekBar seekBarMain1;
	private SeekBar seekBarMain2;
	private int answerTimes = 3;
	private int answerTimesFail = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		initWidgets();
	}

	private void initWidgets() {
		buttonMainStart = (Button) findViewById(R.id.buttonMainStart);
		buttonMainStart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				downloadBase();
			}
		});

		spinnerMain = (Spinner) findViewById(id.spinnerMain);

		textViewMain3 = (TextView) findViewById(id.textViewMain3);
		seekBarMain1 = (SeekBar) findViewById(id.seekBarMain1);
		textViewMain3.setText((seekBarMain1.getProgress() + 1) + "");

		seekBarMain1.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// NOPE

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// textViewMain3.setText((seekBar.getProgress() + 1) + "");
				// answerTimes = seekBar.getProgress() + 1;

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

				textViewMain3.setText((progress + 1) + "");
				answerTimes = progress + 1;

			}
		});

		textViewMain5 = (TextView) findViewById(id.textViewMain5);
		seekBarMain2 = (SeekBar) findViewById(id.seekBarMain2);
		textViewMain5.setText((seekBarMain2.getProgress() + 1) + "");

		seekBarMain2.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// NOPE

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// textViewMain5.setText((seekBarMain2.getProgress() + 1) + "");
				// answerTimesFail = seekBarMain2.getProgress() + 1;

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

				textViewMain5.setText((progress + 1) + "");
				answerTimesFail = progress + 1;

			}
		});

	}

	public void downloadBase() {

		Class<?> c = Settings.class;
		try {
			Field locationField = c.getDeclaredField(spinnerMain.getSelectedItem().toString() + "_LOCATION");
			String location = (String) locationField.get(String.class);
			// Log.d(TAG, "answerTimes: " + answerTimes + ", answerTimesFail: "
			// + answerTimesFail);
			downloadBase(this, location, answerTimes, answerTimesFail);

		} catch (Exception e) {
			Log.e(TAG, "downloadBase", e);
		}
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	//
	// // Inflate the menu; this adds items to the action bar if it is present.
	// getMenuInflater().inflate(R.menu.main, menu);
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
	// return true;
	// }
	// return super.onOptionsItemSelected(item);
	// }

	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			return rootView;
		}
	}

	static AsyncTask<String, Integer, ArrayList<Question>> downloadBase(Context context, String location, int answerTimes, int answerTimesFail) {
		class DownloadingTask extends AsyncTask<String, Integer, ArrayList<Question>> {
			private ProgressDialog progressDialog;
			private String location;
			private Context context;
			private String TAG = "downloadBase";
			private int answerTimes = 3;
			private int answerTimesFail = 3;

			public DownloadingTask(Context context, int answerTimes, int answerTimesFail) {
				this.context = context;
				this.answerTimes = answerTimes;
				this.answerTimesFail = answerTimesFail;
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				Thread.currentThread().setName("SearchingTask");
				progressDialog = new ProgressDialog(context);
				progressDialog.setMessage(context.getText(R.string.downloading));
				progressDialog.setTitle(context.getText(R.string.please_wait));
				progressDialog.show();
			}

			@Override
			protected ArrayList<Question> doInBackground(String... params) {
				location = params[0];
				ArrayList<Question> questions = new ArrayList<Question>(81);

				try {
					URL url = new URL(location);
					BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
					String inputLine;
					while ((inputLine = in.readLine()) != null) {

						if (inputLine.length() > 0 && inputLine.charAt(0) == 'X') {
							Question question = new Question();

							char[] chars = inputLine.toCharArray();

							question.setText(in.readLine());
							for (int i = 1; i < chars.length; i++) {
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
				} catch (Exception e) {
					Log.e(TAG, "AsyncTask.doInBackground", e);
					Toast.makeText(context, R.string.fail, Toast.LENGTH_LONG).show();
					this.cancel(true);
				}

				return questions;
			}

			@Override
			protected void onPostExecute(ArrayList<Question> result) {
				super.onPostExecute(result);
				progressDialog.dismiss();

				try {
					Intent i = new Intent(context, GameActivity.class);
					i.putExtra("questions", result);
					Log.d(TAG, "result size: " + result.size());
					context.startActivity(i);
				} catch (Exception e) {
					Toast.makeText(context, R.string.fail, Toast.LENGTH_LONG).show();
					Log.e(TAG, "AsyncTask.onPostExecute", e);
					this.cancel(true);
				}
			}

		}

		return new DownloadingTask(context, answerTimes, answerTimesFail).execute(location);
	}

}
