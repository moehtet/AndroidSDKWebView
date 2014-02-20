package com.example.app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
    WebView embeddedBrowser;
    ImageButton backBtn;
    ImageButton forwardBtn;
    ImageButton reloadBtn;
    ImageButton closeBtn;
    ImageButton shareBtn;
    String shareUrl;
    String shareTitle;

    final Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        shareUrl="http://nodejs.org/";
        embeddedBrowser = (WebView) findViewById(R.id.webView);
        embeddedBrowser.getSettings().setJavaScriptEnabled(true);
        embeddedBrowser.getSettings().setBuiltInZoomControls(true);
        embeddedBrowser.getSettings().setDisplayZoomControls(false);
        embeddedBrowser.setWebViewClient(new myWebClient());
        embeddedBrowser.loadUrl(shareUrl);

        backBtn = (ImageButton) findViewById(R.id.back);
        forwardBtn=(ImageButton) findViewById(R.id.forward);
        reloadBtn=(ImageButton) findViewById(R.id.reloadBtn);
        closeBtn=(ImageButton)findViewById(R.id.closeBtn);
        shareBtn=(ImageButton)findViewById(R.id.shareBtn);

        //////////////////////


        ImageButton alertBtn=(ImageButton)findViewById(R.id.alertBtn);
        alertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final CharSequence[] items = {"Open in Browser", "Share to Other Apps"};

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        if(item==0){
                            Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(shareUrl));
                            startActivity(intent);
                        }
                        else{
                            Intent sendIntent = new Intent();
                            sendIntent.setAction(Intent.ACTION_SEND);
                            sendIntent.putExtra(Intent.EXTRA_SUBJECT,shareTitle);
                            sendIntent.putExtra(Intent.EXTRA_TEXT, shareUrl);
                            //sendIntent.putExtra(Intent.EXTRA_STREAM,shareUrl);
                            sendIntent.setType("text/plain");
                            // startActivity(sendIntent);
                            startActivity(Intent.createChooser(sendIntent, "How do you want to share?"));

                        }
                    }
                });
                AlertDialog alert = builder.create();

                alert.show();
            }
        });
        //////////////////////

        backBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(embeddedBrowser.canGoBack()) {
                    embeddedBrowser.goBack();
                }
            }
        });

        forwardBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(embeddedBrowser.canGoForward()){
                    embeddedBrowser.goForward();
                }
            }
        });

        reloadBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                embeddedBrowser.reload();

            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), Home.class);
                startActivityForResult(myIntent, 0);
            }
        });

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_SUBJECT,shareTitle);
                sendIntent.putExtra(Intent.EXTRA_TEXT, shareUrl);
                //sendIntent.putExtra(Intent.EXTRA_STREAM,shareUrl);
                sendIntent.setType("text/plain");
               // startActivity(sendIntent);
                startActivity(Intent.createChooser(sendIntent, "How do you want to share?"));


            }
        });
    }

    public class myWebClient extends WebViewClient
    {
        ProgressDialog progressDialog;
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onLoadResource(WebView view,String url){
            if(progressDialog==null){
                progressDialog=new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("Loading...");
                progressDialog.show();
            }
        }

        @Override
        public void onPageFinished(WebView view,String url){
            try{
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                    progressDialog=null;
                }
            }
            catch (Exception exception){
                exception.printStackTrace();
            }

            shareTitle=view.getTitle();

        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            shareUrl=url;
            view.loadUrl(url);
           // this.onLoadResource(view,url);
            return true;

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && embeddedBrowser.canGoBack()) {
            embeddedBrowser.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
