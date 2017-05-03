package com.dystahl.classlib;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class FindBooks extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set custom title
        try { this.getSupportActionBar().setTitle("Search for Books");
        } catch (NullPointerException ex){
            Toast trouble = Toast.makeText(null, "Search for Books", Toast.LENGTH_LONG);
            trouble.show();
        }

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_find_books);


    }

    //Method searches for books in the database by portions of titles.
    public void searchExec(View view){
        try {
            SQLiteDatabase libraryDB = openOrCreateDatabase("Library", MODE_PRIVATE, null);

            ArrayList<String> outSet = new ArrayList<>();
            StringBuilder outSetBuilder = new StringBuilder();
            ListView output = (ListView)findViewById(R.id.bookList);
            String searchTerm = ((TextView)findViewById(R.id.searchText)).getText().toString();

            String[] columns = {"TITLE", "AUTHOR", "BINDING", "ISBN", "COUNT"};

            //Requesting all above columns where the Title matches part of the search term.
            Cursor resultSet = libraryDB.query("BOOK", columns, "TITLE LIKE '%" + searchTerm + "%'", null, null, null,  "TITLE", null);

            resultSet.moveToFirst();

            //Setting up Strings for the ListView
            do{
                outSetBuilder.append(resultSet.getString(resultSet.getColumnIndexOrThrow("TITLE")));
                outSetBuilder.append(" by ");
                outSetBuilder.append(resultSet.getString(resultSet.getColumnIndexOrThrow("AUTHOR")));
                outSetBuilder.append(" -- ");
                outSetBuilder.append(resultSet.getString(resultSet.getColumnIndexOrThrow("BINDING")));
                outSetBuilder.append(" -- ");
                outSetBuilder.append(resultSet.getString(resultSet.getColumnIndexOrThrow("ISBN")));
                outSetBuilder.append(" -- ");
                outSetBuilder.append(resultSet.getString(resultSet.getColumnIndexOrThrow("COUNT")));
                if(resultSet.getString(resultSet.getColumnIndexOrThrow("COUNT")).equals("1")){
                    outSetBuilder.append(" copy in.");
                } else {
                    outSetBuilder.append(" copies in.");
                }
                outSet.add(outSetBuilder.toString());
                outSetBuilder.delete(0, outSetBuilder.length());
            }while(resultSet.moveToNext());

            resultSet.close();

            libraryDB.close();
            ArrayAdapter<String> bookAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, outSet);
            output.setAdapter(bookAdapter);

        //Playing catch with all of the exceptions possible. Just in case.
        } catch (CursorIndexOutOfBoundsException cex){
            Toast temp = Toast.makeText(this,"Empty Table, Please Insert Books to Find Books\nAlternatively, Are You Using a Title/Title Fragment?", Toast.LENGTH_LONG);
            temp.show();
        } catch (IllegalArgumentException ex){
            Toast temp = Toast.makeText(this,"Invalid Column Name", Toast.LENGTH_LONG);
            temp.show();
        } catch (ArrayIndexOutOfBoundsException ex){
            Toast temp = Toast.makeText(this,"ArrayIndex Problems", Toast.LENGTH_LONG);
            temp.show();
        } catch (Exception ex){
            Toast temp = Toast.makeText(this,ex.toString(), Toast.LENGTH_LONG);
            temp.show();
        }
    }

    //Throw the ISBN in the ISBN EditText to the Checkout activity, then switch.
    public void checkout(View view){
        Intent toCheckOut = new Intent(this, CheckOutBook.class);
        toCheckOut.putExtra("ISBN",((EditText)findViewById(R.id.isbnPut)).getText().toString());
        startActivity(toCheckOut);
    }
}
