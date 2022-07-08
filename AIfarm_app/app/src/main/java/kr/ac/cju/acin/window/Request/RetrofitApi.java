package kr.ac.cju.acin.window.Request;

import com.google.gson.JsonObject;

import java.util.HashMap;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RetrofitApi {
    //로그인
    @FormUrlEncoded
    @POST("login/")
    Call<JsonObject> login(@FieldMap HashMap<String, String> param);

    @FormUrlEncoded
    @POST("logout/")
    Call<JsonObject> logout(@FieldMap HashMap<String, String> param);

    //글쓰기
    @Multipart
    @POST("writeBoard/")
    Call<JsonObject> writeBoard(@Part MultipartBody.Part token,
                                @Part MultipartBody.Part email,
                                @Part MultipartBody.Part title,
                                @Part MultipartBody.Part content,
                                @Part MultipartBody.Part category,
                                @Part MultipartBody.Part[] files);

    //이미지 전송
    @Multipart
    @POST("test/")
    Call<JsonObject> uploadImage(@Part MultipartBody.Part image);
}
