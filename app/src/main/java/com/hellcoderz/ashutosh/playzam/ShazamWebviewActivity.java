package com.hellcoderz.ashutosh.playzam;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.hellcoderz.ashutosh.playzam.helpers.Constants;
import com.hellcoderz.ashutosh.playzam.helpers.FileHelper;
import com.hellcoderz.ashutosh.playzam.helpers.SessionManager;
import com.hellcoderz.ashutosh.playzam.helpers.UrlHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sid on 6/7/2015.
 */
public class ShazamWebviewActivity extends Activity {
    private String TAG = "Playzam Shazam";
    private ProgressDialog pDialog;
    UrlHelper urlHelper;
    private static final String target_url="https://www.shazam.com/myshazam";
    private static final String target_url_prefix="www.shazam.com";

    Intent starterIntent;

    private Context mContext;
    private WebView mWebview;
    private WebView mWebviewPop;
    private FrameLayout mContainer;
    private long mLastBackPressTime = 0;
    SessionManager sessionManager;
    FileHelper fileHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shazam_webview);
        starterIntent = getIntent();
        urlHelper = new UrlHelper();
        fileHelper = new FileHelper(getApplicationContext());
        sessionManager = new SessionManager(getApplicationContext());
        sessionManager.setPref(Constants.PREF_SHAZAM);
        pDialog = new ProgressDialog(this);
        mWebview  = (WebView) findViewById(R.id.webview);
        mContainer = (FrameLayout) findViewById(R.id.webview_frame);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        WebSettings webSettings = mWebview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSupportMultipleWindows(true);

        mWebview.setWebViewClient(new UriWebViewClient());
        mWebview.setWebChromeClient(new UriChromeClient());

        mWebview.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
        mWebview.loadUrl(target_url);


        mWebview.setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Log.e(TAG, "Downloading file from url: " + url);
                Log.e(TAG,"content length: "+contentLength);
                Log.e(TAG,"useragent: "+userAgent);
                Log.e(TAG,"mime: "+mimetype);
                Log.e(TAG,"disposition: "+contentDisposition);

                String cookie = CookieManager.getInstance().getCookie(url);
                if (cookie != null){
                    cookie += " social-session=true;";
                    saveFile(url,cookie);//---------------------------------------------------------saving download-history
                }
                //    saveFile(url);
            }
        });
        mContext= getApplicationContext();
    }

    private boolean isUserloggedinFacebook(String Cookie){//----------------------------------------FaceBook Login Success = true
        //    Log.e(TAG,Cookie);
        String[] temp = Cookie.split(";");
        for (String ar1 : temp ){
            if(ar1.contains("c_user=")){
                return true;
            }
        }
        return false;
    }

    private String getFacebookUserid(String Cookie){//----------------------------------------------Getting FB UserID
        //    Log.e(TAG,Cookie);
        String[] temp = Cookie.split(";");
        for (String ar1 : temp ){
            Log.e("FB",ar1);
            //    Log.e(TAG,ar1);
            if(ar1.contains("c_user=")){
                String[] userid = ar1.split("=");
                return userid[1];
            }
        }
        return "";
    }

    private void saveFbUser(String userid){//-------------------------------------------------------Saving FB UserID
        sessionManager.setPref(Constants.PREF_FB);
        sessionManager.setFbUser(userid);
        sessionManager.commit();
    }


    private boolean isUserloggedinShazam(String Cookie){//------------------------------------------Shazam Login Success = true
        //    Log.e(TAG,Cookie);
        String[] temp = Cookie.split(";");
        for (String ar1 : temp ){
            //    Log.e(TAG,ar1);
            if(ar1.contains("uid=")){
                return true;
            }
        }
        return false;
    }

    private class UriWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            String host = Uri.parse(url).getHost();

            if (host.equals(target_url_prefix))//when returned the shazam site.
            {
                if(mWebviewPop!=null)//popup webview != null
                {
                    mWebviewPop.setVisibility(View.GONE);
                    mContainer.removeView(mWebviewPop);
                    mWebviewPop=null;
                }
                return false;
            }

            if(host.equals("m.facebook.com"))//when you are still in facebooklogin.
            {
                return false;
            }

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            Log.e(TAG,"Hii");
            return true;
        }



        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            String cookies = CookieManager.getInstance().getCookie(url);
            if(cookies != null && !cookies.isEmpty()) {
                if (isUserloggedinShazam(cookies)) {//-----you have already logged in Shazam site.
                    sessionManager.setLogin(true);//
                    sessionManager.commit();
                    if(pDialog == null){
                        pDialog = new ProgressDialog(ShazamWebviewActivity.this);
                    }
                    pDialog.setMessage("Please wait...");
                    pDialog.setCancelable(false);
                    pDialog.show();

                    if (!url.equals(urlHelper.ENDPOINT_SHAZAM_TAGS)) {//It isnot download-history URL
                        Map<String,String> extraHeaders = new HashMap<>();
                        extraHeaders.put("X-Requested-With","XMLHttpRequest");
                        mWebview.loadUrl(urlHelper.ENDPOINT_SHAZAM_TAGS,extraHeaders);//send request download-history
                    }
                }
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            String cookies = CookieManager.getInstance().getCookie(url);
            if(cookies != null && !cookies.isEmpty()) {
                if (isUserloggedinFacebook(cookies)) {
                    String userid = getFacebookUserid(cookies);
                    saveFbUser(userid);
                    sessionManager.setPref(Constants.PREF_SHAZAM);
                    if (mWebviewPop != null) {
                        mWebviewPop.setVisibility(View.GONE);
                        mContainer.removeView(mWebviewPop);
                        mWebviewPop = null;
                    }
                    mWebview.loadUrl(urlHelper.ENDPOINT_SHAZAMHOME);
                }
            }
        }

    }

    class UriChromeClient extends WebChromeClient {

        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog,
                                      boolean isUserGesture, Message resultMsg) {

            mWebviewPop = new WebView(mContext);
            mWebviewPop.setVerticalScrollBarEnabled(false);
            mWebviewPop.setHorizontalScrollBarEnabled(false);
            mWebviewPop.setWebViewClient(new UriWebViewClient());
            mWebviewPop.getSettings().setJavaScriptEnabled(true);
            mWebviewPop.getSettings().setSavePassword(false);
            mWebviewPop.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            mContainer.addView(mWebviewPop);
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(mWebviewPop);
            Log.e(TAG,resultMsg.toString());
            resultMsg.sendToTarget();
            return true;
        }

        @Override
        public void onCloseWindow(WebView window) {
            Log.d("onCloseWindow", "called");
        }

    }

    public void saveFile(final String url, final String cookie) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {//-------------------------------------saving download-history
                InputStream is;
                try {
                    URL u = new URL(url);
                    Log.e(TAG,cookie);
                    HttpURLConnection con = (HttpURLConnection) u.openConnection();
                    con.setRequestProperty("cookie", cookie);
                    con.setRequestMethod("GET");
//                    con.setDoOutput(true);
                    con.connect();
                    is = con.getInputStream();


                    BufferedReader r = new BufferedReader(new InputStreamReader(is));
                    StringBuilder total = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        total.append(line);
                    }

                    String content = total.toString();

                    parseTags(content);//---------------------------------storing download-history to json file

                    is.close();
                    return "worked";

                }catch (IOException e) {
                    hidePDialog();

                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if(s == null){
                    showError();
                }
            }
        }.execute((Void) null);
    }

    private void showError(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Shazam Error");
        builder.setMessage("An error occured while downloading your playlist from shazam. Try running this app again. If error persists, clear data of this app from settings.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    private void parseTags(String html) {//--------------------------parsing html to json and saving download-history
        Document doc = Jsoup.parse(html);
        Elements trs = doc.select("tr");
        JSONArray jsonArray = new JSONArray();
        for( Element element : trs )
        {
            Elements tds = element.select("td");
            String title = tds.eq(0).text();
            String artist = tds.eq(1).text();
            String dater = tds.eq(2).text();
            String trackid = tds.eq(0).select("a").attr("href");

            JSONObject trackJSON = new JSONObject();
            if(title != null && !title.isEmpty()) {
                try {
                    URI uri = new URI(trackid);
                    String path = uri.getPath();
                    String idStr = path.substring(path.lastIndexOf('/') + 1);
                    trackid = idStr.substring(1);
                    trackJSON.put("trackid",trackid);
                    trackJSON.put("artist",artist);
                    trackJSON.put("date",dater);
                    trackJSON.put("title",title);
                    jsonArray.put(trackJSON);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e(TAG, "Title:" + title + ",artist:" + artist + ",date:" + dater + ",trackid:" + trackid);
            }

        }

        Log.e(TAG,"JSON: "+jsonArray.toString());
        //Save JSON Settings
        fileHelper.StoreFile(Constants.APP_SETTING_FILE,jsonArray);
        hidePDialog();
        Intent returnIntent = new Intent();
        returnIntent.putExtra("loggedin",true);
        setResult(RESULT_OK,returnIntent);
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    class MyJavaScriptInterface
    {
        @JavascriptInterface
        @SuppressWarnings("unused")
        public void processHTML(String html)
        {
        }
    }
}

