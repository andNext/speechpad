package ru.expendables.speechpad;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

import ru.expendables.speechpad.utils.DatabaseHelper;


public class SecondActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        ListView ResultListView= (ListView)findViewById(R.id.listResult);
        final ArrayList<String> notes = new ArrayList<>();

        final ArrayAdapter<String> adapter;
        adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, notes);

        /*
            output DB in an ordered list
         */
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
        //output each record
        while (cursor.moveToNext()) {
            notes.add(0, cursor.getString(cursor.getColumnIndex(DatabaseHelper.NOTE_COLUMN))+
                    "\n"+cursor.getString(cursor.getColumnIndex(DatabaseHelper.DATE_COLUMN)));
        }
        cursor.close();
        ResultListView.setAdapter(adapter);
    }

    /*
        Launch a new activity(MainActivity)
     */
    public void onClickMicro(View view) {

        Intent intent = new Intent(SecondActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
