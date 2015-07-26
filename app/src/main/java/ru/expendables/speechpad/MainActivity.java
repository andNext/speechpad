package ru.expendables.speechpad;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import ru.yandex.speechkit.PhraseSpotter;
import ru.yandex.speechkit.PhraseSpotterListener;
import ru.yandex.speechkit.Error;
import ru.yandex.speechkit.SpeechKit;
import ru.yandex.speechkit.gui.RecognizerActivity;


public class MainActivity extends Activity
{
    private static final String TAG = "SpeechPad";

    public ImageButton startButton;
    private TextView resultView;
    private boolean dex = false; //press on botton
    public SQLiteDatabase mSqLiteDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
         *инициализация кнопки микрофона
        */
        startButton = (ImageButton)findViewById(R.id.startButton);
        startButton.setOnClickListener (new OnClickListener() {
            public void onClick (View v) {

               PhraseSpotter.stop();
               startVoiceRecognitionActivity();

            }
        });

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        /*
        Содержимое папки assets/model копируются в папку приложения, для того чтобы они были доступны
        механизм распознования
         */
        File wallpaperDirectory = new File(getFilesDir().getAbsolutePath() + "/model/");
        wallpaperDirectory.mkdirs();

        String path = getFilesDir().getAbsolutePath() + "/model/";
        Log.d(TAG, "Path: " + path);
        File f = new File(path);
        File file[] = f.listFiles();
        if(file.length == 0)
            copyAssets();

        //инициализация строки вывода
        resultView = (TextView)findViewById(R.id.result_view);
        DatabaseHelper mDatabaseHelper = new DatabaseHelper(this, "paddatabase.db", null, 1);

        mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();

        initialize();//инициализация механизма распознавания
    }

    /**
     * Инициализируется механизма распознавания.
     */
    private void initialize() {
        //SpeechKit.getInstance().configure(getBaseContext(), "8b1a122c-9942-4f0d-a1a6-10a18353131f");
        SpeechKit.getInstance().configure(getBaseContext(), "8f38a015-ea3b-411f-babb-59b6d9e415a1");

        PhraseSpotter.initialize(getFilesDir().getAbsolutePath() + "/model", new LogRecognitionListener());

        // PhraseSpotter начнет запись звука и поиск заданных фраз
        PhraseSpotter.start();
    }

    /**
     * копирования ассетов.
     */
    private void copyAssets() {
        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list("model");
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }
        for(String filename : files) {
            Log.d(TAG, filename);
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open("model/" + filename);
                File outFile = new File(getFilesDir().getAbsolutePath() + "/model/", filename);
                out = new FileOutputStream(outFile);
                copyFile(in, out);
            } catch(IOException e) {
                Log.e("tag", "Failed to copy asset file: " + filename, e);
            }
            finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
    }

    /**
     * копирование файла.
     */
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

    private void onoff(){
        //анимация кнопки микрофона
        if(!dex){
            startButton.setImageResource(R.drawable.button_on);
        }
        else{
            startButton.setImageResource(R.drawable.button_off);
        }
        dex=!dex;
    }

    private class LogRecognitionListener implements PhraseSpotterListener {

        /**
         * Обнаружении ключевой фразы в аудиопотоке.
         */
        @Override
        public void onPhraseSpotted(String s, int i) {
            Log.d(TAG, "onPhraseSpotted");
            Log.d(TAG, s);
            PhraseSpotter.stop();
            startVoiceRecognitionActivity();
        }

        /**
         * Вызывается в момент начала записи звука.
         */
        @Override
        public void onPhraseSpotterStarted() {
            Log.d(TAG, "onPhraseSpotterStarted");
        }

        /**
         * Вызывается в момент окончания записи звука.
         */
        @Override
        public void onPhraseSpotterStopped() {
            Log.d(TAG, "onPhraseSpotterStopped");
        }

        /**
         * Вызывается при возникновении ошибки во время работы PhraseSpotter.
         */
        @Override
        public void onPhraseSpotterError(Error error) {
            Log.d(TAG, "onPhraseSpotterError");
        }
    }

    public void onStart() {
        super.onStart();
    }

    /**
     * вывод результата
     * @param result
     */
    public void onRecognitionDone(String result) {
        Log.d(TAG, "onRecognitionDone " + result);

        resultView.setText(result);
    }

    /**
    *вывод ошибок
     */
    public void onError(Error error) {
        Log.e(TAG, "onError " + error.toString());

        resultView.setText("Error: " + error.toString());
    }

    /**
     * запуск вспомогательного экрана с процессом распознавания
     */
    private void startVoiceRecognitionActivity() {
        onoff();
        Intent intent = new Intent(this, RecognizerActivity.class);
        intent.putExtra(RecognizerActivity.EXTRA_LANGUAGE, "ru-RU");
        intent.putExtra(RecognizerActivity.EXTRA_MODEL, "general");
        startActivityForResult(intent, 0);
    }

    /**
     * обработка результатов распознавания
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (resultCode == RecognizerActivity.RESULT_OK) {
            onRecognitionDone(data.getStringExtra(RecognizerActivity.EXTRA_RESULT));
        }
        else if (resultCode == RecognizerActivity.RESULT_ERROR) {
            onError((Error) data.getSerializableExtra(RecognizerActivity.EXTRA_ERROR));
        }
        onoff();
        initialize();
    }

    @Override
    public void onPause() {
        super.onPause();
        //деинициализация
        PhraseSpotter.uninitialize();
    }

    @Override
    public void onStop() {
        super.onStop();
        //деинициализация
        PhraseSpotter.uninitialize();
    }

    public void onClickHistory(View view) {
        PhraseSpotter.uninitialize();
        Intent intent = new Intent(MainActivity.this, SecondActivity.class);
        startActivity(intent);
    }

    public void onClickSave(View view) {
        if(resultView.getText().length()>1){
        ContentValues newValues = new ContentValues();
        String cdata = new Date().toString();
        // Задаём значения для каждого столбца
        newValues.put(DatabaseHelper.NOTE_COLUMN, resultView.getText().toString());
        newValues.put(DatabaseHelper.DATE_COLUMN, cdata);

        // Вставляем данные в таблицу
        mSqLiteDatabase.insert("notes", null, newValues);
        }
    }

}