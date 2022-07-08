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
import android.widget.TextView;

import kr.ac.cju.acin.window.Request.RequestHttp;


public class MessageChannel extends AppCompatActivity {
    Toolbar toolbar;
    WebView webView;
    String username;
    String roomId;
    TextView sendBtn;
    TextView deleteBtn;
    private final int WRITE_MESSAGE=1001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_channel);

        //toolbar 세팅
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_black);
        getSupportActionBar().setTitle("");

        Intent intent = getIntent();
        roomId = intent.getStringExtra("room_id");
        username = intent.getStringExtra("username");

        SharedPreferences spLogin = getSharedPreferences("login",Context.MODE_PRIVATE);

        String url = RequestHttp.getHost()+"messageChannel/?token="+spLogin.getString("token","")+"&"
                     +"room_id="+roomId;
        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.addJavascriptInterface(new AndroidBridge(),"android");
        webView.loadUrl(url);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());

        sendBtn = findViewById(R.id.send_btn);
        //보내기 클릭시
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(),WriteMeassageActivity.class);
                intent1.putExtra("username",username);
                startActivityForResult(intent1,WRITE_MESSAGE);

            }
        });

        deleteBtn = findViewById(R.id.delete_btn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.loadUrl("javascript:delete_all('"+roomId+"')");
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == WRITE_MESSAGE){
            webView.reload();
        }
    }

    class  AndroidBridge{

        @JavascriptInterface
        public void deleteFinish(){
//            startActivity(new Intent(getApplicationContext(),ReadMeassageActivity.class));
            finish();
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