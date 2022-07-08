package kr.ac.cju.acin.window.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.fragment.app.Fragment;

import kr.ac.cju.acin.window.R;
import kr.ac.cju.acin.window.Request.RequestHttp;
import kr.ac.cju.acin.window.onBackPressedListener;

public class DetailNoticeFragment extends Fragment implements onBackPressedListener {

    private WebView webView;
    private String noticeId;
    public DetailNoticeFragment(String noticeId){
        this.noticeId = noticeId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment, container, false);
        String url = RequestHttp.getHost()+"manager/mobileDetailNotice/?notice_id="+noticeId;
        //웹뷰 세팅
        webView = view.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.loadUrl(url);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());
        return view;
    }
    @Override
    public void onBackPressed() {
        getActivity().finish();
    }
}
