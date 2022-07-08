package kr.ac.cju.acin.window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import org.apache.http.util.EncodingUtils;

import kr.ac.cju.acin.window.Fragment.CategoryFragment;
import kr.ac.cju.acin.window.Fragment.MainFragment;
import kr.ac.cju.acin.window.Request.RequestHttp;

public class NotificationActivity extends AppCompatActivity {

    Toolbar toolbar;
    WebView webView;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_black);
        getSupportActionBar().setTitle("");

        webView = findViewById(R.id.webView);
        SharedPreferences spLogin = getSharedPreferences("login", Context.MODE_PRIVATE);
        String url = RequestHttp.getHost()+"notification/";
        String posData = "token="+spLogin.getString("token","");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.postUrl(url, EncodingUtils.getBytes(posData, "BASE64"));
//       webView.loadUrl(RequestHttp.getHost()+"main/");
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());
        webView.addJavascriptInterface(new AndroidBridge(), "android");


    }
    class AndroidBridge{
       /*
          알림에서 받은쪽지 클릭시
        */
        @JavascriptInterface
        public void moveReadMessage(String room_id,String username){
            Intent intent = new Intent(getApplicationContext(),MessageChannel.class);
            intent.putExtra("room_id",room_id);
            intent.putExtra("username",username);
            startActivity(intent);
          
        }
        /*
            상세
         */
        @JavascriptInterface
        public void moveReadBoard(String id,String comment_id){
            Intent intent =new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("fragment_name","ReadBoardFragment");
            intent.putExtra("id",id);
            intent.putExtra("comment_id",comment_id);
            startActivity(intent);
        }

    }
    private void loading() {

        new Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        progressDialog = new ProgressDialog(NotificationActivity.this);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("잠시만 기다려 주세요...");
                        progressDialog.show();
                    }
                }, 0);
    }

    private void loadingEnd() {
        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                }, 0);
    }
    private class  WebViewClient extends android.webkit.WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            loading();
        }

        @Override
        public void onPageFinished(WebView view, String url)   {
            loadingEnd();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

}