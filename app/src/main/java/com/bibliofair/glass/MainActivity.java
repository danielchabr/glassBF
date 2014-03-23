package com.bibliofair.glass;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.glass.app.Card;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class MainActivity extends Activity {
    static final int GET_BOOK_ISBN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e("MainActivity", "1");
        Intent scanCodeIntent = new Intent(this, ScanCodeActivity.class);
        startActivityForResult(scanCodeIntent, GET_BOOK_ISBN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("MainActivity", "2");

        /**
         * Get book ISBN.
         */
        if (requestCode == GET_BOOK_ISBN) {
            if (resultCode == Activity.RESULT_OK) {
                String text = data.getStringExtra(ScanCodeActivity.EXTRA_TEXT);
                Log.d("MainActivity", "3");
                if (isValidISBN(text)) {
                    Log.d("MainActivity", text);
                    try {
                        HttpClient httpclient = new DefaultHttpClient();
                        String URL = "http://www.bibliofair.com/api/v1/tel?id=5230ce11c1cf031f18000002&q=" + ISBNConvertor.ISBN1310(text) + "&token=c6c2e0b0152a92e32b41e2d1ac0a2160e53c9d313a5ebddc273e7b814528a412cb562b6f581b128cb80d54a0c2033b5b659523bbecb068a8b20753f97e16f659";
                        HttpGet get = new HttpGet(URL);
                        HttpResponse response = httpclient.execute(get);
                        StatusLine statusLine = response.getStatusLine();
                        if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                            ByteArrayOutputStream out = new ByteArrayOutputStream();
                            response.getEntity().writeTo(out);
                            out.close();
                            String responseString = out.toString();

                            Book book = Book.parseBookFromJSON(responseString);
                            book.setIsbn(text);
                            showBook(book);
                        } else {
                            //Closes the connection.
                            response.getEntity().getContent().close();
                            throw new IOException(statusLine.getReasonPhrase());
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    //showStatus(R.string.valid_code, text);
                } else {
                    showStatus(R.string.invalid_code, "");
                }
            } else {
                showStatus(R.string.scanning_failed, "");
            }
        }
    }

    private boolean isValidISBN(String text) {
        return true;
    }

    private void showBook (Book book) {
        Card statusCard = new Card(this);

        statusCard.setText("Title: " + book.getTitle() + '\n' + "Author: " + book.getAuthor() + '\n' + "ISBN: " + book.getIsbn());

        View cardView = statusCard.toView();
        setContentView(cardView);
    }

    private void showStatus (int statusCode, String text) {
        Log.e("MainActivity", text);
        String status = this.getString(statusCode);
        Card statusCard = new Card(this);
        if (text.equals("")) {
            statusCard.setText(status);
        } else {
            statusCard.setText(status + " " + text);
        }

        View cardView = statusCard.toView();
        setContentView(cardView);
    }

}
