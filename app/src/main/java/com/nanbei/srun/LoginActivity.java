package com.nanbei.srun;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import entity.LocalUser;
import entity.User;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;
import util.HttpUtil;

import java.io.IOException;

public class LoginActivity extends Activity {

    private String url="http://120.79.36.200:8080/SportServer_war/login";//服务器接口地址
    private EditText school,id,password;

    private User user = new User();
    private static Gson gson = null;
    private String userJson = null;

    private Button btLogin;

    private LocalUserManager localUserManager;
    private LocalUser localUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        school = (EditText) findViewById(R.id.editTextSchool_Login);
        id = (EditText) findViewById(R.id.editTextId_Login);
        password = (EditText) findViewById(R.id.editTextPassword_Login);
        btLogin = (Button) findViewById(R.id.buttonLogin);

        localUser = new LocalUser();
        localUserManager = new LocalUserManager(new LocalUserSQLiteHelper(this));


        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String idvalue = id.getText().toString();
                final String schoolvalue = school.getText().toString();
                final String passwordvalue = password.getText().toString();

                //set学生对象的值
                user.setId(idvalue);
                user.setSchool_name(schoolvalue);
                user.setPassword(passwordvalue);

                //封装JSON格式数据
                userJson = UserToJson(user);
                System.out.println(userJson);

                //发送JSON数据到服务器
                HttpUtil.sendOkHttpRequest(url, userJson, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "登录失败，请重试", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        //解析返回值
                        String backcode = response.body().string();
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(backcode);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        int num = -3;//默认未知情况
                        try {
                            num = jsonObject.getInt("statuscode");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //System.out.println("backcode:" + num);
                        if (num == 1) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();

                                }
                            });

                            localUser.setSchool(schoolvalue);
                            localUser.setId(idvalue);
                            localUser.setPassword(passwordvalue);

                            localUserManager.insert(localUser);


                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                            intent.putExtra("idvalue", idvalue);
                            startActivity(intent);
                        } else if (num == -1) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if (num == 0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LoginActivity.this, "用户不存在", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    //数据包装成json
    public String UserToJson(User user){
        gson = new Gson();
        String json = gson.toJson(user);
        return json;
    }
}
