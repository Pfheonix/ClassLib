package com.dystahl.classlib;

import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.CursorJoiner;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class CheckedOutBooks extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try { this.getSupportActionBar().setTitle("All Checked Out Books");
        } catch (NullPointerException ex){
            Toast trouble = Toast.makeText(null, "All Checked Out Books", Toast.LENGTH_LONG);
            trouble.show();
        }

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_checked_out_books);

        searchExec(this.findViewById(android.R.id.content));
    }

    public void searchExec(View view){
        try {
            SQLiteDatabase libraryDB = openOrCreateDatabase("Library", MODE_PRIVATE, null);

            ArrayList<String> outSet = new ArrayList<>();
            StringBuilder outSetBuilder = new StringBuilder();
            ListView output = (ListView)findViewById(R.id.checkedOutListView);

            String[] columns = {"ISBN", "ID"};

            Cursor resultSet = libraryDB.query("CHECKOUT", columns, null, null, null, null,  "ID", null);

            resultSet.moveToFirst();

            do {
                String isbn = resultSet.getString(resultSet.getColumnIndexOrThrow("ISBN"));
                String[] studentData = {"FNAME", "LNAME"};
                String[] bookData = {"TITLE"};

                outSetBuilder.append(isbn);
                outSetBuilder.append(" - ");

                Cursor bookInfo = libraryDB.query("BOOK", bookData, "ISBN = '" + isbn + "'", null, null, null, null, null);
                bookInfo.moveToFirst();

                outSetBuilder.append(bookInfo.getString(bookInfo.getColumnIndexOrThrow("TITLE")));
                outSetBuilder.append(" checked out by ");

                String resId = resultSet.getString(resultSet.getColumnIndexOrThrow("ID"));
                outSetBuilder.append(resId);


                Cursor studentInfo = libraryDB.query("STUDENT", studentData, "ID = '" + resId + "'", null, null, null, null, null);
                if(studentInfo != null && studentInfo.getCount() > 0) {
                    studentInfo.moveToFirst();

                    outSetBuilder.append(" - ");
                    outSetBuilder.append(studentInfo.getString(studentInfo.getColumnIndexOrThrow("FNAME")));
                    outSetBuilder.append(" ");
                    outSetBuilder.append(studentInfo.getString(studentInfo.getColumnIndexOrThrow("LNAME")));
                    outSetBuilder.append(".");

                    studentInfo.close();
                }
                bookInfo.close();

                outSet.add(outSetBuilder.toString());
                outSetBuilder.delete(0, outSetBuilder.length());
            } while(resultSet.moveToNext());

            resultSet.close();

            libraryDB.close();
            ArrayAdapter<String> bookAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, outSet);
            output.setAdapter(bookAdapter);

        } catch (CursorIndexOutOfBoundsException cex){
            ListView output = (ListView)findViewById(R.id.checkedOutListView);
            ArrayList<String> outSet = new ArrayList<>();
            outSet.add("No Checked Out Books");
            Toast temp = Toast.makeText(this, cex.getMessage(), Toast.LENGTH_LONG);
            temp.show();
            ArrayAdapter<String> bookAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, outSet);
            output.setAdapter(bookAdapter);
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

        Toast well = Toast.makeText(this, "In checkIn", Toast.LENGTH_SHORT);
        well.show();

        try(SQLiteDatabase libraryDB = openOrCreateDatabase("Library", MODE_PRIVATE, null)){
            if((libraryDB.delete("CHECKOUT", "ISBN = '" + ISBN + "' AND ID = '" + ID + "'", null)) > 0){
                Toast temp = Toast.makeText(this, "Delete might have worked?", Toast.LENGTH_LONG);
                temp.show();
                this.searchExec(view);
            } else {
                Toast temp = Toast.makeText(this, "Delete didn't work.", Toast.LENGTH_LONG);
                temp.show();
            }
        } catch (Exception ex){
            Toast temp = Toast.makeText(this, "Something messed up. Here.\n" + ex.getMessage(), Toast.LENGTH_LONG);
            temp.show();
        }
    }

    public void contact (View view){
        SQLiteDatabase libraryDB = openOrCreateDatabase("Library", MODE_PRIVATE, null);

        ArrayList<String> isbnList = new ArrayList<>();
        String id = ((EditText)findViewById(R.id.studentIDCheckinText)).getText().toString();
        String[] studentColumns = {"CONTACT", "FNAME", "LNAME"};
        String[] checkoutColumns = {"ISBN", "ID"};
        String[] bookColumns = {"ISBN", "TITLE"};

        String[] isbn = {"ISBN"};

        Cursor resultSet = libraryDB.query("STUDENT", studentColumns, "ID = '" + id + "'", null, null, null, null, null);
        Cursor booksOutResultSet = libraryDB.query("CHECKOUT", checkoutColumns, "ID = '" + id + "'", null, null, null, null);
        Cursor bookToJoin = libraryDB.query("BOOK", bookColumns, null, null, null, null, "ISBN", null);
        Cursor checkoutToJoin = libraryDB.query("CHECKOUT", checkoutColumns, "ID = '" + id + "'", null, null, null, "ISBN", null);

        resultSet.moveToFirst();
        booksOutResultSet.moveToFirst();
        do{
            isbnList.add(booksOutResultSet.getString(booksOutResultSet.getColumnIndexOrThrow("ISBN")));
        } while (booksOutResultSet.moveToNext());

        String[] a = new String[isbnList.size()];
        ArrayList<String> nameList = new ArrayList<>();

        CursorJoiner bookCheckoutJoiner = new CursorJoiner(checkoutToJoin, isbn, bookToJoin, isbn);
        for(CursorJoiner.Result joinerResult : bookCheckoutJoiner){
            switch(joinerResult){
                case LEFT:
                    break;
                case RIGHT:
                    break;
                case BOTH:
                    nameList.add(bookToJoin.getString((bookToJoin.getColumnIndex("TITLE"))));
                    break;
            }
        }

        StringBuilder emailBody = new StringBuilder();


        ArrayList<String> contactAddress = new ArrayList<String>();
        contactAddress.add(resultSet.getString(resultSet.getColumnIndexOrThrow("CONTACT")));
        String[] contactAddresses = new String[contactAddress.size()];
        String name = resultSet.getString(resultSet.getColumnIndexOrThrow("FNAME")) + " " + resultSet.getString(resultSet.getColumnIndexOrThrow("LNAME"));

        resultSet.close();
        booksOutResultSet.close();
        bookToJoin.close();
        checkoutToJoin.close();

        Intent email = new Intent(Intent.ACTION_SENDTO);
        email.setData(Uri.parse("mailto:"));
        email.putExtra(Intent.EXTRA_EMAIL, contactAddress.toArray(contactAddresses));
        email.putExtra(Intent.EXTRA_SUBJECT, "You Have Books to Return!");

        emailBody.append("Hi there, ");
        emailBody.append(name);
        emailBody.append("!\n\nIt seems I'm missing some books from you! If you happen to find the following, please bring them back to my room!\n");

        for(String string : nameList){
            emailBody.append("\n\t");
            emailBody.append(string);
        }

        emailBody.append("\n\nThank you so much!");

        email.putExtra(Intent.EXTRA_TEXT, emailBody.toString());
        if(email.resolveActivity(getPackageManager()) != null){
            startActivity(email);
        }

    }
}
