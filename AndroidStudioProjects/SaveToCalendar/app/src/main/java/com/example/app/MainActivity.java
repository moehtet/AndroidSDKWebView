package com.example.app;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final WebView crimeView = (WebView) findViewById(R.id.webView);
        crimeView.loadUrl("http://www.spiralvibe.com/ssl/moeHtet/AndroidWebView/webview.html");
        crimeView.setWebViewClient(new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading (WebView view, String url)
        {
        //appsploration://createCalendarEvent?description=Arsenal%20vs%20Cardiff&location=Emirates%20Stadium&start=2014-01-01T15:00:00+0800&end=2014-01-01T20:00:00+0800
            if (url.startsWith("appsploration://createCalendarEvent?")) {

                Log.d(this.getClass().getCanonicalName(), url);

                Calendar beginCal = Calendar.getInstance();

                Calendar endCal = Calendar.getInstance();

                String parsed = url.substring(36);

                String[] components = parsed.split("&");

                String description= "Description";
                try {
                    description = URLDecoder.decode(components[0].substring(12), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                String location= "Location";
                try {
                    location = URLDecoder.decode(components[1].substring(9), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String start=components[2].substring(6);
                String end=components[3].substring(4);

                Integer startYear=Integer.parseInt(start.substring(0,4));
                Integer startMonth=Integer.parseInt(start.substring(5,7))-1;
                Integer startDay=Integer.parseInt(start.substring(8,10));
                Integer startHour=Integer.parseInt(start.substring(11,13));
                Integer startMin=Integer.parseInt(start.substring(14,16));
                beginCal = new GregorianCalendar(startYear,startMonth,startDay,startHour,startMin,1);


                Integer endYear=Integer.parseInt(end.substring(0,4));
                Integer endMonth=Integer.parseInt(end.substring(5,7))-1;
                Integer endDay=Integer.parseInt(end.substring(8,10));
                Integer endHour=Integer.parseInt(end.substring(11,13));
                Integer endMin=Integer.parseInt(end.substring(14,16));
                endCal = new GregorianCalendar(endYear,endMonth,endDay,endHour,endMin,2);

                calendarevent(beginCal, endCal, description,location);

                return true;

            }

            return false;

        }

    });
    }

    // Create a calendar event
    public void calendarevent(Calendar begintime, Calendar endtime,
                              String eventName,String eventLocation)
    {
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("beginTime", begintime.getTimeInMillis());
        intent.putExtra("allDay", false);
        //intent.putExtra("rrule", "FREQ=YEARLY");
        intent.putExtra("endTime", endtime.getTimeInMillis());
        intent.putExtra("title", eventName);
        intent.putExtra("eventLocation",eventLocation);
        //intent.putExtra("eventTimezone", "UTC/GMT +8:00");
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

}
