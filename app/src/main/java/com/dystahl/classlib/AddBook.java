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
                //Cursor resultSet = libraryDB.rawQuery("SELECT * FROM BOOK WHERE ISBN = '" + queryValues.get("ISBN") + "';", null);
                String[] count = {"COUNT"};
                Cursor resultSet = libraryDB.query("BOOK", count, "ISBN = '"+ queryValues.get("ISBN") + "'", null, null, null, null, null);


                if(resultSet != null && resultSet.getCount() > 0){
                    resultSet.moveToFirst();
                    ContentValues countVal = new ContentValues();
                    countVal.put("COUNT", Integer.parseInt(resultSet.getString(resultSet.getColumnIndexOrThrow("COUNT"))) + 1);
                    //libraryDB.rawQuery("UPDATE BOOK SET COUNT = COUNT + 1 WHERE ISBN = " + queryValues.get("ISBN") + ";",null);
                    libraryDB.update("BOOK", countVal, "ISBN = '" + queryValues.get("ISBN") + "'", null);
                    Toast temp = Toast.makeText(this, resultSet.getString(resultSet.getColumnIndexOrThrow("COUNT")), Toast.LENGTH_LONG);
                    temp.show();
                    resultSet.close();
                } else {
                    //libraryDB.rawQuery("INSERT INTO BOOK VALUES ('" + queryValues.get(0) + "', '"
                    //       + queryValues.get(1) + "', '" + queryValues.get(2) + "', '" + queryValues.get(3) + "', "
                    //        + queryValues.get(4) + ", '" + queryValues.get(5) + "', " + queryValues.get(6) + ");", null);

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



    /*//Big brother clusterfuck of CSV parsing. It knows that lines are ended by \n, but it has to wait a bit
    //For that.
    public void parseWinnerCSV(){
        BufferedReader toRead = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.turingwinners)));
        StringBuilder remedy = new StringBuilder();

        try {
            int next = toRead.read();
            while (next != -1){
                if(remedy.length() > 0) {
                    remedy.delete(0, remedy.length());
                }
                while(next != ','){
                    remedy.append((char)next);
                    next = toRead.read();
                }

                Fname.add(remedy.toString());
                remedy.delete(0, remedy.length());
                next = toRead.read();

                while(next != ','){
                    remedy.append((char)next);
                    next = toRead.read();
                }

                Minitial.add(remedy.toString());
                remedy.delete(0, remedy.length());
                next = toRead.read();

                while(next != ','){
                    remedy.append((char)next);
                    next = toRead.read();
                }

                Lname.add(remedy.toString());
                remedy.delete(0, remedy.length());
                next = toRead.read();

                while(next != ','){
                    remedy.append((char)next);
                    next = toRead.read();
                }

                AlmaMater.add(remedy.toString());
                remedy.delete(0, remedy.length());
                next = toRead.read();

                while(next != ',') {
                    remedy.append((char)next);
                    next = toRead.read();

                }

                living.add(remedy.toString());
                remedy.delete(0, remedy.length());

                awardYear.add(Integer.parseInt(toRead.readLine()));
                next = toRead.read();
            }
        } catch (IOException iex){
            Toast.makeText(this, "IOException during winner toRead.read. Fucked. Up.", Toast.LENGTH_LONG);
        }
    }*/
}

