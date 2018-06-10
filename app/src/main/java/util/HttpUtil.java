package util;

import okhttp3.*;

public class HttpUtil {

    public static void sendOkHttpRequest(final String address,final String content,final Callback callback){
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"),content);
        Request request = new Request.Builder()
                .url(address)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
