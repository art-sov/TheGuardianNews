package com.artsovalov.theguardiannews;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public final class NewsUtils {
    private final static String LOG_TAG = NewsUtils.class.getSimpleName();

    private NewsUtils() {
    }

    public static ArrayList<News> fetchNewsData(String requestNews){
        Log.i(LOG_TAG, " fetchNewsData method");

        URL url = createUrl(requestNews);
        String jsonResponse = null;

        try{
            jsonResponse = makeHttpRequest(url);
        }
        catch(IOException e){
            Log.e(LOG_TAG, "Error closing input stream ", e);
        }

        return extractNewsFromJson(jsonResponse);
    }

    private static String makeHttpRequest(URL url) throws IOException{
        String jsonResponse = "";

        if (url == null){
            return jsonResponse;
        }
        HttpURLConnection urlConnection;
        InputStream inputStream;

        try {
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if(urlConnection.getResponseCode() == 200){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }
            else{
                Log.i(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.i(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException{
        StringBuilder output = new StringBuilder();

        if(inputStream != null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while ((line != null)){
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static ArrayList<News> extractNewsFromJson(String jsonResponse) {
        ArrayList<News> listNews = new ArrayList<>();

        try{
            JSONObject root = new JSONObject(jsonResponse);
            JSONObject response = root.getJSONObject("response");
            JSONArray results = response.getJSONArray("results");

            for(int i = 0; i< results.length(); i++){
                JSONObject currentNews = results.getJSONObject(i);
                String webPublicationDate = currentNews.getString("webPublicationDate");
                String webUrl = currentNews.getString("webUrl");
                String webTitle = currentNews.getString("webTitle");

                // extract the JSONObject associated with the
                // key called "fields", which represents a list of all fields
                // for that news article.

                JSONObject fields = currentNews.getJSONObject("fields");
                String miniature = fields.getString("thumbnail");

                Log.i(LOG_TAG, "miniature " + miniature);

                News news = new News(webPublicationDate, webTitle, webUrl, miniature);
                listNews.add(news);
            }
        }
        catch(JSONException e){
            e.printStackTrace();
            Log.i(LOG_TAG, " Error retrieving information from jasonResponse");
        }
        return listNews;
    }

    private static URL createUrl(String sUrl) {
        URL url = null;

        try {
            url = new URL(sUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.i(LOG_TAG, "Error creating URL");
        }
        return url;
    }
}
