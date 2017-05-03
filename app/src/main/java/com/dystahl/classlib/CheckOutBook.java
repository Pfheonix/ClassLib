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
import android.widget.Toast;

import java.util.ArrayList;

public class CheckOutBook extends AppCompatActivity {

    SQLiteDatabase libraryDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_check_out_book);
    }

    public void checkOut(View view) {
        ArrayList<String> queryValues = new ArrayList<>();
        StringBuilder notFound = new StringBuilder();
        ContentValues studentData = new ContentValues();

        queryValues.add(((EditText) findViewById(R.id.isbnCheckoutText)).getText().toString());
        if (queryValues.get(0).isEmpty()) {
            notFound.append("ISBN");
        }
        queryValues.add(((EditText) findViewById(R.id.studentIDText)).getText().toString());
        if (queryValues.get(1).isEmpty()) {
            notFound.append(", Student ID");
        } else {
            studentData.put("ID", queryValues.get(1));
        }
        queryValues.add(((EditText) findViewById(R.id.fnameText)).getText().toString());
        if (queryValues.get(2).isEmpty()) {
            notFound.append(", Author");
        } else {
            studentData.put("ID", queryValues.get(2));
        }
        queryValues.add(((EditText) findViewById(R.id.lnameText)).getText().toString());
        if (queryValues.get(3).isEmpty()) {
            notFound.append(", Binding");
        } else {
            studentData.put("ID", queryValues.get(3));
        }
        queryValues.add(((EditText) findViewById(R.id.contactText)).getText().toString());
        if (queryValues.get(4).isEmpty()) {
            notFound.append(", Length");
        } else {
            studentData.put("ID", queryValues.get(4));
        }
        queryValues.add(((EditText) findViewById(R.id.periodText)).getText().toString());
        if (queryValues.get(5).isEmpty()) {
            notFound.append(", Genre ");
        }  else {
            studentData.put("ID", queryValues.get(5));
        }

        if (notFound.length() > 0) {
            notFound.append("not found.");
            Toast temp = Toast.makeText(this, notFound.toString(), Toast.LENGTH_LONG);
            temp.show();
        } else {
            try {
                libraryDB = openOrCreateDatabase("Library", MODE_PRIVATE, null);
                String[] isbn = {"ISBN"};
                Cursor resultSet = libraryDB.query(false, "BOOK", isbn, queryValues.get(0),  null, null, null, null, null);

                String[] id = {"ID"};
                Cursor resultSet2 = libraryDB.query(false, "STUDENT", id, queryValues.get(1), null, null, null, null, null);


                ContentValues checkOutdata = new ContentValues();
                checkOutdata.put("ISBN", queryValues.get(0)); checkOutdata.put("ID", queryValues.get(1));
                if (resultSet != null) {
                    if (resultSet2 == null) {
                        libraryDB.insert("STUDENT", null, studentData);
                    }
                    libraryDB.insert("CHECKOUT", null, checkOutdata);
                    Toast temp = Toast.makeText(this, "Book " + queryValues.get(0) + " checked out by student " + queryValues.get(1), Toast.LENGTH_LONG);
                    temp.show();
                    resultSet.close();
                    resultSet2.close();
                } else {
                    Toast temp = Toast.makeText(this, "Sorry, that book does not exist in the database.", Toast.LENGTH_LONG);
                    temp.show();
//                    if (resultSet2 == null) {
//
//                    }
                }
            } catch (Exception ex) {
                Toast temp = Toast.makeText(this, "Query failed\n" + ex.getMessage(), Toast.LENGTH_LONG);
                temp.show();
            }
        }
    }
}
