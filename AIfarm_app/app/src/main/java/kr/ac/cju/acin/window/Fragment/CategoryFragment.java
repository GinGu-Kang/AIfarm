package kr.ac.cju.acin.window.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import androidx.fragment.app.Fragment;

import org.apache.http.util.EncodingUtils;

import kr.ac.cju.acin.window.MainActivity;
import kr.ac.cju.acin.window.R;
import kr.ac.cju.acin.window.Request.RequestHttp;
import kr.ac.cju.acin.window.onBackPressedListener;

public class CategoryFragment extends Fragment implements onBackPressedListener {
    WebView webView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment, container, false);

        SharedPreferences spLogin = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        String url = RequestHttp.getHost()+"category/?token="+spLogin.getString("token","");

        webView = view.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.loadUrl(url);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new  WebViewClient());
        webView.addJavascriptInterface(new AndroidBridge(), "android");

        return view;
    }
    class AndroidBridge{
        /*
            카테고리 항 클릭시 이동
         */
        @JavascriptInterface
        public void moveBoardList(String c_id){
            Intent intent = new Intent(getContext(),MainActivity.class);
            intent.putExtra("fragment_name","BoardListFragment");
            intent.putExtra("c_id",c_id);
            startActivity(intent);
        }

    }

    @Override
    public void onBackPressed() {
        getActivity().finish();
    }

    private class  WebViewClient extends android.webkit.WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

    }
}
