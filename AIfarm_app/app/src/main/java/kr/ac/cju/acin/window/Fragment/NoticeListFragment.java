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

import kr.ac.cju.acin.window.LoginActivity;
import kr.ac.cju.acin.window.MainActivity;
import kr.ac.cju.acin.window.R;
import kr.ac.cju.acin.window.Request.RequestHttp;
import kr.ac.cju.acin.window.WriteBoardActivity;
import kr.ac.cju.acin.window.onBackPressedListener;

public class NoticeListFragment extends Fragment implements onBackPressedListener {
    private WebView webView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment, container, false);
        String url = RequestHttp.getHost()+"manager/noticeList/";
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

    class  AndroidBridge{
        /*
            공지사항 클릭시 상세 공지사항으로 이동
         */
        @JavascriptInterface
        public void moveDetailNotice(String noticeId){
            Intent intent =new Intent(getContext(), MainActivity.class);
            intent.putExtra("fragment_name","DetailNoticeFragment");
            intent.putExtra("notice_id",noticeId);
            startActivity(intent);
        }

    }
    @Override
    public void onBackPressed() {
        getActivity().finish();
    }
}
