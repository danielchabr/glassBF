package com.bibliofair.glass;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.glass.app.Card;


public class MainActivity extends Activity {
    static final int GET_BOOK_ISBN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        Log.e("MainActivity", "1");
        Intent scanCodeIntent = new Intent(this, ScanCodeActivity.class);
        startActivityForResult(scanCodeIntent, GET_BOOK_ISBN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("MainActivity", "2");
        if (requestCode == GET_BOOK_ISBN) {
            if (resultCode == Activity.RESULT_OK) {
                String text = data.getStringExtra(ScanCodeActivity.EXTRA_TEXT);
                Log.e("MainActivity", "3");
                if (isValidISBN(text)) {
                    showStatus(R.string.valid_code, text);
                } else {
                    showStatus(R.string.invalid_code, "");
                }
            } else {
                showStatus(R.string.scanning_failed, "");
            }
        }
    }

    private boolean isValidISBN (String text) {
        return true;
    }

    private void showStatus (int statusCode, String text) {
        Log.e("MainActivity", "4");
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
