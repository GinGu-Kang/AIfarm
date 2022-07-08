package kr.ac.cju.acin.window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.apache.http.util.EncodingUtils;

import java.util.HashMap;

import kr.ac.cju.acin.window.Fragment.MainFragment;
import kr.ac.cju.acin.window.Request.RequestHttp;
import kr.ac.cju.acin.window.Request.RequestServer;

public class ModifyUserActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_user);

        //툴바 세팅
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_black);
        getSupportActionBar().setTitle("");

        SharedPreferences spLogin = getSharedPreferences("login", Context.MODE_PRIVATE);
        String url = RequestHttp.getHost() + "modifyUser/?token="+spLogin.getString("token","");

        //웹뷰 세팅
        webView    = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.loadUrl(url);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());
        webView.addJavascriptInterface(new AndroidBridge(), "android");

    }
    class AndroidBridge{
        /*
           비밀번호 변경시 로그아웃
         */
        @JavascriptInterface
        public void logout(){
            Toast.makeText(getApplicationContext(),"변경",Toast.LENGTH_LONG).show();
            SharedPreferences sharedPreferences = getSharedPreferences("login",Context.MODE_PRIVATE);
            String token = sharedPreferences.getString("token","");
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.commit();
            HashMap<String,String> map = new HashMap<>();
            map.put("token",token);
            RequestServer.getInstance().logout(map);

            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            finish();

        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}