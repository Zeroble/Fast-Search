package com.zeroble.superdeveloper.fastsearch;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class SearchActivity extends AppCompatActivity implements AbsListView.OnScrollListener {
    String searchWord, exceptWord, essentialWord;
    String fullWord;
    ListView listview;
    static GoogleListviewAdapter googleListviewAdapter;
    private static boolean mLockListView = false;          // 데이터 불러올때 중복안되게 하기위한 변수
    private static ProgressBar progressBar;
    private boolean lastItemVisibleFlag = false;    // 리스트 스크롤이 마지막 셀(맨 바닥)로 이동했는지 체크할 변수

    TextView btn_google, btn_yt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        googleListviewAdapter = new GoogleListviewAdapter();

        btn_google = findViewById(R.id.btn_google);
        btn_yt = findViewById(R.id.btn_yt);

        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        listview = (ListView) findViewById(R.id.search_result);
        listview.setAdapter(googleListviewAdapter);

        searchWord = getIntent().getStringExtra("searchWord");
        exceptWord = getIntent().getStringExtra("exceptWord");
        essentialWord = getIntent().getStringExtra("essentialWord");

        exceptWord = exceptWord.equals("") ? "" : " -" + exceptWord;
        essentialWord = essentialWord.equals("") ? "" : " +" + essentialWord;
        fullWord = searchWord + exceptWord + essentialWord;

        listview.setOnScrollListener(this);
        new GoogleSearchManager(fullWord).execute();


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                GoogleListItem item = (GoogleListItem) parent.getItemAtPosition(position);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(item.getUrl())));
            }
        });
    }


    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastItemVisibleFlag && mLockListView == false) {
            Log.d("asdf", "bottom!!0");
            mLockListView = true;
            progressBar.setVisibility(View.VISIBLE);
            new GoogleSearchManager(fullWord).execute();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        lastItemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
    }


    static class GoogleSearchManager extends AsyncTask {
        String stringQuery;
        static int page = 0;

        public GoogleSearchManager(String stringQuery) {
            this.stringQuery = stringQuery;
            Log.d("asdf", "https://www.google.com/search?q=" + stringQuery);
        }

        @Override
        protected void onPostExecute(Object o) {
            googleListviewAdapter.notifyDataSetChanged();
            mLockListView = false;
            progressBar.setVisibility(View.GONE);
            super.onPostExecute(o);
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            Connection.Response response = null;
            try {
                response = Jsoup.connect("https://www.google.com/search?q=" + stringQuery + "&start=" + page + "0")
                        .method(Connection.Method.GET)
                        .execute();
                page++;
                Log.d("asdf", "" + page);
                Document document = response.parse();
                Elements elements = document.select(".rc");
                for (Element element : elements) {
                    googleListviewAdapter.addItem(element.select("div.r > a > h3").text(), element.select("div.r > a").attr("href"), element.select("div.s > div > span").text());
                }
            } catch (IOException e) {
                Log.d("asdf", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }
    }
    static class YoutubeSearchManager extends AsyncTask {
        String stringQuery;
        static int page = 1;

        public YoutubeSearchManager(String stringQuery) {
            this.stringQuery = stringQuery;
        }

        @Override
        protected void onPostExecute(Object o) {
            googleListviewAdapter.notifyDataSetChanged();
            mLockListView = false;
            progressBar.setVisibility(View.GONE);
            super.onPostExecute(o);
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            Connection.Response response = null;
            try {
                response = Jsoup.connect("https://www.youtube.com/results?search_query="+ stringQuery + "&page=" + page )
                        .method(Connection.Method.GET)
                        .execute();
                page++;
                Log.d("asdf", "" + page);
                Document document = response.parse();
                Elements elements = document.select(".rc");
                for (Element element : elements) {
                    googleListviewAdapter.addItem(element.select("div.r > a > h3").text(), element.select("div.r > a").attr("href"), element.select("div.s > div > span").text());
                }
            } catch (IOException e) {
                Log.d("asdf", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }
    }

}
