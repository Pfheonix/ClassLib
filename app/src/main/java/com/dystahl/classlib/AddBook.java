package com.dystahl.classlib;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class AddBook extends AppCompatActivity {

    SQLiteDatabase libraryDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_add_book);

        libraryDB = openOrCreateDatabase("Library", MODE_PRIVATE, null);
    }

    public void addBook(View view){
        ContentValues queryValues = new ContentValues();
        StringBuilder notFound = new StringBuilder();

        queryValues.put("ISBN", ((EditText)findViewById(R.id.isbnText)).getText().toString());
        if(((String)queryValues.get("ISBN")).isEmpty()){
            notFound.append("ISBN");
        }
        queryValues.put("TITLE", ((EditText)findViewById(R.id.titleText)).getText().toString());
        if(((String)queryValues.get("TITLE")).isEmpty()){
            notFound.append(", Title");
        }
        queryValues.put("AUTHOR", ((EditText)findViewById(R.id.authorText)).getText().toString());
        if(((String)queryValues.get("AUTHOR")).isEmpty()){
            notFound.append(", Author");
        }
        queryValues.put("BINDING", ((EditText)findViewById(R.id.bindingText)).getText().toString());
        if(((String)queryValues.get("BINDING")).isEmpty()){
            notFound.append(", Binding");
        }
        queryValues.put("LENGTH", (Integer.parseInt(((EditText)findViewById(R.id.lengthText)).getText().toString())));
        if(((int)queryValues.get("LENGTH")) == 0){
            notFound.append(", Length");
        }
        queryValues.put("GENRE", ((EditText)findViewById(R.id.genreText)).getText().toString());
        if(((String)queryValues.get("GENRE")).isEmpty()){
            notFound.append(", Genre ");
        }
        queryValues.put("COUNT", (Integer.parseInt(((EditText)findViewById(R.id.countText)).getText().toString())));
        if(((int)queryValues.get("COUNT")) == 0){
            notFound.append(", Genre ");
        }
        if(notFound.length() > 0){
            notFound.append("not found.");
            Toast temp = Toast.makeText(this, notFound.toString(), Toast.LENGTH_LONG);
            temp.show();
        } else {
            try {
                String[] count = {"COUNT"};
                Cursor resultSet = libraryDB.query("BOOK", count, "ISBN = '"+ queryValues.get("ISBN") + "'", null, null, null, null, null);


                if(resultSet != null && resultSet.getCount() > 0){
                    resultSet.moveToFirst();
                    ContentValues countVal = new ContentValues();
                    countVal.put("COUNT", Integer.parseInt(resultSet.getString(resultSet.getColumnIndexOrThrow("COUNT"))) + 1);
                    libraryDB.update("BOOK", countVal, "ISBN = '" + queryValues.get("ISBN") + "'", null);
                    Toast temp = Toast.makeText(this, resultSet.getString(resultSet.getColumnIndexOrThrow("COUNT")), Toast.LENGTH_LONG);
                    temp.show();
                    resultSet.close();
                } else {
                    libraryDB.insertOrThrow("BOOK", null, queryValues);
                    Toast temp = Toast.makeText(this, "Insert completed", Toast.LENGTH_LONG);
                    temp.show();
                }
            } catch (Exception ex){
                Toast temp = Toast.makeText(this, "Query failed\n" + ex.getMessage(), Toast.LENGTH_LONG);
                temp.show();
            }
        }
    }
}

