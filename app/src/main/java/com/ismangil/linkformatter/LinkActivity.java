
/*
 * Copyright (c) 2015 Perry Ismangil
 *
 * This file is part of Link Formatter.
 *
 * Link Formatter is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Link Formatter  is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Link Formatter.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.ismangil.linkformatter;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.system.ErrnoException;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinkActivity extends AppCompatActivity {

    private static final String TAG = "LinkActivity";
    ImageView iv1 = null;
    TextView tv1 = null;
    WebView wv1 = null;
    CheckBox cb1 = null;
    ImageLoader imageLoader = ImageLoader.getInstance();
    String src = "";
    StringBuilder formattedLinkBuilder = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link);

        imageLoader.init(ImageLoaderConfiguration.createDefault(this));

        iv1 = (ImageView) findViewById(R.id.ImageView1);
        tv1 = (TextView) findViewById(R.id.linkTitle);
        wv1 = (WebView) findViewById(R.id.WebView1);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Get the intent that started this activity
        Intent intent = getIntent();
        String data = intent.getStringExtra(Intent.EXTRA_TEXT);

        List<String> urls = new ArrayList<String>();


        if (data != null) {
            // Figure out what to do based on the intent type
            if (intent.getType().equals("text/plain") && (data != null)) {

                Log.d(TAG, data);

                Matcher m = Patterns.WEB_URL.matcher(data);

                Document doc = null;

                while(m.find()) {
                    String url = m.group();
                    Log.d(TAG, url);
                    urls.add(url);

                    try {
                        doc = Jsoup.connect(url).get();

                        String title = doc.title();

                        Log.d(TAG,title);

                        tv1.setText(title);

                        Elements images = doc.select("img[src~=(?i)\\.(png|jpe?g|gif)]");
                        for (Element image : images) {

                            String sHeight = image.attr("height");
                            Log.d(TAG, sHeight);
                            Log.d(TAG, image.attr("width"));

                            try {

                                int height = Integer.valueOf(sHeight);

                                if (height >= 300) {

                                    src = image.attr("src");

                                    Log.d(TAG, src);

                                    imageLoader.displayImage(src, iv1);

                                    cb1 = (CheckBox) findViewById(R.id.checkboxImage);

                                    cb1.setVisibility(View.VISIBLE);

                                    if (cb1.isChecked()) {

                                        formattedLinkBuilder.append("<img src=\"");
                                        formattedLinkBuilder.append(src);
                                        formattedLinkBuilder.append("\" />");
                                    }

                                }
                            } catch (NumberFormatException e)
                                {
                                    Log.d(TAG,e.getMessage());
                                }

                        }


                        formattedLinkBuilder.append("<a href=\"");
                        formattedLinkBuilder.append(url);
                        formattedLinkBuilder.append("\">");
                        formattedLinkBuilder.append(title);
                        formattedLinkBuilder.append("</a>");

                        Log.d(TAG, formattedLinkBuilder.toString());

                        wv1.loadData(formattedLinkBuilder.toString(), "text/html", null);



                    } catch (IOException e)
                    {
                        Log.d(TAG,e.getMessage());
                    }


                }


            }
        }



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "This will eventually share out the formatted link", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, formattedLinkBuilder.toString());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_link, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/
        if (id == R.id.action_about){
            About.Show(this);
        }

        return super.onOptionsItemSelected(item);
    }

    public void onCheckBoxClicked(View view) {

        boolean checked = ((CheckBox) view).isChecked();

        if (!checked){
            wv1.loadData("TODO: reformat without IMG","text/html",null);
        }

    }
}
