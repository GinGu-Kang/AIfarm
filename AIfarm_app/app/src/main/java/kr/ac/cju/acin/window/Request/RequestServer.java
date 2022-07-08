package kr.ac.cju.acin.window.Request;

import com.google.gson.JsonObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestServer {
    private static RequestServer requestServer = null;
    private RequestServer(){

    }

    public static RequestServer getInstance(){
        if(requestServer == null)
            requestServer = new RequestServer();
        return requestServer;
    }
    public void logout(HashMap<String,String> map){
        Call<JsonObject> call = RequestHttp.getInstance().logout(map);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {}
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {}
        });

    }
}
