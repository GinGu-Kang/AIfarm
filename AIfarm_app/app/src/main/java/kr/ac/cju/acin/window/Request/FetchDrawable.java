package kr.ac.cju.acin.window.Request;

import android.graphics.drawable.Drawable;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


public class FetchDrawable {
    InputStream is;
    public Drawable fetchDrawable() throws IOException {
        try {

            URL url = new URL(RequestHttp.getHost()+"media/IMG_20201104_053437_qmkG0Q8.jpg");
            is = (InputStream) url.getContent();
            Drawable drawable = Drawable.createFromStream(is, "src");

            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();

            int scaledWidth = width;
            int scaledHeight = height;
            drawable.setBounds(0, 0, scaledWidth, scaledHeight);
            drawable.setVisible(true, true);

            return drawable;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }finally {
        }

    }
}
