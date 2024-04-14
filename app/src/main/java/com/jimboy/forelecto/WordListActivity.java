package com.jimboy.forelecto;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class WordListActivity extends AppCompatActivity {

    private ListView listView;
    private WordDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list);

        Button clearAllButton = findViewById(R.id.clearAllButton);
        listView = findViewById(R.id.listView);
        databaseHelper = new WordDatabaseHelper(this);

        // Retrieve words from the database
        ArrayList<String> wordList = retrieveWords();

        // Create an adapter and set it to the ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, wordList);
        listView.setAdapter(adapter);

        clearAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showClearAllConfirmationDialog();
            }
        });
    }

    private void showClearAllConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to clear all words?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Clear all words from the database
                        databaseHelper.clearAllWords();

                        // Refresh the ListView
                        ArrayAdapter<String> adapter = (ArrayAdapter<String>) listView.getAdapter();
                        adapter.clear();
                        adapter.addAll(retrieveWords());
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and show it
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private ArrayList<String> retrieveWords() {
        ArrayList<String> wordList = new ArrayList<>();
        // Retrieve words from the database
        Cursor cursor = databaseHelper.getReadableDatabase().rawQuery("SELECT * FROM words", null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String word = cursor.getString(cursor.getColumnIndex("word"));
                wordList.add(word);
            }
            cursor.close();
        }
        return wordList;
    }
}
