package com.dystahl.classlib;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class AddBook extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_add_book);

        SQLiteDatabase libraryDB = openOrCreateDatabase("Library", MODE_PRIVATE, null);
    }

    public void addBook(View view){

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

