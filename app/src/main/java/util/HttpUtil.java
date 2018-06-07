package util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.*;

public class HttpUtil {

//    private static String url1 = "http://pic1.win4000.com/mobile/8/53a3c199e491e.jpg";
//
//    public static void getImageFromNet(){
//        FileOutputStream fos = null;
//        BufferedInputStream bis = null;
//        HttpURLConnection httpUrl = null;
//        URL url = null;
//        int BUFFER_SIZE = 1024;
//        byte[] buf = new byte[BUFFER_SIZE];
//        int size = 0;
//        try {
//            url = new URL(url1);
//            httpUrl = (HttpURLConnection) url.openConnection();
//            httpUrl.connect();
//            bis = new BufferedInputStream(httpUrl.getInputStream());
//            fos = new FileOutputStream("d:\\haha.jpg");
//            while ((size = bis.read(buf)) != -1) {
//                fos.write(buf, 0, size);
//            }
//            fos.flush();
//        } catch (IOException e) {
//        } catch (ClassCastException e) {
//        } finally {
//            try {
//                fos.close();
//                bis.close();
//                httpUrl.disconnect();
//            } catch (IOException e) {
//            } catch (NullPointerException e) {
//            }
//        }
//    }
//
//    public static void main(String[] args) {
//
//        HttpUtil dw=new HttpUtil();
//        dw.getImageFromNet();
//    }


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
