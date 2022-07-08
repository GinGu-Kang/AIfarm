package kr.ac.cju.acin.window;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.HashMap;


public class TempWriteActivity extends AppCompatActivity {
    SwipeMenuListView listView;
    LinearLayout noneTempLayout;
    Toolbar toolbar;
    TextView actionbarTitle;
    SharedPreferences spTemp;
    private ListviewAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_write);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_black);
        getSupportActionBar().setTitle("");

        spTemp= getSharedPreferences("temp", Context.MODE_PRIVATE);
        listView = findViewById(R.id.listView);
        noneTempLayout = findViewById(R.id.none_temp);
        actionbarTitle = findViewById(R.id.actionbar_title);

        SwipeMenuCreator creator = new SwipeMenuCreator(){

            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());

                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                deleteItem.setWidth(170);
                deleteItem.setIcon(R.drawable.delete);
                menu.addMenuItem(deleteItem);
            }
        };
        adapter =new ListviewAdapter();
        listView.setAdapter(adapter);
        listView.setMenuCreator(creator);

        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                Log.d("login",position+"");
                showDialog("임시글삭제","임시글을 삭제하시겠습니까?",position);
                return false;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("position",position);
                setResult(1003,intent);
                finish();
            }
        });

    }
    private void showDialog(String title, String msg,int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(title).setMessage(msg);
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                JSONArray jsonArray = getJSONArray();
                if(jsonArray.length() != 0){
                    jsonArray.remove(position);
                    SharedPreferences.Editor editor = spTemp.edit();
                    editor.putString("tempData",jsonArray.toString());
                    editor.commit();

                    listView.setAdapter(new ListviewAdapter());
                    if(jsonArray.length() != 0)
                        actionbarTitle.setText("임시글("+jsonArray.length()+")");
                    else
                        actionbarTitle.setText("임시글");

                }

            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
    private JSONArray getJSONArray(){


        String tempData = spTemp.getString("tempData",null);
        JSONArray jsonArray = null;
        if(tempData != null){
            try {
                jsonArray = new JSONArray(tempData);
                return jsonArray;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return new JSONArray();
    }
    class ListviewAdapter extends BaseAdapter{
        JSONArray jsonArray = null;
        LayoutInflater mLayoutInflater;
        public ListviewAdapter() {
            jsonArray = getJSONArray();
            if(jsonArray.length() != 0) {
                listView.setVisibility(View.VISIBLE);
                noneTempLayout.setVisibility(View.GONE);
                actionbarTitle.setText("임시글(" + jsonArray.length() + ")");
            }else {
                listView.setVisibility(View.GONE);
                noneTempLayout.setVisibility(View.VISIBLE);
            }
            mLayoutInflater = LayoutInflater.from(getApplicationContext());

        }

        @Override
        public int getCount() {
            return jsonArray.length();
        }

        @Override
        public Object getItem(int position) {

            try {
                 return jsonArray.getJSONObject(position);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view  = mLayoutInflater.inflate(R.layout.write_temp_listview, null);
            TextView tempTitle = view.findViewById(R.id.temp_title);
            TextView tempDate  = view.findViewById(R.id.temp_date);
            JSONObject jsonObject = null;
            try {
                jsonObject = jsonArray.getJSONObject(position);
                tempTitle.setText(jsonObject.getString("title"));
                tempDate.setText(jsonObject.getString("date"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return view;
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

}
