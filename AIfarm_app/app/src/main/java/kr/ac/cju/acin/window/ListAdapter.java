package kr.ac.cju.acin.window;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import kr.ac.cju.acin.window.R;

public class ListAdapter extends BaseAdapter {
    Context mContext = null;
    LayoutInflater mLayoutInflater = null;

    static final String[] LIST_ITEM = {
            "홈", "카테고리", "설정",
            "프로그램정보","문의게시판","공지사항"
    } ;

    static final int[] LIST_ITEM_ICON = {
            R.drawable.home,R.drawable.category,R.drawable.setting,
            R.drawable.info,R.drawable.callcenter,R.drawable.write_black

    };


    public ListAdapter(Context mContext){

        this.mContext = mContext;
        mLayoutInflater = LayoutInflater.from(mContext);
    }
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
        View view              = mLayoutInflater.inflate(R.layout.custom_list_view, null);
        ImageView icon         = view.findViewById(R.id.icon);
        TextView  text         = view.findViewById(R.id.text);
        text.setText(LIST_ITEM[position]);
        icon.setImageResource(LIST_ITEM_ICON[position]);
        return view;
    }


}
