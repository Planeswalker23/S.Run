package com.nanbei.srun;

import android.app.Activity;
import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Choreographer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;


import com.baidu.location.*;
import com.baidu.mapapi.map.*;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by john on 2018/5/22.
 */

public class MapActivity extends Activity {
    //计时相关
    private long startTime;
    private long endTime;
    private long TotalTime;

    private Context context;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private CheckBox cbHeatMap;
    private Button btMyLocation;
    private Button btStartRun;
    private Button btEndRun;
    private TextView tvDistance;
    private TextView tvTime;
    private Chronometer timer;

    //定位相关
    public LocationClient mLocationClient;
    private double mCurrentLat = 0.0;//纬度
    private double mCurrentLon = 0.0;//经度
    private boolean isFirstLocate = true;
    private float mCurrentZoom = 16.0f;//默认地图缩放比例值
    private double distance = 0.0;//运动距离
    private LatLng startPlace;
    private LatLng endPlace;



    //封装设备当前所在位置
    private MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
    private BitmapDescriptor mCurrentMarker =
            BitmapDescriptorFactory .fromResource(R.drawable.start);//自定义图标
    private BitmapDescriptor startBD = BitmapDescriptorFactory.fromResource(R.drawable.start);//起点图标
    private BitmapDescriptor finishBD = BitmapDescriptorFactory.fromResource(R.drawable.end);//终点图标

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLocation();//初始化定位信息
        getPermission();//获取权限
        setContentView(R.layout.activity_map);
        findViewById();//获取控件对象
        initMap();//获取地图控件引用
        heatMap();//开启/关闭城市热力图
        moveToMyLocation();//回到我的位置
        startRun();//开始运动，记录起点坐标
        endRun();//结束运动
    }



    private void endRun() {
        btEndRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endPlace = new LatLng(mCurrentLat, mCurrentLon);
                distance = DistanceUtil.getDistance(startPlace, endPlace);
                String resultdistance = String.format("%.2f", distance / 1000).toString();
                tvDistance.setText("运动总距离 " + resultdistance  + " km ");

                endTime = System.currentTimeMillis();
                TotalTime = endTime - startTime;
                tvTime.setText("运动总时间 ");// + TotalTime + " ms");

                timer.stop();
            }
        });
    }

    private void startRun() {
        btStartRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPlace = new LatLng(mCurrentLat, mCurrentLon);

                startTime = System.currentTimeMillis();//程序开始记录时间
                timer.setBase(SystemClock.elapsedRealtime());//计时器清零
                int hour = (int) ((SystemClock.elapsedRealtime() - timer.getBase()) / 1000 / 60);
                timer.setFormat("0"+String.valueOf(hour)+":%s");
                timer.start();
            }
        });
    }

    private void moveToMyLocation() {
        btMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng latLng = new LatLng(mCurrentLat, mCurrentLon);
                MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
                mBaiduMap.animateMapStatus(msu);
                msu = MapStatusUpdateFactory.zoomTo(19.0f);
                mBaiduMap.animateMapStatus(msu);
            }
        });
    }

    //初次运动时移动到我的位置
    private void navigeteTo(BDLocation location){
        if (isFirstLocate) {
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
            mBaiduMap.animateMapStatus(update);
            isFirstLocate = false;
        }
        //实时改变位置-构造定位数据
        locationBuilder.latitude(mCurrentLat);
        locationBuilder.longitude(mCurrentLon);
        locationBuilder.accuracy(0);
        MyLocationData locationData = locationBuilder.build();//设置定位数据
        mBaiduMap.setMyLocationData(locationData);
        // 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
        MyLocationConfiguration config = new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.NORMAL, true, mCurrentMarker);
        mBaiduMap.setMyLocationConfiguration(config);
    }

    private void initLocation() {
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListenner());//注册监听器
        LocationClientOption option = new LocationClientOption();
        //设置定位模式为GPS模式
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    public class MyLocationListenner implements BDLocationListener{
        @Override
        public void onReceiveLocation(final BDLocation location) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mCurrentLat = location.getLatitude();
                    mCurrentLon = location.getLongitude();

                }
            });
            navigeteTo(location);
        }
    }

    private void heatMap() {
        cbHeatMap = (CheckBox) findViewById(R.id.heatMap);
        cbHeatMap.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    mBaiduMap.setBaiduHeatMapEnabled(true);//开启城市热力图
                } else {
                    mBaiduMap.setBaiduHeatMapEnabled(false);//关闭城市热力图
                }
            }
        });
    }

    private void initMap() {
        mMapView.removeViewAt(1);//隐藏百度logo
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);//地图类型为普通地图
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(mCurrentZoom);
        mBaiduMap.setMapStatus(msu);
        mBaiduMap.setMyLocationEnabled(true);
    }

    private void findViewById() {
        mMapView = (MapView) findViewById(R.id.bmapView);
        btMyLocation = (Button) findViewById(R.id.myLocation);
        cbHeatMap = (CheckBox) findViewById(R.id.heatMap);
        this.context = this;
        btStartRun = (Button) findViewById(R.id.startRun);
        btEndRun = (Button) findViewById(R.id.endRun);
        tvDistance = (TextView) findViewById(R.id.tvdistance);
        tvTime = (TextView) findViewById(R.id.tvtime);
        timer = (Chronometer) findViewById(R.id.timer);
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
            default:
                break;
        }
    }

    private void getPermission() {
        //权限申请
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MapActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MapActivity.this,
                Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(MapActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MapActivity.this, permissions, 1);
        } else {
            mLocationClient.start();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();//销毁地图
        mLocationClient.stop();//停止定位
        mBaiduMap.setMyLocationEnabled(false);
        startBD.recycle();//释放资源
        finishBD.recycle();
        mCurrentMarker.recycle();
    }
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }
}
