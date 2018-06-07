package com.nanbei.srun;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import util.HttpUtil;

/**
 * Created by john on 2018/5/22.
 */

public class HomeFragment extends Fragment{

    private Button btn;
    private Button btCalender;

    private String idvalue;
    private String schoolvalue;
    private String passwordvalue;
    private double totalDistance;
    private double calories;

    private int cyear;
    public int cmonth;
    public int cday;

    TextView tvShowScore;
    TextView tvRank;
    TextView tvDistance;
    TextView tvCalorie;

//    private OnButtonClick onButtonClick;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        idvalue = (String)getArguments().get("idvalue");
        schoolvalue = (String)getArguments().get("schoolvalue");
        passwordvalue = (String)getArguments().get("passwordvalue");


        //日历
        btCalender = (Button) view.findViewById(R.id.buttoncalender);
        btCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CalendarActivity.class);
                startActivityForResult(intent, 1);

            }
        });

        //cyear=2011;
        //从日历activity获取参数
//        cyear = (int)getArguments().get("cyear");
//        cmonth = (int)getArguments().get("cmonth");
//        cday = (int)getArguments().get("cday");
        Calendar c = Calendar.getInstance();
        cyear = c.get(Calendar.YEAR);
        cmonth = c.get(Calendar.MONTH)+1;
        cday = c.get(Calendar.DAY_OF_MONTH);

        System.out.println("ccc::"+cyear+" "+cmonth+" "+cday);



        //每次从服务器中得到最新的一次跑步数据，次数，排名等
        tvShowScore = (TextView) view.findViewById(R.id.textView_points);
        tvRank = (TextView)view.findViewById(R.id.textView_rank);
        tvDistance = (TextView)view.findViewById(R.id.textView_Distance);
        tvCalorie = (TextView)view.findViewById(R.id.textView_Calorie);


        if (getActivity() != null && idvalue != "#") {

            //从服务器获取积分
            String url="http://120.79.36.200:8080/SportServer_war/getRinkingData?id=" + idvalue;//服务器接口地址
            HttpUtil.sendOkHttpRequest(url, "", new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "获取积分请求失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "获取积分请求成功", Toast.LENGTH_SHORT).show();
                        }
                    });

                    //解析返回值
                    String backcode = response.body().string();
    //                System.out.println("backcode:" + backcode);
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(backcode);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
    //                System.out.println("jsonObject:" + jsonObject);
                    String id = "";
                    int score = 0;
                    int ranking = 0;
                    try {
                        id = jsonObject.getString("id");
                        score = jsonObject.getInt("score");
                        ranking = jsonObject.getInt("ranking");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //                        final String finalId = id;
                    final int finalScore = score;
                    final int finalRanking = ranking;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvShowScore.setText(""+finalScore);
                            tvRank.setText(""+finalRanking);
                        }
                    });
                }
            });



            //获取其余的参数
            String url2 = "http://120.79.36.200:8080/SportServer_war/getHomeData?id=" + idvalue
                    + "&date=" + cyear + "/" + String.format("%02d",cmonth) + "/" + String.format("%02d",cday);//服务器接口地址
            System.out.println("d"+url2);
            HttpUtil.sendOkHttpRequest(url2, "", new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "获取服务器请求失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(final Call call, Response response) throws IOException {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "获取服务器请求成功", Toast.LENGTH_SHORT).show();
                        }
                    });

                    //解析返回值
                    String backcode = response.body().string();
                    System.out.println("backcode2:" + backcode);
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(backcode);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    System.out.println("jsonObject:" + jsonObject);
                    //                String id = "";
                    totalDistance = 0.0f;
                    calories = 0.0f;
                    try {
                        //                    id = jsonObject.getString("id");
                        totalDistance = jsonObject.getDouble("totalDistance");
                        calories = jsonObject.getDouble("calories");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //                        final String finalId = id;
                    //                final int finalScore = score;
                    //                final int finalRanking = ranking;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvDistance.setText("" + totalDistance);
                            tvCalorie.setText("" + calories);
                        }
                    });
                }
            });
        }




        btn = (Button)view.findViewById(R.id.StartRunButton);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),DynamicDemo.class);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 2){
            cyear = data.getIntExtra("year",0);
            cmonth = data.getIntExtra("month", 0);
            cday = data.getIntExtra("day", 0);
            Log.i("dayhome", "MainActivity：" + cyear + ":" + cmonth + ":" + cday);
            if(getActivity() != null && idvalue != "#") {
                //获取其余的参数
                String url2 = "http://120.79.36.200:8080/SportServer_war/getHomeData?id=" + idvalue
                        + "&date=" + cyear + "/" + String.format("%02d",cmonth) + "/" + String.format("%02d",cday);//服务器接口地址
                //System.out.println(url2);
                HttpUtil.sendOkHttpRequest(url2, "", new Callback() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "获取服务器请求失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(final Call call, Response response) throws IOException {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "获取服务器请求成功", Toast.LENGTH_SHORT).show();
                            }
                        });

                        //解析返回值
                        String backcode = response.body().string();
                        System.out.println("backcode2:" + backcode);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(backcode);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        System.out.println("jsonObject:" + jsonObject);
                        //                String id = "";
                        totalDistance = 0.0f;
                        calories = 0.0f;
                        try {
                            //                    id = jsonObject.getString("id");
                            totalDistance = jsonObject.getDouble("totalDistance");
                            calories = jsonObject.getDouble("calories");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //                        final String finalId = id;
                        //                final int finalScore = score;
                        //                final int finalRanking = ranking;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println(calories);
                                tvDistance.setText("" + totalDistance);
                                tvCalorie.setText("" + calories);
                            }
                        });
                    }
                });
            }
        }
    }


}
