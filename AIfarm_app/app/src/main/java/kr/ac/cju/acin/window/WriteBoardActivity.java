package kr.ac.cju.acin.window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Html;
import android.text.Spannable;
import android.text.style.ImageSpan;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;

import kr.ac.cju.acin.window.Request.RequestHttp;


public class WriteBoardActivity extends AppCompatActivity {
    Toolbar toolbar;
    WebView webView;
    private final int GALLERY = 200;
    private final int TEMP_WRITE = 300;

    private ValueCallback<Uri[]> mFilePathCallback;
    //임시글 저장
    TextView saveTemp;
    //임시글 보기
    TextView writeTemp;
    //게시글 완료
    TextView saveBtn;
    // 클릭한 임시글 position
    private  int tempBoardPosition = -1;
    //수정
    Intent modifyIntent;
    String boardId;
    boolean isModify;
    boolean isCategory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_board);

        saveTemp  = findViewById(R.id.save_temp);
        writeTemp = findViewById(R.id.write_temp);
        saveBtn   = findViewById(R.id.save_btn);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_black);
        getSupportActionBar().setTitle("");
        // 임시글 숫자 초기화
        //chageText();

        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.addJavascriptInterface(new AndroidBridge(),"android");
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());

        modifyIntent = getIntent();
        isModify = modifyIntent.getBooleanExtra("isModify",false);
        isCategory = modifyIntent.getBooleanExtra("isCategory",false);
        // 게시판 수정
        if(isModify){
            boardId = modifyIntent.getStringExtra("boardId");
            webView.loadUrl(RequestHttp.getHost()+"modifyBoard/"+boardId+"/");
        }
        //문의 게시판 쓰기
        else if(isCategory){
            webView.loadUrl(RequestHttp.getHost()+"writeBoard/?category=16");
        }

        //게시판 쓰기
        else{
            webView.loadUrl(RequestHttp.getHost()+"writeBoard/");
        }


        //게시글 서버에 전송
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences spLogin = getSharedPreferences("login",Context.MODE_PRIVATE);

                String  boardId  = isModify ?modifyIntent.getStringExtra("boardId") : "";
                String url = isModify ? "/modifyBoard/" + boardId + "/" : "/writeBoard/";
                String token = spLogin.getString("token",null);
                String arg   = token + "&*,*&" + url;

                webView.loadUrl("javascript:valid('"+arg+"')");

            }
        });

        //임시글 저장하기
        saveTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.loadUrl("javascript:saveTemp()");
            }
        });
        //임시글 보기
        writeTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(getApplicationContext(),TempWriteActivity.class);
               startActivityForResult(intent,TEMP_WRITE);
            }
        });
    }



    //js 통신
    class  AndroidBridge{
        @JavascriptInterface
        public void saveTemp(String title, String category, String content) {

            try {
                saveTempBoard(title,category,content);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d("login",title);
            Log.d("login",category);
            Log.d("login",content);
        }
        @JavascriptInterface
        public void writeFinish(){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            if(isModify) {
                intent.putExtra("fragment_name", "ReadBoardFragment");
                intent.putExtra("id", boardId);
                startActivity(intent);
                finish();
            }else if(isCategory){
                finish();
            }
            else{
                intent.putExtra("fragment_name", "MyBoardFragment");
                startActivity(intent);
                finish();
            }

        }

    }

    private void finishActivity(){

        if(isModify) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("fragment_name", "ReadBoardFragment");
            intent.putExtra("id", boardId);
            startActivity(intent);
        }

        finish();
    }

    private void saveTempBoard(String title, String category, String content) throws JSONException {
        /*
           SharedPreferences temp 파일 이름으로
           key 값이 tempData에
           JSONArray 저장한다
           원소는 JSONObject
         */
        SharedPreferences spTemp = getSharedPreferences("temp", Context.MODE_PRIVATE);
        JSONArray jsonArray = null;
        String tempData = spTemp.getString("tempData",null);
        // tempData가 null 아니면 임시글 변경
        if(tempData != null){
            jsonArray = new JSONArray(tempData);
        }else{
            jsonArray =new JSONArray();
        }
        JSONObject object;
        // tempBoardPosition이 -1 아니면 임시글 수정으로 함
        if(tempBoardPosition != -1){
            jsonArray.remove(tempBoardPosition);
            object= jsonArray.getJSONObject(tempBoardPosition);
        }
        else    object = new JSONObject();
        object.put("title",title);
        object.put("category",category);
        object.put("content",content);
        object.put("date", getCurrentDate());
        jsonArray.put(object);
        jsonArray = reverse(jsonArray);
        SharedPreferences.Editor editor = spTemp.edit();
        // tempData key 값으로 jsonArray String으로 변환해서 저장
        editor.putString("tempData",jsonArray.toString());
        editor.commit();
        Toast.makeText(getApplicationContext(),"임시글 저장되었습니다.",Toast.LENGTH_LONG).show();
    }

    private String getCurrentDate(){
        /*
            현재 시간을 특 포맷으로 변환해서 반환
         */
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy.MM.dd HH:mm");
        return sdf.format(date);
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == GALLERY && resultCode == RESULT_OK){
            if (mFilePathCallback == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }
            Uri[] results = new Uri[]{data.getData()};
            mFilePathCallback.onReceiveValue(results);
            mFilePathCallback = null;

        }else if(requestCode == TEMP_WRITE && data != null){
            tempBoardPosition = data.getIntExtra("position",-1);
            displayTempBoard();


        }else{
            if (mFilePathCallback != null) mFilePathCallback.onReceiveValue(null);
            mFilePathCallback = null;
            super.onActivityResult(requestCode, resultCode, data);
        }

    }
    private JSONArray reverse(JSONArray arr){
        /*
            JSONArray 원소를 역순으로 함
         */
        JSONArray jsonArray = new JSONArray();
        for(int i = arr.length()-1; i >= 0; i--){
            try {
                jsonArray.put(arr.get(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonArray;
    }
    private void displayTempBoard(){
        JSONObject object = null;
        try {
            object = getJsonObject(tempBoardPosition);
            String title = object.getString("title");
            String category = object.getString("category");
            String content  = object.getString("content");
            String arg = title+"&*,*&"+category+"&*,*&"+content;
            webView.loadUrl("javascript:displayTempBoard('"+arg+"')");

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
    // 임시저장된 JSONArray에서 특정jsonobject 가져오기
    private JSONObject getJsonObject(int position) throws JSONException {
        SharedPreferences spTemp = getSharedPreferences("temp",Context.MODE_PRIVATE);
        String tempData = spTemp.getString("tempData",null);
        JSONArray jsonArray = null;
        if(tempData != null){
            jsonArray = new JSONArray(tempData);
            return jsonArray.getJSONObject(position);
        }
        return null;
    }

    private class WebChromeClient extends android.webkit.WebChromeClient{
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            if (mFilePathCallback != null) {
                mFilePathCallback.onReceiveValue(null);
            }
            mFilePathCallback = filePathCallback;
            openGallery();
            return true;
        }
    }
    private class  WebViewClient extends android.webkit.WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

    }

    private void openGallery(){
        /*
            갤러리 열기
         */
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent. setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, GALLERY);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finishActivity();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finishActivity();
    }
}