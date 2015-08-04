package ru.expendables.speechpad.utils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by eugene on 24.02.15.
 */
public class LanguageListParser {
    public static List<String> getListFromJSON (JSONArray jArray) throws JSONException {
        List <String> languageList = new ArrayList<>();
        int length = jArray.length();
        for (int i=0; i < length; i++)
            languageList.add(jArray.getString(i));
        return languageList;
    }


    public static Map<String, ArrayList<String>> parseLanguageList (List<String> languages) {
        Map <String, ArrayList<String>> languageMap = new HashMap<>();
        String curLang = "";
        ArrayList<String> curLangTo = new ArrayList<>();
        for (String pair : languages) {
            String[] array = pair.split("-");
            if (languageMap.containsKey(array[0]))
                curLangTo.add(array[1]);
            else {
                languageMap.put(curLang, curLangTo);
                curLangTo = new ArrayList<>();
                curLang = array[0];
                curLangTo.add(array[1]);
            }
        }
        languageMap.remove("");
        return languageMap;
    }
}
