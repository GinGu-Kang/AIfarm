package kr.ac.cju.acin.window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashMap;

import kr.ac.cju.acin.window.Request.RequestHttp;
import kr.ac.cju.acin.window.Request.RequestServer;


public class MypageActivity extends AppCompatActivity {
    Toolbar toolbar;
    ListView listView;
    Intent intent;
    SharedPreferences sharedPreferences;
    private boolean isLogin = false;
    String email;
    String name;
    LinearLayout mypageContainer;
    TextView  mName;
    TextView  mEmail;
    MypageListAdapter adapter;
    TextView myInfomationBtn;

    final int LOGIN_REQUEST = 1002;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_black);
        getSupportActionBar().setTitle("");

        listView = findViewById(R.id.listView);
        adapter = new MypageListAdapter(getApplicationContext());

        mypageContainer = findViewById(R.id.mypage_contaner);
        mName = findViewById(R.id.name);
        mEmail= findViewById(R.id.email);
        sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);

        intent = new Intent();


        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        if(isLogin){
                            logout();
                            adapter.LIST_ITEM[0]="로그인";
                            adapter.LIST_ITEM_ICON[0] = R.drawable.logout;
                            isLogin = false;
                        }else{
                            startActivityForResult(new Intent(getApplicationContext(),LoginActivity.class),LOGIN_REQUEST);
                        }
                        adapter.notifyDataSetChanged();
                        break;
                    case 1:
                        if(sharedPreferences.getString("token",null)!=null){
                            startActivity(new Intent(getApplicationContext(),ReadMeassageActivity.class));
                        }else{
                            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                        }

                        break;
                    case 2:
                        if(sharedPreferences.getString("token",null)!=null){
                            startActivity(new Intent(getApplicationContext(),MyComment.class));
                        }else{
                            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                        }

                        break;
                    case 3:
                        if(sharedPreferences.getString("token",null)!=null){
//                            startActivity(new Intent(getApplicationContext(),ReadBoardActivity.class));

                            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                            intent.putExtra("fragment_name","MyBoardFragment");
                            startActivity(intent);
                        }else{
                            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                        }
                        break;

                }

            }
        });

        myInfomationBtn = findViewById(R.id.my_infomation_btn);
        myInfomationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ModifyUserActivity.class));
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        email = sharedPreferences.getString("email", "");
        name  = sharedPreferences.getString("name", "");
        if(!email.equals("")) {
            mypageContainer.setVisibility(View.VISIBLE);
            mName.setText(email);
            mEmail.setText(name);
            isLogin = true;
            adapter.LIST_ITEM[0]="로그아웃";
            adapter.LIST_ITEM_ICON[0] = R.drawable.logout;

        }else{
            adapter.LIST_ITEM[0]="로그인";
            adapter.LIST_ITEM_ICON[0] = R.drawable.login;

        }
        adapter.notifyDataSetChanged();

    }



    private void logout(){
        String token = sharedPreferences.getString("token","");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
        mypageContainer.setVisibility(View.GONE);
        HashMap<String,String> map = new HashMap<>();
        map.put("token",token);
        RequestServer.getInstance().logout(map);
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        finish();
        return super.onOptionsItemSelected(item);
    }
}