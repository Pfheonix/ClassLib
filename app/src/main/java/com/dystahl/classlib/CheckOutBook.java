package com.dystahl.classlib;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
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

        Intent input = this.getIntent();
        String ISBN;
        if(input.getExtras() != null){
            ISBN = input.getExtras().getString("ISBN");
            if(ISBN != null){
                ((EditText)findViewById(R.id.isbnCheckoutText)).setText(ISBN);
            }
        }
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
            studentData.put("ID", Integer.parseInt(queryValues.get(1)));
        }
        queryValues.add(((EditText) findViewById(R.id.fnameText)).getText().toString());
        if (queryValues.get(2).isEmpty()) {
            notFound.append(", First Name");
        } else {
            studentData.put("FNAME", queryValues.get(2));
        }
        queryValues.add(((EditText) findViewById(R.id.lnameText)).getText().toString());
        if (queryValues.get(3).isEmpty()) {
            notFound.append(", Last Name");
        } else {
            studentData.put("LNAME", queryValues.get(3));
        }
        queryValues.add(((EditText) findViewById(R.id.contactText)).getText().toString());
        if (queryValues.get(4).isEmpty()) {
            notFound.append(", Contact");
        } else {
            studentData.put("CONTACT", queryValues.get(4));
        }
        queryValues.add(((EditText) findViewById(R.id.periodText)).getText().toString());
        if (queryValues.get(5).isEmpty()) {
            notFound.append(", Period ");
        }  else {
            studentData.put("PERIOD", Integer.parseInt(queryValues.get(5)));
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

                ContentValues checkOutData = new ContentValues();

                checkOutData.put("ISBN", queryValues.get(0));
                checkOutData.put("ID", queryValues.get(1));

                if (resultSet != null) {
                    if (resultSet2 != null && resultSet2.getCount() == 0) {
                        if(libraryDB.insertOrThrow("STUDENT", null, studentData) == -1){
                            Toast temp = Toast.makeText(this, "Insert didn't throw, but failed.", Toast.LENGTH_LONG);
                            temp.show();
                        } else {
                            Toast temp = Toast.makeText(this, "Insert seems to have succeeded.", Toast.LENGTH_LONG);
                            temp.show();
                        }
                    }
                    libraryDB.insertOrThrow("CHECKOUT", null, checkOutData);
                    Toast temp = Toast.makeText(this, "Book " + queryValues.get(0) + " checked out by student " + queryValues.get(1), Toast.LENGTH_LONG);
                    temp.show();
                    resultSet.close();
                    if(resultSet2 != null){
                        resultSet2.close();
                    }
                } else {
                    Toast temp = Toast.makeText(this, "Sorry, that book does not exist in the database.", Toast.LENGTH_LONG);
                    temp.show();
                }
            } catch (SQLiteConstraintException sqlex) {
                Toast temp = Toast.makeText(this, "Student already has book checked out!", Toast.LENGTH_LONG);
                temp.show();
            } catch (Exception ex) {
                Toast temp = Toast.makeText(this, "Query failed\n" + ex.getMessage(), Toast.LENGTH_LONG);
                temp.show();
            }
        }
    }
}
