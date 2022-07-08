package kr.ac.cju.acin.window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import kr.ac.cju.acin.window.Request.RequestHttp;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView findPw;
    TextView register;
    Button loginButton;
    EditText email;
    EditText pw;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_black);
        getSupportActionBar().setTitle("");

        findPw     = findViewById(R.id.findPw);
        register   = findViewById(R.id.register);
        loginButton= findViewById(R.id.login_button);
        email      = findViewById(R.id.email);
        pw         = findViewById(R.id.pw);
        sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email_txt = email.getText().toString();
                String pw_txt    = pw.getText().toString();
                HashMap<String,String> map = new HashMap<>();

                map.put("email",email_txt);//retrofit2
                map.put("pw",pw_txt);
                Call<JsonObject> call = RequestHttp.getInstance().login(map);
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        try {
                            JSONObject obj = new JSONObject(response.body().toString());
                            StringBuffer sb = new StringBuffer();
                            if(obj.getBoolean("isLogin")){
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("email",map.get("email"));
                                editor.putString("pw",map.get("pw"));
                                editor.putString("name",obj.getString("name"));
                                editor.putString("token",obj.getString("token"));
                                editor.commit();
                                Intent intent = new Intent(getApplicationContext(),MypageActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }else{
                                sb.append("로그인 실패했습니다.");
                                Toast.makeText(getApplicationContext(),sb,Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {e.printStackTrace();}
                    }
                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {}
                });



            }
        });

        findPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),FindPwActivity.class));
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(getApplicationContext(),MypageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(),MypageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}