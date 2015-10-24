package com.wings.zilizili.utils;

import android.os.AsyncTask;


/**
 * Created by wing on 2015/10/24.
 */
public class NetUtils {

    private String result;

    public String getJsonData(String url) {
        new JsonAsyncTask().execute(url);
        return null;
    }

    class JsonAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            return null;
        }
    }
}
