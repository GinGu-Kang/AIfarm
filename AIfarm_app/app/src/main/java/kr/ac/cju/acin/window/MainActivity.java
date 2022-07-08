package kr.ac.cju.acin.window;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kr.ac.cju.acin.window.Fragment.AlarmFragment;
import kr.ac.cju.acin.window.Fragment.BoardListFragment;
import kr.ac.cju.acin.window.Fragment.CategoryFragment;
import kr.ac.cju.acin.window.Fragment.DetailNoticeFragment;
import kr.ac.cju.acin.window.Fragment.MainFragment;
import kr.ac.cju.acin.window.Fragment.MyBoardFragment;
import kr.ac.cju.acin.window.Fragment.NoticeListFragment;
import kr.ac.cju.acin.window.Fragment.ProgramInfoFragment;
import kr.ac.cju.acin.window.Fragment.QuetionFragment;
import kr.ac.cju.acin.window.Fragment.ReadBoardFragment;
import kr.ac.cju.acin.window.Fragment.SearchFragment;
import kr.ac.cju.acin.window.Request.RequestHttp;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity  {

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    static ListView listView;
    FrameLayout mainContainer;

    static FragmentTransaction fragmentTransaction;
    static FragmentManager fm;
    private long lastTimeBackPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tedPermission();
        fm = getSupportFragmentManager();
        drawerLayout = findViewById(R.id.drawer_layout);
        listView     = findViewById(R.id.listView);
        mainContainer= findViewById(R.id.main_container);

        //툴바 세팅
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu_black);
        getSupportActionBar().setTitle("");

        //메뉴 아이콘 클릭시 리스트뷰 세팅
        ListAdapter adapter  = new ListAdapter(getApplicationContext());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                drawerLayout.closeDrawer(Gravity.LEFT);
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                switch (position){
                    case 0:
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        break;
                    case 1:
                        intent.putExtra("fragment_name","CategoryFragment");
                        startActivity(intent);
                        break;
                    case 2:
                        SharedPreferences spLogin =getSharedPreferences("login",Context.MODE_PRIVATE);
                        if(spLogin.getString("token",null) != null){
                            intent.putExtra("fragment_name","AlarmFragment");
                            startActivity(intent);
                        }else{
                            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                        }
                        break;
                    case 3:
                        intent.putExtra("fragment_name","ProgramInfoFragment");
                        startActivity(intent);
                        break;
                    case 4:
                        intent.putExtra("fragment_name","QuetionFragment");
                        startActivity(intent);
                        break;
                    case 5:
                        intent.putExtra("fragment_name","NoticeListFragment");
                        startActivity(intent);
                        break;
                 }
            }
        });

        /*
            fragment_name에 따라 부분화면 전
         */
        Intent intent = getIntent();
        String fragmentName = intent.getStringExtra("fragment_name");
        Log.d("login",fragmentName+"");
        if (fragmentName != null) {
            //내가쓴글
            if (fragmentName.equals("MyBoardFragment")) {
                appenMyBoardFragment();
            }
            //상세게시판
            else if(fragmentName.equals("ReadBoardFragment")){
                String id = intent.getStringExtra("id");
                String comment_id = intent.getStringExtra("comment_id");
                if(comment_id != null){
                    appendReadBoardFragment(id,comment_id);
                }else{
                    appendReadBoardFragment(id);
                }

            }
            //카테고리
            else if(fragmentName.equals("CategoryFragment")){
                appendCategory();
            }
            //게시글 리스트
            else if(fragmentName.equals("BoardListFragment")){
                String c_id = intent.getStringExtra("c_id");
                appendBoardListFragment(c_id);
            }
            //검색
            else if(fragmentName.equals("SearchFragment")){
                appendSearchFragment();
            }
            //문의게시판
            else if(fragmentName.equals("QuetionFragment")){
                appendQuetionFragment();
            }
            //공지사항
            else if(fragmentName.equals("NoticeListFragment")){
                appendNoticeListFragment();
            }
            //상세 공지사항
            else if(fragmentName.equals("DetailNoticeFragment")){
                String noticeId = intent.getStringExtra("notice_id");
                appendDetailNoticeFragment(noticeId);
            }
            //프로그램 정보
            else if(fragmentName.equals("ProgramInfoFragment")){
                appendProgramInfoFragment();
            }
            //알람설정
            else if(fragmentName.equals("AlarmFragment")){
                appendAlarmFragment();
            }
        }else{
            appendMainLayout();
        }

    }
    private void tedPermission(){
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {}
            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {}
        };
        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage(getResources().getString(R.string.gallery))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()){
            case R.id.action_mypage:
                Intent intent = new Intent(this,MypageActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_notification:
                SharedPreferences spLogin = getSharedPreferences("login",Context.MODE_PRIVATE);
                if(spLogin.getString("token",null) == null){

                    startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                }else{
                    startActivity(new Intent(getApplicationContext(),NotificationActivity.class));
                }


                return true;
            case R.id.action_search:
                Intent intent1 = new Intent(getApplicationContext(),MainActivity.class);
                intent1.putExtra("fragment_name","SearchFragment");
                startActivity(intent1);
                return true;
            default:
                drawerLayout.openDrawer(Gravity.LEFT);
                return super.onOptionsItemSelected(item);

        }

    }



    /*
        Fragment 추가
     */

    private void appendAlarmFragment(){
        fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.main_container,new AlarmFragment());
        //fragmentTransaction.commit();
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void appendProgramInfoFragment(){
        fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.main_container,new ProgramInfoFragment());
        //fragmentTransaction.commit();
        fragmentTransaction.commitAllowingStateLoss();
    }
    private void appendDetailNoticeFragment(String noticeId){
        fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.main_container,new DetailNoticeFragment(noticeId));
        //fragmentTransaction.commit();
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void appendNoticeListFragment(){
        fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.main_container,new NoticeListFragment());
        //fragmentTransaction.commit();
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void appendQuetionFragment(){
        fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.main_container,new QuetionFragment());
        //fragmentTransaction.commit();
        fragmentTransaction.commitAllowingStateLoss();
    }
    private void appendSearchFragment(){
        fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.main_container,new SearchFragment());
        //fragmentTransaction.commit();
        fragmentTransaction.commitAllowingStateLoss();
    }
    private void appendBoardListFragment(String c_id){
        fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.main_container,new BoardListFragment(c_id));
        //fragmentTransaction.commit();
        fragmentTransaction.commitAllowingStateLoss();
    }
    private void appendReadBoardFragment(String id){
        fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.main_container,new ReadBoardFragment(id));
        //fragmentTransaction.commit();
        fragmentTransaction.commitAllowingStateLoss();
    }
    private void appendReadBoardFragment(String id,String comment_id){
        fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.main_container,new ReadBoardFragment(id,comment_id));
        //fragmentTransaction.commit();
        fragmentTransaction.commitAllowingStateLoss();
    }
    private void appenMyBoardFragment(){
        fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.main_container,new MyBoardFragment());
        //fragmentTransaction.commit();
        fragmentTransaction.commitAllowingStateLoss();
    }

    public static void appendMainLayout(){


        fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.main_container,new MainFragment());
        //fragmentTransaction.commit();
        fragmentTransaction.commitAllowingStateLoss();
    }

    public static void appendCategory(){
        fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.main_container,new CategoryFragment());
        //fragmentTransaction.commit();
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        for(Fragment fragment : fragmentList){
            if(fragment instanceof onBackPressedListener){
                ((onBackPressedListener)fragment).onBackPressed();
                return;
            }
        }
        if(System.currentTimeMillis() - lastTimeBackPressed < 1500){
            finish();
            return;
        }
        lastTimeBackPressed = System.currentTimeMillis();
        Toast.makeText(this,"'뒤로' 버튼을 한 번 더 누르면 종료됩니다.",Toast.LENGTH_SHORT).show();
    }
}