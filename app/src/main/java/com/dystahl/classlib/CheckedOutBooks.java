package com.dystahl.classlib;

import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CheckedOutBooks extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checked_out_books);
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

            String[] columns = {"TITLE", "AUTHOR", "BINDING", "ISBN", "COUNT"};

            Cursor resultSet = libraryDB.query("BOOK", columns, "TITLE LIKE '%" + searchTerm + "%'", null, null, null,  "TITLE", null);

            resultSet.moveToFirst();

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
                    outSetBuilder.append(" copy.");
                } else {
                    outSetBuilder.append(" copies.");
                }
                outSet.add(outSetBuilder.toString());
                outSetBuilder.delete(0, outSetBuilder.length());
            }while(resultSet.moveToNext());

            resultSet.close();

            libraryDB.close();
            ArrayAdapter<String> bookAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, outSet);
            output.setAdapter(bookAdapter);

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
}
