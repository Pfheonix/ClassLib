package com.dystahl.classlib;

import android.content.Context;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class FindBooks extends AppCompatActivity {

    SQLiteDatabase libraryDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_find_books);


        ListView result =  (ListView)findViewById(R.id.bookList);
        TextView input = (TextView)findViewById(R.id.searchText);


    }

    public void searchExec(View view){
        try {
            SQLiteDatabase libraryDB = openOrCreateDatabase("Library", MODE_PRIVATE, null);

            ArrayList<String> outSet = new ArrayList<>();
            StringBuilder outSetBuilder = new StringBuilder();
            ListView output = (ListView)findViewById(R.id.bookList);
            String searchTerm = ((TextView)findViewById(R.id.searchText)).getText().toString();
            String query = "SELECT * FROM BOOK WHERE TITLE LIKE '%" + searchTerm + "%'" +
                    "ORDER BY TITLE";

            Cursor resultSet = libraryDB.rawQuery(query, null);

            resultSet.moveToFirst();

            do{
                outSetBuilder.append(resultSet.getString(resultSet.getColumnIndexOrThrow("TITLE")));
                outSetBuilder.append(" by ");
                outSetBuilder.append(resultSet.getString(resultSet.getColumnIndexOrThrow("AUTHOR")));
                outSetBuilder.append(" -- ");
                outSetBuilder.append(resultSet.getString(resultSet.getColumnIndexOrThrow("BINDING")));
                outSetBuilder.append(" -- ");
                outSetBuilder.append(resultSet.getString(resultSet.getColumnIndexOrThrow("ISBN")));
                outSet.add(outSetBuilder.toString());
                outSetBuilder.delete(0, outSetBuilder.length());
            }while(resultSet.moveToNext());

            libraryDB.close();
            ArrayAdapter<String> bookAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, outSet);
            output.setAdapter(bookAdapter);

        } catch (CursorIndexOutOfBoundsException cex){
            Toast temp = Toast.makeText(this,"Empty Database, Please Insert Books to Find Books", Toast.LENGTH_LONG);
            temp.show();
        } catch (IllegalArgumentException ex){
            Toast temp = Toast.makeText(this,"Invalid Column Name", Toast.LENGTH_LONG);
            temp.show();
        } catch (ArrayIndexOutOfBoundsException ex){
            Toast temp = Toast.makeText(this,"ArrayIndex Fuck", Toast.LENGTH_LONG);
            temp.show();
        } catch (Exception ex){
            Toast temp = Toast.makeText(this,ex.toString(), Toast.LENGTH_LONG);
            temp.show();
        }
    }

    public void checkout(){

    }
}

class BookCursorAdapter extends CursorAdapter {
    public LayoutInflater cursorInflater;

    public BookCursorAdapter(Context context, Cursor c, int flags){
        super(context, c, flags);
        cursorInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.book_list_item, parent, false);
    }

    public void bindView(View view, Context context, Cursor cursor) {
        TextView title = (TextView) view.findViewById(R.id.book_title);
        TextView author = (TextView) view.findViewById(R.id.book_author);

        title.setText(cursor.getString(cursor.getColumnIndexOrThrow("title")));
        author.setText(cursor.getString(cursor.getColumnIndexOrThrow("author")));
    }
}
