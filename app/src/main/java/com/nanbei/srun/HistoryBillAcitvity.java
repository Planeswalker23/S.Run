package com.nanbei.srun;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import entity.Goods;
import entity.HistoryBill;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import util.HttpUtil;

/**
 * Created by john on 2018/6/9.
 */

public class HistoryBillAcitvity  extends Activity {
    private static final int COMPLETED = 0;
    private Handler handler;

    private String idvalue;
    private List<HistoryBill> historyBillList;

    HistoryBillAdapter historyBillAdapter;

    private int cnt=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_historybill);

        Intent intent = getIntent();
        idvalue = intent.getStringExtra("idvalue");

        historyBillList = new ArrayList<HistoryBill>();

        if (idvalue != "#") {
            //从服务器获取历史账单
            String url = "http://120.79.36.200:8080/SportServer_war/getConsumeHistory?sid=" + idvalue;//服务器接口地址
            HttpUtil.sendOkHttpRequest(url, "", new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            Toast.makeText(this, "获取历史账单请求失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            Toast.makeText(this, "获取历史账单请求成功", Toast.LENGTH_SHORT).show();
                        }
                    });

                    //解析返回值
                    String backcode = response.body().string();
//                  System.out.println("backcode:" + backcode);
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(backcode);

//                        int len=jsonArray.length();
                        for(int i=0;i<jsonArray.length();i++){
                            // 遍历 jsonarray 数组，把每一个对象转成 json 对象
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

//                            System.out.println("jsonObject:" + jsonObject);
                            int hbid = 0;
                            long btime = 0;
                            String name = "";
                            double price = 0.0f;
                            try {
                                hbid = i + 1;
                                btime = jsonObject.getLong("btime");
                                name = jsonObject.getString("name");
                                price = jsonObject.getDouble("price");

//                                System.out.println(btime+" "+name+" "+price);

                                final HistoryBill historyBill = new HistoryBill();
                                historyBill.setBtime(btime);
                                historyBill.setName(name);
                                historyBill.setPrice(price);
                                historyBillList.add(historyBill);
//                                System.out.println("listmall:"+goodsList.size());

//                                imagesUrl.add(good.getGpic());

                                //处理完成后给handler发送消息
                                Message msg = new Message();
                                msg.what = COMPLETED;
                                handler.sendMessage(msg);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            });

        }

        System.out.println("hhhh"+historyBillList);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == COMPLETED) {
                    if (historyBillList.size() != 0) {
                        historyBillAdapter = new HistoryBillAdapter();
                        historyBillAdapter.addWeapons(historyBillList);
                        ((ListView) findViewById(R.id.historybill_list)).setAdapter(historyBillAdapter);
//        ((ListView) findViewById(R.id.listview2)).setSelection(0);
                    }
                }
            }
        };
    }



    public class HistoryBillAdapter extends BaseAdapter {

        private List<HistoryBill> historyBillList = new ArrayList<>();

        public void addWeapons(List<HistoryBill> accountItems) {
            this.historyBillList.addAll(accountItems);
        }

        public void addWeapon(HistoryBill accountItem) {
            this.historyBillList.add(accountItem);
        }

        @Override
        public int getCount() {
            return historyBillList.size();
        }

        @Override
        public Object getItem(int position) {
            return historyBillList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return historyBillList.get(position).getHbid();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final HistoryBill historyBill = historyBillList.get(position);
            View view = View.inflate(HistoryBillAcitvity.this, R.layout.historybill_item, null);

            long timeStamp = historyBill.getBtime();  //获取当前时间戳,也可以是你自已给的一个随机的或是别人给你的时间戳(一定是long型的数据)
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//这个是你要转成后的时间的格式
            String sd = sdf.format(new Date(Long.parseLong(String.valueOf(timeStamp))));   // 时间戳转换成时间

            ((TextView) view.findViewById(R.id.tv_billname)).setText(historyBill.getName());
            ((TextView) view.findViewById(R.id.tv_billprice)).setText(String.valueOf(historyBill.getPrice()));
            ((TextView) view.findViewById(R.id.tv_billtime)).setText(sd);
            return view;
        }
    }
}
