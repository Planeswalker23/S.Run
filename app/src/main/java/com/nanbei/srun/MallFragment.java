package com.nanbei.srun;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
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
//    private MallAdapter adapter;
    ListView listView = null;
//    private String username;

//    private MallManager accountManager;
//    private AccountAdapter adapter;
    private String idvalue;
//    private String schoolvalue;
//    private String passwordvalue;
    private List<Goods> goodsList;

    private Handler handler;
    private Handler handler1;
    ImageView goodImgV;

    ArrayList<String> imagesUrl;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_mall, container, false);
//        adapter = new MallAdapter();
//        accountManager = new AccountManager(new AccountSQLiteHelper(getActivity()));
        listView = (ListView) view.findViewById(R.id.mallList);
        idvalue = (String)getArguments().get("idvalue");
//        schoolvalue = (String)getArguments().get("schoolvalue");
//        passwordvalue = (String)getArguments().get("passwordvalue");

         goodsList = new ArrayList<Goods>();

         imagesUrl = new ArrayList<String>();

//        if (getActivity() != null && idvalue != "#") {

            //从服务器获取商品
            String url = "http://120.79.36.200:8080/SportServer_war/getCommodities";//服务器接口地址
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
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(getActivity(), "获取商品请求成功", Toast.LENGTH_SHORT).show();
//                        }
//                    });

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
                            int gid=-1;
                            String gname = "";
                            double gprice = 0;
                            String gpic = "";
                            try {
                                gid = jsonObject.getInt("id");
                                gname = jsonObject.getString("name");
                                gprice = jsonObject.getDouble("price");
                                gpic = jsonObject.getString("imgurl");

                                final Goods good = new Goods();
                                good.setGid(gid);
                                good.setGname(gname);
                                good.setGprice(gprice);
                                good.setGpic(gpic);
                                goodsList.add(good);
                                System.out.println("listmall:"+goodsList.size());

                                imagesUrl.add(good.getGpic());

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
//                        adapter = new MallAdapter();
//                        adapter.addWeapons(goodsList);
//                        ((ListView) view.findViewById(R.id.mallList)).setAdapter(adapter);


                        String[] stringUrls = imagesUrl.toArray(new String[imagesUrl.size()]);
                        System.out.println("imgurl"+stringUrls);
                        ImageAdapter adapter = new ImageAdapter(getActivity(), 0, stringUrls);
                        listView.setAdapter(adapter);
                    }
                }
            }
        };

        return view;
    }





    public class ImageAdapter extends ArrayAdapter<String> {

        private ListView mListView;

//        private List<Goods> goodsItemList = new ArrayList<Goods>();
        /**
         * 图片缓存技术的核心类，用于缓存所有下载好的图片，在程序内存达到设定值时会将最少最近使用的图片移除掉。
         */
        private LruCache<String, BitmapDrawable> mMemoryCache;

        public ImageAdapter(Context context, int resource, String[] objects) {
            super(context, resource, objects);
            // 获取应用程序最大可用内存
            int maxMemory = (int) Runtime.getRuntime().maxMemory();
            int cacheSize = maxMemory / 8;
            mMemoryCache = new LruCache<String, BitmapDrawable>(cacheSize) {
                @Override
                protected int sizeOf(String key, BitmapDrawable drawable) {
                    return drawable.getBitmap().getByteCount();
                }
            };
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (mListView == null) {
                mListView = (ListView) parent;
            }
            String url = getItem(position);
            View view;
            if (convertView == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.mall_item, null);
            } else {
                view = convertView;
            }
            ImageView image = (ImageView) view.findViewById(R.id.goodImageView);
            image.setImageResource(R.drawable.mall);
            image.setTag(url);
            BitmapDrawable drawable = getBitmapFromMemoryCache(url);
            if (drawable != null) {
                image.setImageDrawable(drawable);
            } else {
                BitmapWorkerTask task = new BitmapWorkerTask();
                task.execute(url);
            }
            final Goods goodItem = goodsList.get(position);
            ((TextView) view.findViewById(R.id.goodNamePrice)).setText(goodItem.getGid()+"\n"+goodItem.getGname() + "\n" + goodItem.getGprice());

            view.findViewById(R.id.buttonBuy).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //goodsItemList.remove(position);
                    //点击兑换，根据商品id和学生id查询是否可以兑换
                    String url = "http://120.79.36.200:8080/SportServer_war/consumeCommodity?sid=" +
                            idvalue + "&cid=" + goodItem.getGid();//服务器接口地址
                    System.out.println("");
                    HttpUtil.sendOkHttpRequest(url, "", new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), "请求兑换商品失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            //解析返回值
                            String backcode = response.body().string();
                            int temp = Integer.parseInt(backcode);
                            System.out.println("temp"+temp);
                            if(temp == 0){
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(), "剩余积分不足，兑换商品失败", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }else {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(), "兑换商品成功，请等待收货", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        }
                    });
                    notifyDataSetChanged();
                }
            });
            return view;
        }

        /**
         * 将一张图片存储到LruCache中。
         *
         * @param key
         *            LruCache的键，这里传入图片的URL地址。
         * @param drawable
         *            LruCache的值，这里传入从网络上下载的BitmapDrawable对象。
         */
        public void addBitmapToMemoryCache(String key, BitmapDrawable drawable) {
            if (getBitmapFromMemoryCache(key) == null) {
                mMemoryCache.put(key, drawable);
            }
        }

        /**
         * 从LruCache中获取一张图片，如果不存在就返回null。
         *
         * @param key
         *            LruCache的键，这里传入图片的URL地址。
         * @return 对应传入键的BitmapDrawable对象，或者null。
         */
        public BitmapDrawable getBitmapFromMemoryCache(String key) {
            return mMemoryCache.get(key);
        }

        /**
         * 异步下载图片的任务。
         *
         * @author guolin
         */
        class BitmapWorkerTask extends AsyncTask<String, Void, BitmapDrawable> {

            String imageUrl;

            @Override
            protected BitmapDrawable doInBackground(String... params) {
                imageUrl = params[0];
                // 在后台开始下载图片
                Bitmap bitmap = downloadBitmap(imageUrl);
                BitmapDrawable drawable = new BitmapDrawable(getContext().getResources(), bitmap);
                addBitmapToMemoryCache(imageUrl, drawable);
                return drawable;
            }

            @Override
            protected void onPostExecute(BitmapDrawable drawable) {
                ImageView imageView = (ImageView) mListView.findViewWithTag(imageUrl);
                if (imageView != null && drawable != null) {
                    imageView.setImageDrawable(drawable);
                }
            }

            /**
             * 建立HTTP请求，并获取Bitmap对象。
             *
             * @param imageUrl
             *            图片的URL地址
             * @return 解析后的Bitmap对象
             */
            private Bitmap downloadBitmap(String imageUrl) {
                Bitmap bitmap = null;
                HttpURLConnection con = null;
                try {
                    URL url = new URL(imageUrl);
                    con = (HttpURLConnection) url.openConnection();
                    con.setConnectTimeout(5 * 1000);
                    con.setReadTimeout(10 * 1000);
                    bitmap = BitmapFactory.decodeStream(con.getInputStream());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }
                }
                return bitmap;
            }

        }

    }



//    public class MallAdapter extends BaseAdapter {
//
//        private Context context;
//
//        private List<Goods> goodsItemList = new ArrayList<Goods>();
//
//        public void addWeapons(List<Goods> goodsItems) {
//            this.goodsItemList.addAll(goodsItems);
//            System.out.println(goodsItems);
//        }
//
//        public void addWeapon(Goods goods) {
//            this.goodsItemList.add(goods);
//        }
//
//        @Override
//        public int getCount() {
//            return goodsItemList.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return goodsItemList.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return goodsItemList.get(position).getGid();
//        }
//
//        @Override
//        public View getView(final int position, View convertView, ViewGroup parent) {
//
//            final Goods goodItem = goodsItemList.get(position);
//            final View view = View.inflate(getActivity(), R.layout.mall_item, null);
////            handler1 = new Handler() {
////                @Override
////                public void handleMessage(Message msg) {
////                    if(msg.what==666){
////                        Bitmap bitmap = ((Bitmap) msg.obj);
////                        ((ImageView)view.findViewById(R.id.goodImageView)).setImageBitmap(bitmap);
////                    }
////                }
////            };
//
//            System.out.println("vvv"+view);
//            System.out.println("pos::"+goodItem.getGpic());
////            getURLimage1(goodItem.getGpic());
//            ((TextView) view.findViewById(R.id.goodNamePrice)).setText(goodItem.getGid()+"\n"+goodItem.getGname() + "\n" + goodItem.getGprice());
////            ((ImageView) view.findViewById(R.id.goodImageView)).setTag();
//
//            view.findViewById(R.id.buttonBuy).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    //goodsItemList.remove(position);
//                    //点击兑换，根据商品id和学生id查询是否可以兑换
//                    String url = "http://120.79.36.200:8080/SportServer_war/consumeCommodity?sid=" +
//                            idvalue + "&cid=" + goodItem.getGid();//服务器接口地址
//                    System.out.println("");
//                    HttpUtil.sendOkHttpRequest(url, "", new Callback() {
//                        @Override
//                        public void onFailure(Call call, IOException e) {
//                            getActivity().runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Toast.makeText(getActivity(), "请求兑换商品失败", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                        }
//
//                        @Override
//                        public void onResponse(Call call, Response response) throws IOException {
//                            getActivity().runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Toast.makeText(getActivity(), "请求兑换商品成功", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//
//                            //解析返回值
//                            String backcode = response.body().string();
//                            int temp = Integer.parseInt(backcode);
//                            System.out.println("temp"+temp);
//                            if(temp == 0){
//                                getActivity().runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Toast.makeText(getActivity(), "剩余积分不足，兑换商品失败", Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//
//                            }else {
//                                getActivity().runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Toast.makeText(getActivity(), "兑换商品成功", Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//
//                            }
//                        }
//                    });
//
//
//                    notifyDataSetChanged();
//                }
//            });
//            return view;
//        }
//
//    }
//
//    public Bitmap getURLimage(final String url) {
//
//        final Bitmap[] bmp = {null};
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//
//                    URL myurl = new URL(url);
//                    // 获得连接
//                    HttpURLConnection conn = (HttpURLConnection) myurl.openConnection();
//                    conn.setConnectTimeout(6000);//设置超时
//                    conn.setDoInput(true);
//                    conn.setUseCaches(false);//不缓存
//                    conn.connect();
//                    InputStream is = conn.getInputStream();//获得图片的数据流
//                    bmp[0] = BitmapFactory.decodeStream(is);
//                    is.close();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//
//            }
//        }).start();
//        return bmp[0];
//    }
//
//    public void getURLimage1(final String url){
//        //Bitmap bitmap = null;
//        HttpUtil.sendOkHttpRequestByGet(url, "", new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                System.out.println("error ");
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                byte[] content = response.body().bytes();
//                Bitmap bitmap = BitmapFactory.decodeByteArray(content,0,content.length);
//                Message message = Message.obtain();
//                message.what = 666;
//                message.obj = bitmap;
//                handler1.sendMessage(message);
//                if(bitmap!=null){
//                    System.out.println("getBitmap");
//                }else{
//                    System.out.println("not getBitmap");
//                }
//
//            }
//        });
//    }




}
