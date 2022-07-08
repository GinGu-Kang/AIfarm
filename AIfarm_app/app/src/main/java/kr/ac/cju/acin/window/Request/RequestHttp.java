package kr.ac.cju.acin.window.Request;

import android.graphics.Bitmap;

import com.google.gson.FieldAttributes;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RequestHttp {
    private static final String HOST = "http://203.252.240.63:80/";
    private static RequestHttp requestHttp=null;
    Retrofit retrofit;
    RetrofitApi retrofitApi;
    private RequestHttp() {
//        OkHttpClient.Builder builder = new OkHttpClient.Builder();
//        builder.addInterceptor(new CookieInterceptor());
//
//       OkHttpClient client = builder.build();

        retrofit = new Retrofit.Builder()
                .baseUrl(HOST)
                .addConverterFactory(GsonConverterFactory.create())
//                .client(client)
                .build();
        retrofitApi = retrofit.create(RetrofitApi.class);
    }
    public static RequestHttp getInstance(){
        if (requestHttp == null) {
            requestHttp = new RequestHttp();
        }
        return requestHttp;
    }
    public static String getHost(){
        return HOST;
    }
    //로그인
    public Call<JsonObject> login(HashMap<String, String> hashMap) {
        return retrofitApi.login(hashMap);
    }
    public Call<JsonObject> logout(HashMap<String,String> map) {

        return retrofitApi.logout(map);
    }

    //글쓰기
    public Call<JsonObject> writeBoard(String token,String email,String title, String content, String category,  ArrayList<String> url){
        MultipartBody.Part[] files = new MultipartBody.Part[url.size()];

        for(int i=0; i<url.size(); i++){
            File f = new File(url.get(i));
            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), f);
            files[i] = MultipartBody.Part.createFormData("files", f.getName(), reqFile);

        }

        return retrofitApi.writeBoard(
                                      MultipartBody.Part.createFormData("token",token),
                                      MultipartBody.Part.createFormData("email",email),
                                      MultipartBody.Part.createFormData("title",title),
                                      MultipartBody.Part.createFormData("content",content),
                                      MultipartBody.Part.createFormData("category",category),
                                      files);
    }
//    private File createFileFromBitmap(Bitmap bitmap) throws IOException {
//        File newFile = new File(getActivity().getFilesDir(), makeImageFileName());
//        FileOutputStream fileOutputStream = new FileOutputStream(newFile);
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
//        fileOutputStream.close();
//        return newFile;
//    }



    //이미지 업로드
    public Call<JsonObject>  imageUpload(String imagePath){
          File image = new File(imagePath);

          RequestBody uploadImage = RequestBody.create(MediaType.parse("multipart/form-data"),image);

        return  retrofitApi.uploadImage(MultipartBody.Part.createFormData("image","image",uploadImage));
    }




}
