package kr.ac.cju.acin.window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import org.apache.http.util.EncodingUtils;

import kr.ac.cju.acin.window.Fragment.MyBoardFragment;
import kr.ac.cju.acin.window.Request.RequestHttp;

public class MyComment extends AppCompatActivity {

    Toolbar toolbar;
    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_comment);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_black);
        getSupportActionBar().setTitle("");

        SharedPreferences spLogin = getSharedPreferences("login", Context.MODE_PRIVATE);
        webView = findViewById(R.id.webView);
        String url = RequestHttp.getHost()+"myComment/";
        String posData = "token="+spLogin.getString("token","");

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.postUrl(url, EncodingUtils.getBytes(posData,"BASE64"));
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());
        webView.addJavascriptInterface(new AndroidBridge(), "android");

    }

    class AndroidBridge{
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

    private class  WebViewClient extends android.webkit.WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

        }

        @Override
        public void onPageFinished(WebView view, String url)   {

        }


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        finish();
        return super.onOptionsItemSelected(item);
    }
}