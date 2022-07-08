package kr.ac.cju.acin.window;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SpinnerAdapter extends ArrayAdapter<String> {
    public SpinnerAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

}
