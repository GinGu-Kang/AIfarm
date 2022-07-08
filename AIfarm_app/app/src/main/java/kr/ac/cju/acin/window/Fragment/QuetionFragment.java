package kr.ac.cju.acin.window.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.fragment.app.Fragment;

import kr.ac.cju.acin.window.MainActivity;
import kr.ac.cju.acin.window.R;
import kr.ac.cju.acin.window.Request.RequestHttp;
import kr.ac.cju.acin.window.WriteBoardActivity;
import kr.ac.cju.acin.window.onBackPressedListener;

public class QuetionFragment extends Fragment implements onBackPressedListener {
    WebView webView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment, container, false);
        SharedPreferences spLogin = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        String url = RequestHttp.getHost()+"quetion/?token="+spLogin.getString("token","");
        //웹뷰 세팅
        webView = view.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.loadUrl(url);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());
        webView.addJavascriptInterface(new AndroidBridge(), "android");
        return view;
    }

    class AndroidBridge{
        /*
            각각 게시판 항목 클릭시 게시판 상세 페이지로 이동
         */
        @JavascriptInterface
        public void moveReadBoard(String id){
            Intent intent = new Intent(getContext(), MainActivity.class);
            intent.putExtra("fragment_name","ReadBoardFragment");
            intent.putExtra("id",id);
            startActivity(intent);
        }

        /*
            보내기 아이콘 클릭시
         */
        @JavascriptInterface
        public void moveWriteBoard(){
            Intent intent = new Intent(getContext(), WriteBoardActivity.class);
            intent.putExtra("isCategory",true);
            startActivity(intent);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        webView.reload();
    }

    @Override
    public void onBackPressed() {
        getActivity().finish();
    }
}
