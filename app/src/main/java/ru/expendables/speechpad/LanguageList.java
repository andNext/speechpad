package ru.expendables.speechpad;


import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.Map;
import java.util.Set;

public class LanguageList extends Activity {
    private Map <String, ArrayList<String>> languageMap = new HashMap<>();
    private String action;
    private String fromLang;
    private String toLang;
    private String [] langArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_list);

        Intent intent = getIntent();
        fetchParams(intent);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(LanguageList.this,
                android.R.layout.simple_list_item_1, langArray);

        ListView langList = (ListView) findViewById(R.id.lang_list);
        langList.setAdapter(adapter);
        langList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
                String selectedFromList = (listView.getItemAtPosition(position).toString());

                if (action.equals("from_lang_change")) {
                    ArrayList availableLangs = languageMap.get(selectedFromList);

                    if (!availableLangs.contains(toLang))
                        toLang = (String) availableLangs.get(0);

                    setResult(RESULT_OK, ReturnLanguageIntent(selectedFromList, toLang, "lang_changed"));

                } else if (action.equals("to_lang_change")) {
                    ArrayList availableLangs = languageMap.get(fromLang);

                    if (availableLangs.contains(selectedFromList))
                        setResult(RESULT_OK,ReturnLanguageIntent(fromLang, selectedFromList, "lang_changed"));
                    else
                        Toast.makeText(
                                getApplicationContext(),
                                "No translation available for your language combination\nPlease, select another language",
                                Toast.LENGTH_SHORT
                        ).show();
                }
                finish();
            }
        });
    }

    public void fetchParams (Intent intent) {
        action = intent.getStringExtra("action");
        if (! (action.equals("from_lang_change") || (action.equals("to_lang_change"))) ) {
            Toast.makeText(
                    getApplicationContext(),
                    "Unexpected action brought you here",
                    Toast.LENGTH_SHORT
            ).show();
            intent = new Intent(this, TranslateActivity.class);
            setResult(RESULT_CANCELED, intent);
            finish();
        }
        else {
            fromLang = intent.getStringExtra("from_lang");
            toLang = intent.getStringExtra("to_lang");
            languageMap = (HashMap <String, ArrayList<String>>) intent.getSerializableExtra("map");
        }

        if (action.equals("from_lang_change")) {
            Set<String> set = languageMap.keySet();
            langArray = new String[set.size()];
            set.toArray(langArray);
        }
        else {
            ArrayList availableLangs = languageMap.get(fromLang);
            langArray = new String[availableLangs.size()];
            availableLangs.toArray(langArray);
        }
    }

    private Intent ReturnLanguageIntent(String from, String to, String action) {
        Intent intent = new Intent(LanguageList.this, TranslateActivity.class);
        intent.putExtra("to_lang", to);
        intent.putExtra("from_lang", from);
        intent.putExtra("action", action);
        return intent;
    }

    //---------------------------------------------------------------------------------------------

}
