package kr.ac.cju.acin.window;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import kr.ac.cju.acin.window.R;

public class MypageListAdapter extends BaseAdapter {
    LayoutInflater mLayoutInflater = null;
    Context mContext = null;
    boolean isLogin = false;
    public MypageListAdapter(Context mContext){
        this.mContext = mContext;

        mLayoutInflater = LayoutInflater.from(mContext);
    }
    String[] LIST_ITEM = {
            "로그인", "쪽지함","내가쓴 댓글","내가쓴글"
    };
    int[] LIST_ITEM_ICON = {
            R.drawable.login,R.drawable.write_gray,R.drawable.comment_gray,R.drawable.msg_gray
    };
    @Override
    public int getCount() {
        return LIST_ITEM.length;
    }

    @Override
    public Object getItem(int position) {
        return LIST_ITEM[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view  = mLayoutInflater.inflate(R.layout.mypage_list_view, null);
        ImageView icon = view.findViewById(R.id.icon);
        TextView  text = view.findViewById(R.id.text);

        text.setText(LIST_ITEM[position]);
        icon.setImageResource(LIST_ITEM_ICON[position]);


        return view;
    }
}
