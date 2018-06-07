package com.nanbei.srun;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.gson.Gson;
import entity.Student;
import entity.User;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import util.HttpUtil;

import java.io.IOException;

public class RegistActivity extends Activity {

    private String url="http://120.79.36.200:8080/SportServer_war/register";//服务器接口地址
    private EditText school,id,name,password,tel;//用户名和密码
    private Button regist;//提交按钮

    private Student student = new Student();
    private User user = new User();

    private static Gson gson = null;
    private String userJson = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        //初始化数据
        school = (EditText) findViewById(R.id.editTextSchool);
        id = (EditText) findViewById(R.id.editTextID);
        name = (EditText) findViewById(R.id.editTextName);
        password = (EditText) findViewById(R.id.editTextPassword);
        regist = (Button) findViewById(R.id.buttonRegistByHttp);
        tel = (EditText) findViewById(R.id.editTextTel);



        regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取输入的数据
                String idvalue = id.getText().toString();
                String namevalue = name.getText().toString();
                String schoolvalue = school.getText().toString();
                final String passwordvalue = password.getText().toString();
                String telvalue = tel.getText().toString();

                //set学生对象的值
                user.setId(idvalue);
                user.setName(namevalue);
                user.setPassword(passwordvalue);
                user.setSchool_name(schoolvalue);
                user.setTel(telvalue);

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
                                Toast.makeText(RegistActivity.this, "注册失败，请重试", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RegistActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                            }
                        });

                        Intent intent = new Intent(RegistActivity.this, LoginActivity.class);
                        startActivity(intent);
                        System.out.println(response.body().string());
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
