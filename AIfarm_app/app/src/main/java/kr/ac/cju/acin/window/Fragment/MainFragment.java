package kr.ac.cju.acin.window.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.fragment.app.Fragment;

import org.apache.http.util.EncodingUtils;

import kr.ac.cju.acin.window.LoginActivity;
import kr.ac.cju.acin.window.MainActivity;
import kr.ac.cju.acin.window.R;
import kr.ac.cju.acin.window.Request.RequestHttp;
import kr.ac.cju.acin.window.WriteBoardActivity;


public class MainFragment extends Fragment{
    WebView webView;
    private SharedPreferences spLogin;
    private final int MODIFY = 100;
    private boolean isModify = false;
    private String boardId = null;
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment, container, false);

        spLogin = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        String url = RequestHttp.getHost()+"main/all/";
        String posData = "token="+spLogin.getString("token","");

        //웹뷰 세팅
        webView    = view.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.postUrl(url, EncodingUtils.getBytes(posData, "BASE64"));
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());
        webView.addJavascriptInterface(new AndroidBridge(), "android");

        return view;
    }




    class  AndroidBridge{
        /*
            상세
         */
        @JavascriptInterface
        public void moveReadBoard(String id){
            Intent intent =new Intent(getContext(), MainActivity.class);
            intent.putExtra("fragment_name","ReadBoardFragment");
            intent.putExtra("id",id);
            startActivity(intent);
        }
        /*
            메인에서 글쓰기 버튼 클릭시
         */
        @JavascriptInterface
        public void writeButtonClicked(){
            SharedPreferences sp = getActivity().getSharedPreferences("login",Context.MODE_PRIVATE);
            if(!sp.getString("token","").equals(""))
                startActivity(new Intent(getContext(), WriteBoardActivity.class));
            else
                startActivity(new Intent(getContext(),LoginActivity.class));
        }
        /*
            poster 사진 클릭시 브라우저로 이동
         */
        @JavascriptInterface
        public void moveBrowser(String url){
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        }
    }

}
