package com.example.app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
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

import java.util.ArrayList;
import java.util.List;

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

                Resources resources = getResources();

                Intent emailIntent = new Intent();
                emailIntent.setAction(Intent.ACTION_SEND);
                // Native email client doesn't currently support HTML, but it doesn't hurt to try in case they fix it
                emailIntent.putExtra(Intent.EXTRA_TEXT, shareUrl);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, shareTitle);
                emailIntent.setType("message/rfc822");

                PackageManager pm = getPackageManager();
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");


                Intent openInChooser = Intent.createChooser(emailIntent, "Chooser");

                List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
                List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();
                for (int i = 0; i < resInfo.size(); i++) {
                    // Extract the label, append it, and repackage it in a LabeledIntent
                    ResolveInfo ri = resInfo.get(i);
                    String packageName = ri.activityInfo.packageName;
                    if(packageName.contains("android.email")) {
                        emailIntent.setPackage(packageName);
                    } else if(packageName.contains("plus") || packageName.contains("twitter") || packageName.contains("facebook") || packageName.contains("mms") || packageName.contains("android.gm")) {
                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName(packageName, ri.activityInfo.name));
                        intent.setAction(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        if(packageName.contains("twitter")) {
                            intent.putExtra(Intent.EXTRA_TEXT, shareUrl);
                        } else if(packageName.contains("facebook")) {
                            // Warning: Facebook IGNORES our text. They say "These fields are intended for users to express themselves. Pre-filling these fields erodes the authenticity of the user voice."
                            // One workaround is to use the Facebook SDK to post, but that doesn't allow the user to choose how they want to share. We can also make a custom landing page, and the link
                            // will show the <meta content ="..."> text from that page with our link in Facebook.
                            intent.putExtra(Intent.EXTRA_TEXT, shareUrl);
                        } else if(packageName.contains("mms")) {
                            intent.putExtra(Intent.EXTRA_TEXT, shareUrl);
                        } else if(packageName.contains("android.gm")) {
                            intent.putExtra(Intent.EXTRA_TEXT, shareUrl);
                            intent.putExtra(Intent.EXTRA_SUBJECT, shareTitle);
                            intent.setType("message/rfc822");
                        } else if(packageName.contains("plus")){
                            intent.putExtra(Intent.EXTRA_TEXT,shareUrl);
                            intent.putExtra(Intent.EXTRA_SUBJECT, shareTitle);                       }

                        intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
                    }
                }

                // convert intentList to array
                LabeledIntent[] extraIntents = intentList.toArray( new LabeledIntent[ intentList.size() ]);

                openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
                startActivity(openInChooser);

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
