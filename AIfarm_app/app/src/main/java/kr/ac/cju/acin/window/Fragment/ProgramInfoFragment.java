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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import kr.ac.cju.acin.window.FormActivity;
import kr.ac.cju.acin.window.R;
import kr.ac.cju.acin.window.Request.RequestHttp;
import kr.ac.cju.acin.window.onBackPressedListener;

public class ProgramInfoFragment extends Fragment implements onBackPressedListener {
    private WebView webView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment, container, false);
        String url = RequestHttp.getHost()+"programInfo/";
        //웹뷰 세팅
        webView    = view.findViewById(R.id.webView);
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
            useService : 서비스이용약관 화면
            privacy : 개인정보 수집 화면
         */
        @JavascriptInterface
        public void moveForm(String keyword){

            Intent intent = new Intent(getContext(), FormActivity.class);
            intent.putExtra("keyword",keyword);
            startActivity(intent);
        }

    }
    @Override
    public void onBackPressed() {
        getActivity().finish();
    }
}
