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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by perry on 03/11/2015.
 */
public class About {

    private static final String TAG = "About";

    static String VersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(),0).versionName;
        }
        catch (PackageManager.NameNotFoundException e) {
            return "Unknown";
        }
    }
    public static void Show(Activity callingActivity) {
        //Use a Spannable to allow for links highlighting
        SpannableString aboutText = new SpannableString("Version " + VersionName(callingActivity)+ "\n\n"
                + callingActivity.getString(R.string.about));
        Log.d(TAG, aboutText.toString());
        //Generate views to pass to AlertDialog.Builder and to set the text
        View about;
        TextView tvAbout;
        try {
            //Inflate the custom view
            LayoutInflater inflater = callingActivity.getLayoutInflater();
            about = inflater.inflate(R.layout.about, (ViewGroup) callingActivity.findViewById(R.id.aboutView));
            tvAbout = (TextView) about.findViewById(R.id.aboutText);
        }
        catch(InflateException e) {
            //Inflater can throw exception, unlikely but default to TextView if it occurs
            about = tvAbout = new TextView(callingActivity);
        }
        //Set the about text
        tvAbout.setText(aboutText);
        // Now Linkify the text
        Linkify.addLinks(tvAbout, Linkify.ALL);
        //Build and show the dialog
        new AlertDialog.Builder(callingActivity)
                .setTitle("About " + callingActivity.getString(R.string.app_name))
                .setCancelable(true)
                .setIcon(R.mipmap.launcher_icon)
                .setPositiveButton("OK", null)
                .setView(about)
                .show();	//Builder method returns allow for method chaining
    }
}
