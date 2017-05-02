package com.dystahl.classlib;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main_menu);

        this.deleteDatabase("Library");

        SQLiteDatabase libraryDB = openOrCreateDatabase("Library", MODE_PRIVATE, null);

        libraryDB.execSQL("CREATE TABLE IF NOT EXISTS BOOK(ISBN VARCHAR NOT NULL, TITLE VARCHAR NOT NULL," +
                " AUTHOR VARCHAR NOT NULL, BINDING VARCHAR, LENGTH INT, GENRE VARCHAR, COUNT INT, PRIMARY KEY(ISBN) )");

        libraryDB.execSQL("CREATE TABLE IF NOT EXISTS STUDENT(ID INT NOT NULL, FNAME VARCHAR NOT NULL," +
                " LNAME VARCHAR NOT NULL, CONTACT VARCHAR NOT NULL, PERIOD INTEGER, PRIMARY KEY(ID))");

        libraryDB.execSQL("CREATE TABLE IF NOT EXISTS CHECKOUT( ISBN VARCHAR NOT NULL, ID INT NOT NULL, PRIMARY KEY (ISBN, ID)," +
                "FOREIGN KEY(ISBN) REFERENCES BOOK, FOREIGN KEY(ID) REFERENCES STUDENT)");

    }

    public void changeToFindBooks(View view){
        Intent toFindBook = new Intent(this, FindBooks.class);
        startActivity(toFindBook);
    }

    public void changeToAddBook(View view){
        Intent toAddBook = new Intent(this, AddBook.class);
        startActivity(toAddBook);
    }

    public void changeToCheckOut(View view){
        Intent toCheckOut = new Intent(this, CheckOutBook.class);
        startActivity(toCheckOut);
    }

    public void changeToCheckedOut(View view){
        Intent toCheckedOut = new Intent(this, CheckedOutBooks.class);
        startActivity(toCheckedOut);
    }
}
