package kr.ac.cju.acin.window;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import kr.ac.cju.acin.window.Fragment.MainFragment;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        WebView webView = (WebView) findViewById(R.id.webvw);
        webView.setWebViewClient(new WebViewClient());

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new AndroidBridge(), "android");
        webView.loadUrl("http://203.252.240.63:80/newMain");
    }


    class  AndroidBridge{

        // 홈 화면이동
        @JavascriptInterface
        public void goHomeScreen(){
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
        }

        // 작물 진단 이동
        @JavascriptInterface
        public void sickness_notice() {
            Intent intent = new Intent(getApplicationContext(), Sickness.class);

            startActivity(intent);
        }
        // 농업 날씨 이동
        @JavascriptInterface
        public void weather() {
            Intent intent = new Intent(getApplicationContext(), weather.class);
            intent.putExtra("button" , "weather");
            startActivity(intent);
        }
        // 병해충 검색
        @JavascriptInterface
        public void insect() {
            Intent intent = new Intent(getApplicationContext(), insect.class);
            intent.putExtra("button" , "insect");
            startActivity(intent);
        }
    }
}