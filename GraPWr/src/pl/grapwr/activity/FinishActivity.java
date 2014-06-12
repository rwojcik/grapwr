package pl.grapwr.activity;

import pl.grapwr.R;
import pl.grapwr.R.id;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class FinishActivity extends Activity {
	private int goodAnswers = 0;
	private int badAnswers = 0;
	private TextView textViewFinish3;
	private Button buttonFinish1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_finish);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
		goodAnswers = getIntent().getIntExtra("goodAnswers", goodAnswers);
		badAnswers = getIntent().getIntExtra("badAnswers", badAnswers);
	}

	@Override
	protected void onStart() {
		super.onStart();
		initWidgets();
	}

	private void initWidgets() {
		textViewFinish3 = (TextView) findViewById(id.textViewFinish3);
		textViewFinish3.setText(goodAnswers + "/" + badAnswers);
		buttonFinish1 = (Button) findViewById(id.buttonFinish1);
		buttonFinish1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				newGame();
			}

		});

	}

	private void newGame() {

		Intent i = new Intent(this, MainActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // czyszczenie stosu -> brak
													// leaka
		this.startActivity(i);
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	//
	// // Inflate the menu; this adds items to the action bar if it is present.
	// getMenuInflater().inflate(R.menu.finish, menu);
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

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_finish, container, false);
			return rootView;
		}
	}

}
