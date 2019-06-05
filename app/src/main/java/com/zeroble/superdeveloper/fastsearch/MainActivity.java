package com.zeroble.superdeveloper.fastsearch;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {
    EditText searchWord, essentialWord, exceptWord;
    CheckBox cb_google, cb_yt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cb_google = findViewById(R.id.cb_google);
        cb_yt = findViewById(R.id.cb_yt);

        searchWord = findViewById(R.id.search_word);
        essentialWord = findViewById(R.id.essential_word);
        exceptWord = findViewById(R.id.except_word);
        ImageView imageView = findViewById(R.id.img);
    }

    public void Search(View v){
        Intent intent =  new Intent(MainActivity.this, SearchActivity.class);
        Log.d("asdf",searchWord.getText().toString());
        intent.putExtra("searchWord",searchWord.getText().toString());
        intent.putExtra("essentialWord",essentialWord.getText().toString());
        intent.putExtra("exceptWord",exceptWord.getText().toString());
        intent.putExtra("cb_yt",cb_yt.isChecked());
        intent.putExtra("cb_google",cb_google.isChecked());
        startActivity(intent);
    }
}
