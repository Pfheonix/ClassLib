package com.dystahl.classlib;

import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
            //String query = "SELECT * FROM BOOK WHERE TITLE LIKE '%" + searchTerm + "%'" +
            //        "ORDER BY TITLE";

            String[] columns = {"ISBN", "ID"};

            Cursor resultSet = libraryDB.query("CHECKOUT", columns, null, null, null, null,  "ID", null);

            resultSet.moveToFirst();

            do{
                outSetBuilder.append(resultSet.getString(resultSet.getColumnIndexOrThrow("ISBN")));
                outSetBuilder.append(" checked out by ");
                outSetBuilder.append(resultSet.getString(resultSet.getColumnIndexOrThrow("ID")));
                outSet.add(outSetBuilder.toString());
                outSetBuilder.delete(0, outSetBuilder.length());
            } while(resultSet.moveToNext());

            resultSet.close();

            libraryDB.close();
            ArrayAdapter<String> bookAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, outSet);
            output.setAdapter(bookAdapter);

        } catch (CursorIndexOutOfBoundsException cex){
            Toast temp = Toast.makeText(this,"Empty Table, Please Insert Books to Find Books\nAlternatively, Are You Using a Title/Title Fragment?", Toast.LENGTH_LONG);
            temp.show();
        } catch (IllegalArgumentException illex){
            Toast temp = Toast.makeText(this,"Invalid Column Name", Toast.LENGTH_LONG);
            temp.show();
        } catch (ArrayIndexOutOfBoundsException arrex){
            Toast temp = Toast.makeText(this,"ArrayIndex Problems", Toast.LENGTH_LONG);
            temp.show();
        } catch (Exception ex){
            Toast temp = Toast.makeText(this,ex.toString(), Toast.LENGTH_LONG);
            temp.show();
        }
    }

    public void checkIn (View view){
        String ISBN, ID;
        ISBN = ((EditText)findViewById(R.id.isbnCheckinText)).getText().toString();
        ID = ((EditText)findViewById(R.id.studentIDCheckinText)).getText().toString();

        try(SQLiteDatabase libraryDB = openOrCreateDatabase("Library", MODE_PRIVATE, null)){
            libraryDB.delete("CHECKOUT", "ISBN = '" + ISBN + "' AND ID = '" + ID + "'", null);
        } catch (Exception ex){
            Toast temp = Toast.makeText(this, "Something messed up. Here.\n" + ex.getMessage(), Toast.LENGTH_LONG);
            temp.show();
        }
    }
}
