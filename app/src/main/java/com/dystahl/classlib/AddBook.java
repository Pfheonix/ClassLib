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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            this.getSupportActionBar().setTitle("Add Books");
        } catch (NullPointerException ex){
            Toast trouble = Toast.makeText(null, "Add Books", Toast.LENGTH_LONG);
            trouble.show();
        }

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_add_book);
    }

    //Method for adding books to the database.
    public void addBook(View view){

        SQLiteDatabase libraryDB = openOrCreateDatabase("Library", MODE_PRIVATE, null);

        ContentValues queryValues = new ContentValues();
        StringBuilder notFound = new StringBuilder();

        //Getting all values for the book, in order to put it into the database.
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
        //If not all fields are present, kick it out.
        if(notFound.length() > 0){
            notFound.append("not found.");
            Toast temp = Toast.makeText(this, notFound.toString(), Toast.LENGTH_LONG);
            temp.show();
        } else {
            try {
                String[] count = {"COUNT"};
                Cursor resultSet = libraryDB.query("BOOK", count, "ISBN = '"+ queryValues.get("ISBN") + "'", null, null, null, null, null);


                //If you have an ISBN present that matches, add one to the count.
                if(resultSet != null && resultSet.getCount() > 0){
                    resultSet.moveToFirst();
                    ContentValues countVal = new ContentValues();
                    countVal.put("COUNT", Integer.parseInt(resultSet.getString(resultSet.getColumnIndexOrThrow("COUNT"))) + 1);
                    libraryDB.update("BOOK", countVal, "ISBN = '" + queryValues.get("ISBN") + "'", null);
                    Toast temp = Toast.makeText(this, resultSet.getString(resultSet.getColumnIndexOrThrow("COUNT")), Toast.LENGTH_LONG);
                    temp.show();
                    resultSet.close();
                    libraryDB.close();
                } else {
                    //Insert the book to the database.
                    libraryDB.insertOrThrow("BOOK", null, queryValues);
                    Toast temp = Toast.makeText(this, "Insert completed", Toast.LENGTH_LONG);
                    temp.show();
                    libraryDB.close();
                }
            } catch (Exception ex){
                Toast temp = Toast.makeText(this, "Query failed\n" + ex.getMessage(), Toast.LENGTH_LONG);
                temp.show();
                libraryDB.close();
            }
        }
    }
}

