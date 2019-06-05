package com.zeroble.superdeveloper.fastsearch;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;

public class SearchActivity extends AppCompatActivity implements AbsListView.OnScrollListener {
    String searchWord, exceptWord, essentialWord;
    static ClipboardManager clipboardManager;
    String fullWord;
    ListView listview;
    private static boolean mLockListView = false;          // 데이터 불러올때 중복안되게 하기위한 변수
    private static ProgressBar progressBar;
    private boolean lastItemVisibleFlag = false;    // 리스트 스크롤이 마지막 셀(맨 바닥)로 이동했는지 체크할 변수
    static SelectedEngin se;
    TextView btn_google, btn_yt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        searchWord = getIntent().getStringExtra("searchWord");
        exceptWord = getIntent().getStringExtra("exceptWord");
        essentialWord = getIntent().getStringExtra("essentialWord");

        exceptWord = exceptWord.equals("") ? "" : " -" + exceptWord;
        essentialWord = essentialWord.equals("") ? "" : " +" + essentialWord;
        fullWord = searchWord + exceptWord + essentialWord;
        se = new SelectedEngin();

        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        listview = (ListView) findViewById(R.id.search_result);
        listview.setAdapter(se.googleListviewAdapter);
        setBtnInit();



        listview.setOnScrollListener(this);


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(se.getSelectedItemUrl(parent,position))));
            }
        });
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_google:
                se.selected_engin = se.Google;
                break;

            case R.id.btn_yt:
                se.selected_engin = se.Youtube;
                break;
        }
        changeEngin();
    }
    private void setBtnInit() {
        btn_yt = findViewById(R.id.btn_yt);
        btn_google = findViewById(R.id.btn_google);

        if (getIntent().getBooleanExtra("cb_yt", false)) {
            btn_yt.setVisibility(View.VISIBLE);
            se.youtubeListviewAdapter = new YoutubeListviewAdapter();
            se.selected_engin = se.Youtube;
        }

        if (getIntent().getBooleanExtra("cb_google", false)) {
            btn_google.setVisibility(View.VISIBLE);
            se.googleListviewAdapter = new GoogleListviewAdapter();
            se.selected_engin = se.Google;
        }
        changeEngin();

    }
    private void changeEngin(){
        if(se.Google == se.selected_engin){
            btn_google.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.choosen_search_engin_item));
            btn_yt.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.choose_search_engin_item));
            listview.setAdapter(se.googleListviewAdapter);
            if(se.googleListviewAdapter.getCount() == 0)
                new GoogleSearchManager(fullWord).execute();
        }

        if(se.Youtube == se.selected_engin){
            btn_google.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.choose_search_engin_item));
            btn_yt.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.choosen_search_engin_item));
            listview.setAdapter(se.youtubeListviewAdapter);
            if(se.youtubeListviewAdapter.getCount() == 0)
                new YoutubeSearchManager(searchWord).execute();
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastItemVisibleFlag && mLockListView == false) {
            Log.d("asdf", "bottom!!0");
            mLockListView = true;
            progressBar.setVisibility(View.VISIBLE);
            se.EnginReload();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        lastItemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
    }


    static class GoogleSearchManager extends AsyncTask {
        static String stringQuery;
        static int page = 0;

        public GoogleSearchManager(String stringQuery) {
            progressBar.setVisibility(View.VISIBLE);
            this.stringQuery = stringQuery;
            this.page = 0;
        }
        public GoogleSearchManager(){
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Object o) {
            se.googleListviewAdapter.notifyDataSetChanged();
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
                    se.googleListviewAdapter.addItem(element.select("div.r > a > h3").text(), element.select("div.r > a").attr("href"), element.select("div.s > div > span").text());
                }
            } catch (IOException e) {
                Log.d("asdf", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }
    }

    static class YoutubeSearchManager extends AsyncTask {
        static String stringQuery;
        static int page = 1;

        public YoutubeSearchManager(String stringQuery) {
            progressBar.setVisibility(View.VISIBLE);
            this.stringQuery = stringQuery;
            page = 1;
        }
        public YoutubeSearchManager(){
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Object o) {
            se.youtubeListviewAdapter.notifyDataSetChanged();
            mLockListView = false;
            progressBar.setVisibility(View.GONE);
            super.onPostExecute(o);
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            Connection.Response response = null;
            try {
                Log.d("asdf","YT");

                response = Jsoup.connect("https://www.youtube.com/results?search_query=" + stringQuery + "&page=" + page)
                        .method(Connection.Method.GET)
                        .execute();
                page++;
                Document document = response.parse();
                Elements elements = document.select(".yt-lockup-content");


                ClipData clipData = ClipData.newPlainText("label", document.toString());
                clipboardManager.setPrimaryClip(clipData);

                for (Element element : elements) {
                    try {
                        String Title = element.selectFirst("h3 a span").attr("aria-label");
                        String Info = Title.substring(Title.indexOf("게시자: "));
                        Title = Title.substring(0, Title.indexOf("게시자: "));
                        String ID = element.selectFirst("h3 a").attr("href").replace("/watch?v=","");
                    se.youtubeListviewAdapter.addItem(Title, Info, "https://www.youtube.com/watch?v="+ID, "https://i.ytimg.com/vi/"+ID+"/hqdefault.jpg");
                    }catch (Exception e){
                        Log.d("asdf","PROBLEM "+e.getMessage());
                    }
                }
            } catch (IOException e) {
                Log.d("asdf", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }
    }

}
