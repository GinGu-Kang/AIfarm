package kr.ac.cju.acin.window;

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

import org.json.JSONException;

import kr.ac.cju.acin.window.Request.RequestHttp;

public class WriteMeassageActivity extends AppCompatActivity {
    Toolbar toolbar;
    WebView webView;
    TextView send_btn;
    String username = null;
    String fromIntent = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_meassage);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_black);
        getSupportActionBar().setTitle("");

        //게시판에서 쪽지 클릭시
        Intent intent = getIntent();

        //게시판 작성한 사용자 아이
        username = intent.getStringExtra("username");
        fromIntent = intent.getStringExtra("from");


        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.addJavascriptInterface(new AndroidBridge(),"android");
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(RequestHttp.getHost()+"writeMeassage/?username="+username);

        // 보내기 버튼
        send_btn = findViewById(R.id.send_btn);
        //보내기 버튼 클릭시
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences spLogin = getSharedPreferences("login", Context.MODE_PRIVATE);
                webView.loadUrl("javascript:send_message('"+spLogin.getString("token","")+"')");
            }
        });

    }
    //js 통신
    class  AndroidBridge{


        @JavascriptInterface
        public void writeFinish(String room_id,String username){

            if(fromIntent != null){
                Intent intent = new Intent(getApplicationContext(),MessageChannel.class);
                intent.putExtra("room_id",room_id);
                intent.putExtra("username",username);
                startActivity(intent);
                finish();

            }else {
                Log.d("login", "hihihihihi");
                finish();
            }

        }
    }
    private class  WebViewClient extends android.webkit.WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        finish();
        return super.onOptionsItemSelected(item);
    }
}