package ru.expendables.speechpad;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import ru.expendables.speechpad.utils.HttpResponseGetter;
import ru.expendables.speechpad.utils.LanguageListParser;
import ru.expendables.speechpad.utils.ProgressBarViewer;

import org.json.JSONObject;


import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import static ru.expendables.speechpad.utils.HttpResponseGetter.streamToString;


public class TranslateActivity extends FragmentActivity {
    private static final int REQUEST_CODE_ACTIVITY_LANGUAGE = 150;

    private Map<String, ArrayList<String>> languageMap = new HashMap<>();
    private String fromLang = "en";
    private String toLang = "ru";
    private Button buttonFrom;
    private Button buttonTo;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);

        String API_KEY = getResources().getString(R.string.yandex_API);
        String URL = getResources().getString(R.string.url_lang_list);


        buttonFrom = (Button) findViewById(R.id.from_lang);
        buttonTo = (Button) findViewById(R.id.to_lang);
        final ImageButton buttonSwitchLang = (ImageButton) findViewById(R.id.switch_lang);
        final ImageButton buttonToHistory = (ImageButton) findViewById(R.id.to_history);
        final Button buttonTranslate = (Button) findViewById(R.id.translate);

        final EditText editText = (EditText) findViewById(R.id.editText);
        editText.setText(MainActivity.toTranslate);
        final TextView textView = (TextView) findViewById(R.id.textView);

        restore(savedInstanceState);
        if (languageMap.isEmpty())
            new DownloadLanguageList().execute(URL + API_KEY);



        buttonTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String requestText = editText.getText().toString();

                if (requestText.length() == 0) {
                    ShowMessage("Field must not be empty");
                }
                try {
                    new TranslatedTextGetter(fromLang, toLang, requestText).execute();
                }
                catch (Exception e) {
                    ShowMessage("Can't be translated");
                    Log.e("TranslateActivity", e.toString());
                }
            }
        });


        buttonFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivityForResult(
                    languageListIntent(
                        fromLang, toLang, "from_lang_change"
                ), REQUEST_CODE_ACTIVITY_LANGUAGE);
            }
        });

        buttonTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivityForResult(
                        languageListIntent(
                                fromLang, toLang, "to_lang_change"
                        ), REQUEST_CODE_ACTIVITY_LANGUAGE);
            }
        });

        buttonSwitchLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonFrom.setText(toLang);
                buttonTo.setText(fromLang);
                fromLang = buttonFrom.getText().toString();
                toLang = buttonTo.getText().toString();

                String requestText = editText.getText().toString();
                String responseText = textView.getText().toString();

                if (responseText.length() != 0) {
                    textView.setText(requestText);
                    editText.setText(responseText);
                }
            }
        });

        buttonSwitchLang.callOnClick();
        buttonTranslate.callOnClick();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_ACTIVITY_LANGUAGE:
                    String action = data.getStringExtra("action");
                    if (action != null) {
                        if (action.equals("lang_changed")) {
                            fromLang = data.getStringExtra("from_lang");
                            toLang = data.getStringExtra("to_lang");
                            buttonFrom.setText(fromLang);
                            buttonTo.setText(toLang);
                        }
                    }
                    break;
            }
        } else {
            ShowMessage("Can't choose language");
        }

    }

    private Intent languageListIntent(String from, String to, String action) {
        Intent intent = new Intent(TranslateActivity.this, LanguageList.class);
        intent.putExtra("to_lang", to);
        intent.putExtra("from_lang", from);
        intent.putExtra("action", action);
        intent.putExtra("map", (java.io.Serializable) languageMap);
        return intent;
    }

    private class DownloadLanguageList extends AsyncTask<String, Void, String> {
        private static final String progressBarMsg = "Downloading Language List";

        public DownloadLanguageList() {}

        protected void onPreExecute() {
            ProgressBarViewer.view(TranslateActivity.this, progressBarMsg);
        }

        protected String doInBackground(String ... urls) {
            try {
                InputStream in = new java.net.URL(urls[0]).openStream();
                JSONObject json = new JSONObject(streamToString(in));
                String API_ARRAY_NAME = getResources().getString(R.string.api_lang_array_name);

                languageMap = LanguageListParser.parseLanguageList(
                        LanguageListParser.getListFromJSON(
                                json.getJSONArray(API_ARRAY_NAME)
                        )
                );
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return languageMap.keySet().toString();
        }

        protected void onPostExecute(String result) {
            ProgressBarViewer.hide();
        }
    }

    private class TranslatedTextGetter extends AsyncTask<String, Void, String> {

        private final String fromLang;
        private final String toLang;
        private final String text;

        public TranslatedTextGetter(String fromLang, String toLang, String text) {
            this.fromLang = fromLang;
            this.toLang = toLang;
            this.text = text;
        }

        @Override
        protected String doInBackground(String ... params) {
            String urlTranslate = getResources().getString(R.string.url_translate);
            String translatedText = null;

            try {
                String encodedText = URLEncoder.encode(text, "UTF-8");
                String requestURL = urlTranslate + "&lang=" + fromLang + "-" + toLang + "&text=" + encodedText;

                JSONObject json = HttpResponseGetter.getResponseByUrl(requestURL);
                Integer status = json.getInt("code");
                if (status != 200) {
                    ShowMessage("Can't be translated: ");
                }
                else {
                    translatedText = json.getString("text");
                    translatedText = translatedText.substring(2, translatedText.length() - 2);
                }
            } catch (Exception e) {
                Log.e("TranslateActivity", e.toString());
            }
            return translatedText;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                result = result.replaceAll("\\\\n", "\\\n");
                setTextView(result);
            }
            else {
                Log.e("TranslateActivity", "Response is null");
                ShowMessage("Can't be translated");
            }
        }
    }

    private void setTextView(String text) {
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(text);
    }

    private void ShowMessage(String message) {
        Toast.makeText(TranslateActivity.this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_translate, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d("res", "1");
        restore(savedInstanceState);
    }

    public void restore (Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            buttonFrom.setText(savedInstanceState.getString("fromLang"));
            buttonTo.setText(savedInstanceState.getString("toLang"));
            fromLang = savedInstanceState.getString("fromLang");
            toLang = savedInstanceState.getString("toLang");
            languageMap = (Map<String, ArrayList<String>>) savedInstanceState.getSerializable("languages");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.d("save", "1");
        savedInstanceState.putString("fromLang", fromLang);
        savedInstanceState.putString("toLang", toLang);
        savedInstanceState.putSerializable("languages", (java.io.Serializable) languageMap);
    }

    public void onClickMicro(View view) {

        Intent intent = new Intent(TranslateActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
