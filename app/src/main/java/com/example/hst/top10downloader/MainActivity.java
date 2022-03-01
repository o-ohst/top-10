package com.example.hst.top10downloader;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    //static log tag with class name (type logt)
    private static final String TAG = "MainActivity";
    //declare listview reference
    private ListView listApps;
    private String feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml";
    private int feedLimit = 10;
    private static final String STATE_URL = "feedUrl";
    private static final String STATE_LIMIT = "feedLimit";
    private String feedCachedUrl = "INVALIDATED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //casting and storing view into reference listApps by id
        listApps = (ListView) findViewById(R.id.xmlListView);

        //restore values in onCreate because downloadUrl is in onCreate, which is called before onRestore
        //check for null(change of state)
        if (savedInstanceState != null) {
            feedUrl = savedInstanceState.getString(STATE_URL);
            feedLimit = savedInstanceState.getInt(STATE_LIMIT);
        }

        downloadUrl(String.format(feedUrl, feedLimit));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feeds_menu, menu);
        if (feedLimit == 10) {
            menu.findItem(R.id.mnu10).setChecked(true);
        } else {
            menu.findItem(R.id.mnu25).setChecked(true);
        }
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_URL, feedUrl);
        outState.putInt(STATE_LIMIT, feedLimit);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.mnuFree:
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
                break;
            case R.id.mnuPaid:
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml";
                break;
            case R.id.mnuSongs:
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml";
                break;
            case R.id.mnu10:
            case R.id.mnu25:
                if (!item.isChecked()) {
                    item.setChecked(true);
                    feedLimit = 35 - feedLimit;
                    Log.d(TAG, "onOptionsItemSelected: " + item.getTitle() + " setting feedLimit to " + feedLimit);
                } else {
                    Log.d(TAG, "onOptionsItemSelected: " + item.getTitle() + " feedLimit unchanged");
                }
                break;
            case R.id.mnuRefresh:
                Log.d(TAG, "onOptionsItemSelected: refresh buttton clicked");
                feedCachedUrl = "INVALIDATED";
                Log.d(TAG, "onOptionsItemSelected: feed cache invalidated");
                break;
            case R.id.mnuWarning:
                WarningDialogue warningDialogue = new WarningDialogue();
                warningDialogue.show(getFragmentManager(),"warning");
                Log.d(TAG, "onOptionsItemSelected: user is being warned");
            default:
                return super.onOptionsItemSelected(item);
        }
        downloadUrl(String.format(feedUrl, feedLimit));
        return true;
    }

    private void downloadUrl(String feedUrl) {
        //check that cache is not invalidated, else do not download
        if (!feedUrl.equalsIgnoreCase(feedCachedUrl)) {
            Log.d(TAG, "downloadUrl: starting Asynctask");
            //DownloadData instance
            DownloadData downloadData = new DownloadData();
            downloadData.execute(feedUrl);
            feedCachedUrl = feedUrl;
            Log.d(TAG, "downloadUrl: done");
        } else {
            Log.d(TAG, "downloadUrl: URL not changed");
        }
    }

    //AsyncTask
    private class DownloadData extends AsyncTask<String, Void, String> {
        //Logt
        private static final String TAG = "DownloadData";

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
//            Log.d(TAG, "onPostExecute: parameter is " + s);
            ParseApplications parseApplications = new ParseApplications();
            parseApplications.parse(s);

            //initiating arrayadapter
//            ArrayAdapter<FeedEntry> arrayAdapter = new ArrayAdapter<FeedEntry>(
//                    //context, destination for each item, arraylist
//                    MainActivity.this, R.layout.list_item, parseApplications.getApplications()
//            );
//            //connecting adapter to listview
//            listApps.setAdapter(arrayAdapter);

            FeedAdapter feedAdapter = new FeedAdapter(
//                    //context, destination for each item, arraylist
                    MainActivity.this, R.layout.list_apps, parseApplications.getApplications()
            );
            listApps.setAdapter(feedAdapter);

        }


        //doInBackground: compulsory main method of AsyncTask
        @Override
        //String... (multiple Strings can be passed in)
        //strings: parameter, an array of strings
        //LogD is removed when app uploaded
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: starts with " + strings[0]);
            String rssFeed = downloadXML(strings[0]);
            if (rssFeed == null) {
                Log.e(TAG, "doInBackground: Error downloading");
            }
            return rssFeed;
        }

        private String downloadXML(String urlPath) {
            StringBuilder xmlResult = new StringBuilder();

            try {
                //urlPath(Str) -> URL obj -> (HttpUC) url.openConn -> HttpURLConnection obj -> get response
                //InputStream obj from HttpURLConnection.getInputStream-> InputStreamReader(inputStream) -> BufferedReader(inputStreamReader)
                URL url = new URL(urlPath);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                int response = connection.getResponseCode();
                Log.d(TAG, "downloadXML: The response code was " + response);
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                int charsRead;
                //increase char[] array length for larger download
                //inputBuffer: char array
                char[] inputBuffer = new char[500];
                while (true) {
                    //read single char from inputStream INTO inputBuffer
                    //returns the number of characters read and set as charsRead
                    charsRead = reader.read(inputBuffer);
                    //will return -1 if no more data
                    if (charsRead < 0) break;
                    //returns string from char array(inputBuffer), for length of charsRead
                    if (charsRead > 0)
                        xmlResult.append(String.copyValueOf(inputBuffer, 0, charsRead));
                }
                reader.close();
                return xmlResult.toString();

            } catch (MalformedURLException e) {
                Log.e(TAG, "downloadXML: Invalid URL " + e.getMessage());
            }
            //Malformed is subclass of IOException, so it goes before IOExc
            catch (IOException e) {
                Log.e(TAG, "downloadXML: IO Exception" + e.getMessage());
            } catch (SecurityException e) {
                Log.e(TAG, "downloadXML: Security exception. " + e.getMessage());
//                e.printStackTrace();
            }
            return null;
        }
    }
}

