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
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import kr.ac.cju.acin.window.LoginActivity;
import kr.ac.cju.acin.window.MainActivity;
import kr.ac.cju.acin.window.R;
import kr.ac.cju.acin.window.Request.RequestHttp;
import kr.ac.cju.acin.window.WriteBoardActivity;
import kr.ac.cju.acin.window.WriteMeassageActivity;
import kr.ac.cju.acin.window.onBackPressedListener;

public class ReadBoardFragment extends Fragment implements onBackPressedListener {
    private WebView webView;
    private String id;
    private SharedPreferences spLogin;
    private String comment_id = null;

    //생성
    public ReadBoardFragment(String id){
        this.id = id;
    }
    public ReadBoardFragment(String id,String comment_id){
        this.id = id;
        this.comment_id = comment_id;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment, container, false);
        spLogin =  getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);

        String url = null;
        if(comment_id != null){
            url = RequestHttp.getHost()+"readBoard/"+id+"/?token="+spLogin.getString("token","")+"&noti_comment_id="+comment_id;
        }else{
            url = RequestHttp.getHost()+"readBoard/"+id+"/?token="+spLogin.getString("token","");
        }


        //웹뷰 세팅
        webView = view.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());
        webView.addJavascriptInterface(new AndroidBridge(), "android");
        webView.loadUrl(url);


        return view;
    }


    @Override
    public void onBackPressed() {
      getActivity().finish();
    }


    class AndroidBridge{
        /*
            상세 게시판에서 수정번튼 클릭시
         */
        @JavascriptInterface
        public void modify(String id){

            Intent intent = new Intent(getContext(), WriteBoardActivity.class);
            intent.putExtra("isModify",true);
            intent.putExtra("boardId",id);
            startActivity(intent);
            getActivity().finish();
        }
        /*
            상세 게시판에서 좋아요 버튼 클릭시 로그인 체
         */
        @JavascriptInterface
        public boolean loginCheck(){

            if(spLogin.getString("token",null) == null){
                startActivity(new Intent(getContext(), LoginActivity.class));
                return false;
            }
            return true;
        }
        /*
            상세게시판에서 쪽지 아이콘 클릭시
         */
        @JavascriptInterface
        public void writeMeassage(String username){
            Intent intent = new Intent(getContext(), WriteMeassageActivity.class);
            intent.putExtra("username",username);
            intent.putExtra("from","readBoard");
            startActivity(intent);
        }

        @JavascriptInterface
        public void deleteFinish(){
            Intent intent = new Intent(getContext(),MainActivity.class);
            intent.putExtra("fragment_name","MyBoardFragment");
            startActivity(intent);
            getActivity().finish();
        }

    }





}
