package com.zeroble.superdeveloper.fastsearch;

import android.widget.AdapterView;

import static com.zeroble.superdeveloper.fastsearch.SearchActivity.se;

class SelectedEngin {
    int selected_engin;
    final int Naver = 1;
    final int Google = 2;
    final int Daum = 3;
    final int Bing = 4;
    final int Youtube = 5;

    static GoogleListviewAdapter googleListviewAdapter = new GoogleListviewAdapter();
    static YoutubeListviewAdapter youtubeListviewAdapter = new YoutubeListviewAdapter();

    public String getSelectedItemUrl(AdapterView parent, int position) {
        if(Google == selected_engin){
            GoogleListItem item = (GoogleListItem) parent.getItemAtPosition(position);
            return item.getUrl();
        }
        if(Youtube == selected_engin){
            YoutubeListItem item = (YoutubeListItem) parent.getItemAtPosition(position);
            return item.getUrl();
        }

        return "ERROR";
    }

    public void EnginReload() {
        if(Google == selected_engin){
            new SearchActivity.GoogleSearchManager().execute();
        }
        if(Youtube == selected_engin){
            new SearchActivity.YoutubeSearchManager().execute();


        }
    }
}