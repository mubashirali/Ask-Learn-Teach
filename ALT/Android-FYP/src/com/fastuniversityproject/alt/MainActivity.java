package com.fastuniversityproject.alt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.fastuniversityproject.alt.R;

public class MainActivity extends Activity implements
		TextToSpeech.OnInitListener {

	private final static String TAG = MainActivity.class.getSimpleName();

	protected static final int RESULT_SPEECH = 101;
	protected static final int RESULT_TTS_CHECK = 102;

	private Button btnSubmit;
	private ImageButton btnStart;
	private EditText question;
	private TextView answer;
	private TextView Head;
	private TextToSpeech textToSpeech;
	private TextView textqs;
	private static ArrayList<String> summery = new ArrayList<String>();
	private ProgressBar prog;
	private ProgressDialog dialog;

	// private AsyncHttpClient httpClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		textToSpeech = new TextToSpeech(this, this);
		prog = (ProgressBar) findViewById(R.id.pB);
		// dialog (ProgressDialog)
		btnStart = (ImageButton) findViewById(R.id.button_start);
		question = (EditText) findViewById(R.id.Et_qs);
		answer = (TextView) findViewById(R.id.textView_answer);
		Head = (TextView) findViewById(R.id.texthead2);
		btnSubmit = (Button) findViewById(R.id.btnSend);
		textqs = (TextView) findViewById(R.id.textqs);
		textqs.setText("Question: ");

		btnStart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {

				if (textToSpeech != null && textToSpeech.isSpeaking()) {
					textToSpeech.stop();
				}

				Intent intent = new Intent(
						RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
				intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
						RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

				try {
					startActivityForResult(intent, RESULT_SPEECH);
					// question.setText("");
				} catch (ActivityNotFoundException aex) {
					Toast toast = Toast.makeText(getApplicationContext(),
							"Your device doesn't support Speech to Text",
							Toast.LENGTH_SHORT);
					toast.show();
				}
			}
		});

		// Text To Speech Engine Check. Buggy on ICS and Above.
		// Intent checkTTSIntent = new Intent();
		// checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		// startActivityForResult(checkTTSIntent, RESULT_TTS_CHECK);

		btnSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				answer.setText("");
				new CallServer().execute();
			}
		});
	}

	public class CallServer extends AsyncTask<String, Integer, String> {

		@SuppressLint("NewApi")
		@Override
		protected String doInBackground(String... arg0) {
			Socket socket = null;
			DataOutputStream dataOutputStream = null;
			DataInputStream dataInputStream = null;
			int size = 0;

			//54.187.211.217
			try {
				socket = new Socket("54.187.211.217", 8081);
				dataOutputStream = new DataOutputStream(
						socket.getOutputStream());
				dataInputStream = new DataInputStream(socket.getInputStream());
				if (!question.toString().isEmpty()) {
					dataOutputStream
							.writeUTF(question.getText().toString() + '\n');

					size = dataInputStream.read();
					if (size > 0) {

						for (int i = 0; i < size - 1; i++) {
							summery.add(dataInputStream.readUTF());
						}
					}

					else {
						String text = "Sorry! Out of domain Question.";
						summery.add(text);
					}
					return "1";
				}
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return "0";
		}

		protected void onPreExecute() {
			super.onPreExecute();
			btnSubmit.setEnabled(false);
			prog.setVisibility(ProgressBar.VISIBLE);
		}

		protected void onPostExecute(String result) {
			// String insert = "";
			prog.setVisibility(ProgressBar.INVISIBLE);
			int count = 0;
			if (summery.get(0).matches("Sorry! Out of domain Question.")) {
				answer.append(summery.get(0));
			} else {
				answer.append("TOP ANSWERS ARE:" + '\n');
				for (String str : summery) {
					count++;
					answer.append(count + ") " + str + '\n');
				}
			}
			summery.clear();
			btnSubmit.setEnabled(true);
			speakOut(answer.getText().toString());
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case RESULT_TTS_CHECK:
			if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
				// Success. Create TTS Instance
				textToSpeech = new TextToSpeech(this, this);
			} else {
				// Missing Data. Install Text To Speech Engine
				Log.v(TAG, "Missing Data. Re-Directing to Install Page.");
				Intent installIntent = new Intent();
				installIntent
						.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
				startActivity(installIntent);
			}
			break;
		case RESULT_SPEECH:
			if (resultCode == RESULT_OK && null != data) {
				ArrayList<String> text = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				question.setText(text.get(0));
				Log.v(TAG, "Speech Recognized: " + text.get(0));
				// getMeaningOnline(text.get(0));

			}
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {

			int result = textToSpeech.setLanguage(Locale.US);

			if (result == TextToSpeech.LANG_MISSING_DATA
					|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
				Log.e(TAG, "Language is not supported");
			} else {
				String text = "Kindly ask any question related to chemistry!";
				Head.setText(text);
				speakOut(text);
			}

		} else {
			Log.e(TAG, "Initilization Failed!");
		}
	}

	private void speakOut(String text) {
		Log.v(TAG, "Speaking: " + text);
		textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
	}

	/*
	 * private void getMeaningOnline(String word) { if(word.length() < 2) {
	 * return; } RequestParams params = new RequestParams("term", word);
	 * httpClient.get("http://api.urbandictionary.com/v0/define", params, new
	 * JsonHttpResponseHandler() {
	 * 
	 * @Override public void onSuccess(JSONObject jsonObj) {
	 * super.onSuccess(jsonObj); try {
	 * if(!jsonObj.getString("result_type").equals("no_results")) { JSONArray
	 * listArr = jsonObj.getJSONArray("list"); JSONObject listObj =
	 * listArr.getJSONObject(0); String definition =
	 * listObj.getString("definition"); speakOut(definition); } else {
	 * Log.v(TAG, "Word not found online"); } } catch (JSONException jexp) {
	 * Log.e(TAG, jexp.getMessage()); } }
	 * 
	 * }); }
	 */
	@Override
	protected void onDestroy() {
		if (textToSpeech != null) {
			textToSpeech.stop();
			textToSpeech.shutdown();
		}
		super.onDestroy();
	}

}
