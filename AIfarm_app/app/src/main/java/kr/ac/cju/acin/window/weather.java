package kr.ac.cju.acin.window;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

public class weather extends Activity {


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.test);
        WebView webView = (WebView) findViewById(R.id.webvw);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

// 화면 비율
        webSettings.setUseWideViewPort(true);       // wide viewport를 사용하도록 설정
        webSettings.setLoadWithOverviewMode(true);  // 컨텐츠가 웹뷰보다 클 경우 스크린 크기에 맞게 조정
// 웹뷰 멀티 터치 가능하게 (줌기능)
        webSettings.setBuiltInZoomControls(true);   // 줌 아이콘 사용
        webSettings.setSupportZoom(true);


        webView.setWebViewClient(new WebViewClient());
        Intent intent = getIntent();

        webSettings.setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new AndroidBridge(), "android");
        webView.loadUrl("http://203.252.240.63:80/weather");








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
    }
}
