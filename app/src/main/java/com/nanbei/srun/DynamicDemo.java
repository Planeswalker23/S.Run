package com.nanbei.srun;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import util.HttpUtil;
import util.RunDateUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 此demo实现时时动态画运动轨迹
 * author zhh
 */
public class DynamicDemo extends Activity implements SensorEventListener {

    DecimalFormat df = new DecimalFormat("#.00");
    private MediaPlayer mediaPlayer = new MediaPlayer();

    //计时相关
    private long startTime;
    private long endTime;

    // 定位相关
    private LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private int mCurrentDirection;
    private double mCurrentLat;
    private double mCurrentLon;

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private Button btMyLocation;
    private Button btPeopleAroundYou;

    private TextView info;
    private RelativeLayout progressBarRl;

    private TextView timer_tv_1, TextView_map_distance, showcalorie, showSpeed, getpoint;

    private Timer timer = null;
    private TimerTask timerTask = null;
    private int i = 0;


    boolean isFirstLoc = true; // 是否首次定位
    private MyLocationData locData;
    float mCurrentZoom = 19.0f;//默认地图缩放比例值

    private SensorManager mSensorManager;
    private Context context;

    private String time_hh, time_mm, time_ss, time_m, time_s;
    private String dis, caluli, timePerKM;
    private String score_Share;

    //是否开启画笔
    private boolean isOpenDraw = false;
    //是否显示周围用户坐标
    private boolean isShowUsers = false;

    //起点图标
    BitmapDescriptor startBD = BitmapDescriptorFactory.fromResource(R.drawable.start);
    //终点图标
    BitmapDescriptor finishBD = BitmapDescriptorFactory.fromResource(R.drawable.end);

    List<LatLng> points = new ArrayList<LatLng>();//位置点集合
    Polyline mPolyline;//运动轨迹图层
    LatLng last = new LatLng(0, 0);//上一个定位点
    MapStatus.Builder builder;

    //定义当前周围用户坐标(纬度,经度)
    private double[] coordinates = {39.9152, 116.4038,
            39.9154, 116.4038,
            39.9152, 116.4036,
            39.9154, 116.4036,
            39.9156, 116.4040,
            39.9156, 116.4042,
            39.9158, 116.4044,
            39.9160, 116.4040,
            39.9160, 116.4044
    };

    //添加自定义覆盖类
    private Marker usersMarker;
    //初始化全局 bitmap 信息，不用时及时 recycle
    BitmapDescriptor usersBD = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);

    //定义用户运动起始、结束点以及距离
    private LatLng startPlace;
    private LatLng endPlace;
    private double distance = 0.0;

    //运动类型
    private String sportsType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dyn);
        this.context = this;

        sportsType = "run";

        if (ContextCompat.checkSelfPermission(DynamicDemo.this, Manifest.permission.
                WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(DynamicDemo.this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }else{
            initMediaPlayer(R.raw.runmusic);//初始化MediaPlayer的开始运动资源
        }

        initView();//开始停止按钮初始化

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);// 获取传感器管理服务
        // 地图初始化
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);

        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING, true, null));

        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(mCurrentZoom);
        mBaiduMap.setMapStatus(msu);

        /**
         * 添加地图缩放状态变化监听，当手动放大或缩小地图时，拿到缩放后的比例，然后获取到下次定位，
         *  给地图重新设置缩放比例，否则地图会重新回到默认的mCurrentZoom缩放比例
         */
        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {

            @Override
            public void onMapStatusChangeStart(MapStatus arg0) {

            }

            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus arg0) {
                mCurrentZoom = arg0.zoom;
            }

            @Override
            public void onMapStatusChange(MapStatus arg0) {

            }
        });

        initLocation();//初始化定位

        getPermission();

        myLocation();//定位到我当前的位置

        showPeopleAroundYou();//绘制测试用户坐标（纬度、经度代表北京）

//        showMovementDate();//显示运动数据

//        savePathByScreensShots();//保存运动截图(在有开始和结尾经纬度的情况下开启此功能)

    }

    private void initLocation() {
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);//只用gps定位，需要在室外定位。
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        //获取即使位置
        mLocClient.start();//初次定位
    }

    private void initOverlay() {
        // add marker overlay
        LatLng userll;

        //定位点在天安门附近
        for (int i = 0; i < coordinates.length; i += 2) {
            userll = new LatLng(coordinates[i], coordinates[i + 1]);
            MarkerOptions ooA = new MarkerOptions().position(userll).icon(usersBD).zIndex(9);
            usersMarker = (Marker) (mBaiduMap.addOverlay(ooA));
        }

        //用户定位附近随机位置
        for (int i = 0; i < 10; i++) {
            userll = new LatLng(mCurrentLat + Math.random() / 1000, mCurrentLon + Math.random() / 1000);
            MarkerOptions ooA = new MarkerOptions().position(userll).icon(usersBD).zIndex(9);
            usersMarker = (Marker) (mBaiduMap.addOverlay(ooA));
        }
    }


    private void showPeopleAroundYou() {
        btPeopleAroundYou = (Button) findViewById(R.id.buttonPeopleAroundYou);
        btPeopleAroundYou.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowUsers == false) {
                    initOverlay();
                    isShowUsers = true;
                    Toast.makeText(context, "已显示周围跑步用户", Toast.LENGTH_SHORT).show();
                } else {
                    clearOverlay(null);
                    isShowUsers = false;
                    Toast.makeText(context, "已隐藏周围跑步用户", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void myLocation() {
        btMyLocation = (Button) findViewById(R.id.dynMyLocation);
        btMyLocation.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng latLng = new LatLng(mCurrentLat, mCurrentLon);
                MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
                mBaiduMap.animateMapStatus(msu);
            }
        });
    }

    private void initView() {

        getpoint = (TextView) findViewById(R.id.getpoint);
        timer_tv_1 = (TextView) findViewById(R.id.timer);
        TextView_map_distance = (TextView) findViewById(R.id.TextView_map_distance);
        showcalorie = (TextView) findViewById(R.id.showcalorie);
        showSpeed = (TextView) findViewById(R.id.showSpeed);

        Button start = (Button) findViewById(R.id.buttonStart);
        Button finish = (Button) findViewById(R.id.buttonFinish);
        info = (TextView) findViewById(R.id.info);
        progressBarRl = (RelativeLayout) findViewById(R.id.progressBarRl);

        start.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mLocClient != null){
                    mLocClient.stop();//关闭定位
                }

                if (mLocClient != null && !mLocClient.isStarted()) {

                    isOpenDraw = true;

                    mLocClient.start();
                    progressBarRl.setVisibility(View.VISIBLE);
                    info.setText("GPS信号搜索中，请稍后...");
                    mBaiduMap.clear();
                    Toast.makeText(context, "准备开始运动...", Toast.LENGTH_LONG).show();
                }
            }
        });

        finish.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (mLocClient != null && mLocClient.isStarted()) {
                    mLocClient.stop();

                    progressBarRl.setVisibility(View.GONE);

                    Toast.makeText(context, "运动结束", Toast.LENGTH_SHORT).show();

                    endTime = System.currentTimeMillis();


////////////////////通过选择得到sportType,默认run-walk,ball;两个1是学号和密码
                    String json = RunDateUtil.returnAllDate("1", "1", sportsType, startTime, endTime,
                            distance, mCurrentLat, mCurrentLon);
//                    String json = RunDateUtil.returnAllDate("0", "1111","run",
//                            System.currentTimeMillis() + 3600 * 1000, System.currentTimeMillis() + 2*3600*1000,
//                            3499.0, 12.1, 121.1);
                    System.out.println(json);
                    String url="http://120.79.36.200:8080/SportServer_war/receiveRunData";//服务器接口地址
                    HttpUtil.sendOkHttpRequest(url, json, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(DynamicDemo.this, "发送数据失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(DynamicDemo.this, "发送数据成功", Toast.LENGTH_SHORT).show();
                                }
                            });
                            System.out.println(response.body().string());
                        }
                    });


                    if (isFirstLoc) {
                        points.clear();
                        last = new LatLng(mCurrentLat, mCurrentLon);
                        return;
                    }

                    MarkerOptions oFinish = new MarkerOptions();// 地图标记覆盖物参数配置类
                    oFinish.position(points.get(points.size() - 1));
                    oFinish.icon(finishBD);// 设置覆盖物图片
                    mBaiduMap.addOverlay(oFinish); // 在地图上添加此图层

                    //获取运动结束的位置
                    endPlace = points.get(points.size() - 1);

                    //复位
                    points.clear();
                    last = new LatLng(mCurrentLat, mCurrentLon);
                    isFirstLoc = true;

                    isOpenDraw = false;

                    endTime=System.currentTimeMillis();
                    //计时结束
                    stopTime();

                    mediaPlayer.reset(); // 停止播放
                    initMediaPlayer(R.raw.runmusic);//初始化MediaPlayer的结束运动资源
                    mediaPlayer.start();

                    //跳到分享页面
                    Intent intent = new Intent(DynamicDemo.this, ShareActivity.class);
                    //传递参数给ShareActivity
                    intent.putExtra("score_Share",score_Share);
                    //时间
                    intent.putExtra("time_hh",time_hh);
                    intent.putExtra("time_mm",time_mm);
                    intent.putExtra("time_ss",time_ss);
                    //距离
                    intent.putExtra("dis",dis);
                    //卡路里
                    intent.putExtra("caluli",caluli);
                    //配速
                    intent.putExtra("time_m",time_m);
                    intent.putExtra("time_s",time_s);
                    startActivity(intent);
                }
            }
        });

    }

    double lastX;

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        double x = sensorEvent.values[SensorManager.DATA_X];

        if (Math.abs(x - lastX) > 1.0) {
            mCurrentDirection = (int) x;

            if (isFirstLoc) {
                lastX = x;
                return;
            }

            locData = new MyLocationData.Builder().accuracy(0)
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(mCurrentLat).longitude(mCurrentLon).build();
            mBaiduMap.setMyLocationData(locData);
        }
        lastX = x;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(final BDLocation location) {

            if (location == null || mMapView == null) {
                return;
            }

            //初次定位可接受室内信号
            LatLng firstLL = new LatLng(location.getLatitude(), location.getLongitude());
            //显示当前定位点，缩放地图
            locateAndZoom(location, firstLL);

            if (isOpenDraw == true){
                //注意这里只接受gps点，需要在室外定位。
                if (location.getLocType() == BDLocation.TypeGpsLocation) {

                    info.setText("GPS信号弱，请稍后...");

                    if (isFirstLoc) {//首次定位
                        //第一个点很重要，决定了轨迹的效果，gps刚开始返回的一些点精度不高，尽量选一个精度相对较高的起始点
                        LatLng ll = null;

                        ll = getMostAccuracyLocation(location);
                        if (ll == null) {
                            return;
                        }
                        isFirstLoc = false;
                        points.add(ll);//加入集合
                        last = ll;

                        //显示当前定位点，缩放地图
                        locateAndZoom(location, ll);

                        //标记起点图层位置
                        MarkerOptions oStart = new MarkerOptions();// 地图标记覆盖物参数配置类
                        oStart.position(points.get(0));// 覆盖物位置点，第一个点为起点
                        oStart.icon(startBD);// 设置覆盖物图片
                        mBaiduMap.addOverlay(oStart); // 在地图上添加此图层

                        startPlace = points.get(0);

                        progressBarRl.setVisibility(View.GONE);

                        //开始计时
                        startTime();
                        startTime = System.currentTimeMillis();//程序开始记录时间

                        if (!mediaPlayer.isPlaying()) {
                            initMediaPlayer(R.raw.runmusic);//初始化MediaPlayer的开始运动资源
                            mediaPlayer.start(); // 开始播放
                        }

                        return;//画轨迹最少得2个点，首地定位到这里就可以返回了
                    }


                    //从第二个点开始
                    LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                    //sdk回调gps位置的频率是1秒1个，位置点太近动态画在图上不是很明显，可以设置点之间距离大于为5米才添加到集合中
                    if (DistanceUtil.getDistance(last, ll) < 5) {
                        return;
                    }
                    points.add(ll);//如果要运动完成后画整个轨迹，位置点都在这个集合中
                    last = ll;

                    //显示当前定位点，缩放地图
                    locateAndZoom(location, ll);

                    //清除上一次轨迹，避免重叠绘画
                    mMapView.getMap().clear();

                    //起始点图层也会被清除，重新绘画
                    MarkerOptions oStart = new MarkerOptions();
                    oStart.position(points.get(0));
                    oStart.icon(startBD);
                    mBaiduMap.addOverlay(oStart);
                    //将points集合中的点绘制轨迹线条图层，显示在地图上
                    OverlayOptions ooPolyline = new PolylineOptions().width(13).color(0xAAFF0000).points(points);
                    mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);
                }
            }
        }

    }

    private void locateAndZoom(final BDLocation location, LatLng ll) {
        mCurrentLat = location.getLatitude();
        mCurrentLon = location.getLongitude();
        locData = new MyLocationData.Builder().accuracy(0)
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(mCurrentDirection).latitude(location.getLatitude())
                .longitude(location.getLongitude()).build();
        mBaiduMap.setMyLocationData(locData);

        builder = new MapStatus.Builder();
        builder.target(ll).zoom(mCurrentZoom);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

    /**
     * 首次定位很重要，选一个精度相对较高的起始点
     * 注意：如果一直显示gps信号弱，说明过滤的标准过高了，
     * 你可以将location.getRadius()>25中的过滤半径调大，比如>40，
     * 并且将连续5个点之间的距离DistanceUtil.getDistance(last, ll ) > 5也调大一点，比如>10，
     * 这里不是固定死的，你可以根据你的需求调整，如果你的轨迹刚开始效果不是很好，你可以将半径调小，两点之间距离也调小，
     * gps的精度半径一般是10-50米
     */
    private LatLng getMostAccuracyLocation(BDLocation location) {

        if (location.getRadius() > 40) {//gps位置精度大于40米的点直接弃用
            return null;
        }

        LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());

        if (DistanceUtil.getDistance(last, ll) > 10) {
            last = ll;
            points.clear();//有任意连续两点位置大于10，重新取点
            return null;
        }
        points.add(ll);
        last = ll;
        if (isOpenDraw == false) {
            //有5个连续的点之间的距离小于10，认为gps已稳定，以最新的点为起始点
            if (points.size() >= 2) {
                points.clear();
                return ll;
            }
        } else {
            //有5个连续的点之间的距离小于10，认为gps已稳定，以最新的点为起始点
            if (points.size() >= 3) {
                points.clear();
                return ll;
            }
        }
        return null;
    }

    @Override
    protected void onPause() {
        // MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        // 为系统的方向传感器注册监听器
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);
        // MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
        mMapView.onResume();
        super.onResume();

    }

    @Override
    protected void onStop() {
        // 取消注册传感器监听
        mSensorManager.unregisterListener(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        mLocClient.unRegisterLocationListener(myListener);
        if (mLocClient != null && mLocClient.isStarted()) {
            mLocClient.stop();
        }
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.getMap().clear();
        mMapView.onDestroy();
        mMapView = null;
        startBD.recycle();
        finishBD.recycle();
        super.onDestroy();
        // 回收 bitmap 资源
        usersBD.recycle();
    }

    /**
     * 清除所有Overlay
     *
     * @param view
     */
    public void clearOverlay(View view) {
        mBaiduMap.clear();
        usersMarker = null;
    }

    /**
     * 重新添加Overlay
     *
     * @param view
     */
    public void resetOverlay(View view) {
        clearOverlay(null);
        initOverlay();
    }

    private class SeekBarListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            float alpha = ((float) seekBar.getProgress()) / 10;
            if (usersMarker != null) {
                usersMarker.setAlpha(alpha);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initMediaPlayer(R.raw.runmusic);//初始化MediaPlayer的开始运动资源
                } else {
                    Toast.makeText(DynamicDemo.this, "拒绝权限将无法使用程序", Toast.LENGTH_SHORT).show();
                    DynamicDemo.this.finish();
                }
            default:
                break;
        }
    }

    private void getPermission() {
        //权限申请
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(DynamicDemo.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(DynamicDemo.this,
                Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(DynamicDemo.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(DynamicDemo.this, permissions, 1);
        } else {
            mLocClient.start();
        }
    }

    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            time_hh = new DecimalFormat("00").format(msg.arg1 / 3600);
            time_mm = new DecimalFormat("00").format(msg.arg1 % 3600 / 60);
            time_ss = new DecimalFormat("00").format(msg.arg1 % 60);
            timer_tv_1.setText(time_hh + ":" + time_mm + ":" + time_ss);
            double dis1 = (double)((msg.arg2 / 10000));
            dis = dis1 + "";
            TextView_map_distance.setText(dis + " m");
            caluli = Double.parseDouble(df.format(72 * dis1/1000)) + "";
            showcalorie.setText(caluli + " Cal");
            double timePerKM1 = 0.0;
            timePerKM = timePerKM1+"";
            if (dis1 != 0){
                timePerKM1 = Double.parseDouble(df.format((double)1000*msg.arg1 / dis1));
                time_m = new DecimalFormat("00").format(timePerKM1 / 60);
                time_s = new DecimalFormat("00").format(timePerKM1 % 60);
                showSpeed.setText(time_m + ":" + time_s);
            } else {
                showSpeed.setText("0" + ":" + "0");
            }
            score_Share = (int)(dis1/1000) * 2 + "";
            getpoint.setText(score_Share + "");
            startTime();
        };
    };
    /**
     * 开始自动减时
     */
    private void startTime() {
        if(timer==null){
            timer = new Timer();
        }
        timerTask = new TimerTask() {
            @Override
            public void run() {
                i++;//自动加1
                endPlace = new LatLng(mCurrentLat, mCurrentLon);
                distance = DistanceUtil.getDistance(startPlace, endPlace);
                Message message = Message.obtain();
                message.arg1 = i;
                message.arg2 = (int)(distance * 10000);
                mHandler.sendMessage(message);//发送消息
            }
        };
        timer.schedule(timerTask, 1000);//1000ms执行一次
    }
    /**
     * 停止自动减时
     */
    private void stopTime() {
        if(timer!=null)
            timer.cancel();
    }

    //音乐音效类
    private void initMediaPlayer(int res) {
        try {
            mediaPlayer = MediaPlayer.create(DynamicDemo.this, res);
            mediaPlayer.prepare(); // 让MediaPlayer进入到准备状态
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}