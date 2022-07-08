package kr.ac.cju.acin.window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import kr.ac.cju.acin.window.Request.RequestHttp;

public class FormActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private WebView webView;
    private TextView toolbarTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_black);
        getSupportActionBar().setTitle("");

        Intent intent = getIntent();
        String keyword = intent.getStringExtra("keyword");
        Log.d("login",keyword);
        String url = RequestHttp.getHost()+"form/"+keyword+"/";

        toolbarTitle = findViewById(R.id.toolbar_title);
        if(keyword.equals("useService")){
            toolbarTitle.setText("서비스이용약관");
        }else{
            toolbarTitle.setText("개인정보수집");
        }

        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.loadUrl(url);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        finish();
        return super.onOptionsItemSelected(item);
    }
}