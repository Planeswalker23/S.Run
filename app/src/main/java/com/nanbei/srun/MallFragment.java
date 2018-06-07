package com.nanbei.srun;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import entity.Goods;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import util.HttpUtil;

/**
 * Created by john on 2018/6/3.
 */

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MallFragment extends Fragment {
    private static final int COMPLETED = 0;

    //    private AccountManager accountManager;
    private MallAdapter adapter;
    ListView listView = null;
//    private String username;

//    private MallManager accountManager;
//    private AccountAdapter adapter;
    private String idvalue;
//    private String schoolvalue;
//    private String passwordvalue;
    private List<Goods> goodsList;

    private Handler handler;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_mall, container, false);
//        adapter = new MallAdapter();
//        accountManager = new AccountManager(new AccountSQLiteHelper(getActivity()));
        listView = (ListView) view.findViewById(R.id.mallList);
        idvalue = (String)getArguments().get("idvalue");
//        schoolvalue = (String)getArguments().get("schoolvalue");
//        passwordvalue = (String)getArguments().get("passwordvalue");

         goodsList = new ArrayList<Goods>();


//        if (getActivity() != null && idvalue != "#") {

            //从服务器获取积分
            String url = "http://120.79.36.200:8080/OracleFinalWork_war/getItems/1";//服务器接口地址
            HttpUtil.sendOkHttpRequest(url, "", new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "获取商品请求失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "获取商品请求成功", Toast.LENGTH_SHORT).show();
                        }
                    });

                    //解析返回值
                    String backcode = response.body().string();
                    System.out.println("mall_backcode:" + backcode);
                    JSONArray jsonArray = null;
                    try {
//                        jsonObject = new JSONObject(backcode);
                        jsonArray = new JSONArray(backcode);


                        int len=jsonArray.length();
                        for(int i=0;i<jsonArray.length();i++){
                            // 遍历 jsonarray 数组，把每一个对象转成 json 对象
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

//                            System.out.println("jsonObject:" + jsonObject);
                            String gname = "";
                            double gprice = 0;
                            String gpic = "";
                            try {
                                gname = jsonObject.getString("name");
                                gprice = jsonObject.getDouble("price");
                                gpic = jsonObject.getString("imgurl");

                                final Goods good = new Goods();
                                good.setGid(len-i);
                                good.setGname(gname);
                                good.setGprice(gprice);
                                good.setGpic(gpic);
                                goodsList.add(good);
                                System.out.println("listmall:"+goodsList.size());

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
//        }


        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == COMPLETED) {
                    //将输入传入adapter
                    System.out.println("goodlist.size"+goodsList.size());
                    if(goodsList.size() != 0){
                        adapter = new MallAdapter();
                        adapter.addWeapons(goodsList);
                        ((ListView) view.findViewById(R.id.mallList)).setAdapter(adapter);
                    }
                }
            }
        };


        return view;
    }

    public class MallAdapter extends BaseAdapter {

        private Context context;

        private List<Goods> goodsItemList = new ArrayList<Goods>();

//        public MallAdapter(Context context, List<Goods> appList)
//        {
//            this.context = context;
//            this.goodsItemList = appList;
//        }

        public void addWeapons(List<Goods> goodsItems) {
            this.goodsItemList.addAll(goodsItems);
            System.out.println(goodsItems);
        }

        public void addWeapon(Goods goods) {
            this.goodsItemList.add(goods);
        }

        @Override
        public int getCount() {
            return goodsItemList.size();
        }

        @Override
        public Object getItem(int position) {
            return goodsItemList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return goodsItemList.get(position).getGid();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final Goods goodItem = goodsItemList.get(position);
                View view = View.inflate(getActivity(), R.layout.mall_item, null);
                ((TextView) view.findViewById(R.id.goodNamePrice)).setText(goodItem.getGname() + "\n" + goodItem.getGprice());
                view.findViewById(R.id.buttonBuy).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goodsItemList.remove(position);
                        notifyDataSetChanged();
                    }
                });

            return view;
        }

    }
}
