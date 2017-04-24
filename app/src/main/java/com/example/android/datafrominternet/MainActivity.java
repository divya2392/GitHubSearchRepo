/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.datafrominternet;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.datafrominternet.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText mSearchBoxEditText;

    private TextView mUrlDisplayTextView;

    private TextView mSearchResultTextView;

    private TextView mErrorMessageDisplay;

    private ProgressBar mLoadingIndicator;

    private static final String SEARCH_QUERY_URL_EXTRA = "query";

    private static final String SEARCH_RESULTS_RAW_JSON = "results";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchBoxEditText = (EditText)findViewById(R.id.et_search_box);
        mUrlDisplayTextView = (TextView)findViewById(R.id.tv_url_display);
        mSearchResultTextView = (TextView)findViewById(R.id.tv_github_search_results_json);
        mErrorMessageDisplay = (TextView)findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        if(savedInstanceState != null)
        {
            String queryUrl = savedInstanceState.getString(SEARCH_QUERY_URL_EXTRA);
            String rawJsonSearchResults = savedInstanceState.getString(SEARCH_RESULTS_RAW_JSON);

            mUrlDisplayTextView.setText(queryUrl);
            mSearchResultTextView.setText(rawJsonSearchResults);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String queryUrl = mUrlDisplayTextView.getText().toString();
        outState.putString(SEARCH_QUERY_URL_EXTRA,queryUrl);

        String rawJsonSearchResults = mSearchResultTextView.getText().toString();
        outState.putString(SEARCH_RESULTS_RAW_JSON,rawJsonSearchResults);
    }

    /**
     * This method will make the View for the JSON data visible and
     * hide the error message.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showJsonDataView()
    {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mSearchResultTextView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the error message visible and hide the JSON
     * View.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showErrorMessage()
    {
        mSearchResultTextView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    /**
     * This method retrieves the search text from the EditText, constructs
     * the URL (using {@link NetworkUtils}) for the github repository you'd like to find, displays
     * that URL in a TextView, and finally fires off an AsyncTask to perform the GET request using
     * our (not yet created)
     */
    void makegitSearchQuery()
    {
        String githubQuery = mSearchBoxEditText.getText().toString();
        URL githubSearchUrl = NetworkUtils.buildUrl(githubQuery);
        mUrlDisplayTextView.setText(githubSearchUrl.toString());

        String githubSearchResults = null;
        new GithubQueryTask().execute(githubSearchUrl);
    }

    public class GithubQueryTask extends AsyncTask<URL, Void, String>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... urls) {
            URL searchUrl = urls[0];
            String githubSearchResults = null;
            try {
                githubSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return githubSearchResults;
        }

        @Override
        protected void onPostExecute(String githubSearchResults) {

            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if(githubSearchResults != null && !githubSearchResults.equals(""))
            {
                showJsonDataView();
                mSearchResultTextView.setText(githubSearchResults);
            }else{
                showErrorMessage();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemThatWasSeleted = item.getItemId();
        if(menuItemThatWasSeleted == R.id.action_search)
        {
            makegitSearchQuery();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
