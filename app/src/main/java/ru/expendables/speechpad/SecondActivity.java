package ru.expendables.speechpad;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


public class SecondActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        //DatabaseHelper mDatabaseHelper;
        //mDatabaseHelper = new DatabaseHelper(this, "paddatabase.db", null, 1);
       // SQLiteDatabase mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();;
        //Cursor cursor = mSqLiteDatabase.query("notes", new String[]{DatabaseHelper.NOTE_COLUMN,
       //                 DatabaseHelper.DATE_COLUMN},
      //          null, null,
      //          null, null, null) ;

       // cursor.moveToFirst();

       // String note = cursor.getString(cursor.getColumnIndex(DatabaseHelper.NOTE_COLUMN));
       // String date = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DATE_COLUMN));


        //TextView infoTextView = (TextView)findViewById(R.id.textView);
        //infoTextView.setText(note + "\n" + date);

        // не забываем закрывать курсор
        //cursor.close();
       // TextView infoTextView = (TextView)findViewById(R.id.textView);
        // јбстрактный пример
        LinearLayout layoutt = (LinearLayout) findViewById(R.id.layout);
        DatabaseHelper mDatabaseHelper;
        mDatabaseHelper = new DatabaseHelper(this, "paddatabase.db", null, 1);
        SQLiteDatabase mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();;
        Cursor cursor = mSqLiteDatabase.query("notes", new String[]{
                        DatabaseHelper.NOTE_COLUMN, DatabaseHelper.DATE_COLUMN}, null,
                null,
                null,
                null,
                null
        );
        while (cursor.moveToNext()) {
            String note= cursor.getString(cursor.getColumnIndex(DatabaseHelper.NOTE_COLUMN));
            String date = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DATE_COLUMN));

            TextView txt = new TextView(this);
            txt.setText(note + "\n" + date + "\n");
            txt.setTextColor(Color.rgb(180,118,74));
            txt.setTextSize(16);
            txt.setPadding(25, 0, 15, 0);
            layoutt.addView(txt);
        }
        cursor.close();
    }


    public void onClickMicro(View view) {

        Intent intent = new Intent(SecondActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
