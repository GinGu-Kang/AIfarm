package kr.ac.cju.acin.window;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import kr.ac.cju.acin.window.Request.RequestHttp;

public class ReadMeassageActivity extends AppCompatActivity {
    WebView webView;
    Toolbar toolbar;
    private final int MESSAGE_CHANNEL = 1001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_meassage);

        //toolbar 세팅
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_black);
        getSupportActionBar().setTitle("");

        SharedPreferences spLogin = getSharedPreferences("login", Context.MODE_PRIVATE);
        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.addJavascriptInterface(new AndroidBridge(),"android");
        webView.loadUrl(RequestHttp.getHost()+"readMeassage/?token="+spLogin.getString("token",""));
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());

    }

    //js 통신
    class  AndroidBridge{
        @JavascriptInterface
        public void move(String roomId,String username){
            Intent intent = new Intent(getApplicationContext(),MessageChannel.class);
            intent.putExtra("room_id",roomId);
            intent.putExtra("username",username);
            startActivityForResult(intent,MESSAGE_CHANNEL);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == MESSAGE_CHANNEL){
            webView.reload();
        }
    }

    private class  WebViewClient extends android.webkit.WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
//        @Override
//        public void onPageStarted(WebView view, String url, Bitmap favicon) {
//            loading();
//        }
//
//        @Override
//        public void onPageFinished(WebView view, String url)   {
//            loadingEnd();
//        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

}