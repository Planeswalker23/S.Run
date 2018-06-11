package com.nanbei.srun;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import com.google.gson.*;
import com.prolificinteractive.materialcalendarview.*;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import util.HttpUtil;
import util.MyRunDateDecorator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

public class CalendarActivity extends Activity implements OnDateSelectedListener {

    private Button btBackToHeom;
    private MaterialCalendarView widget;
    private Handler handler;

    NotificationManager manager;//通知控制类
    int notification_ID = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        btBackToHeom = (Button) findViewById(R.id.buttonBackToHome);
        btBackToHeom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendNotification();
                finish();
            }
        });

        widget = (MaterialCalendarView) findViewById(R.id.calendarView);
        //编辑日历属性
        widget.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)   //设置每周开始的第一天
                .setCalendarDisplayMode(CalendarMode.MONTHS)//设置显示模式，可以显示月的模式，也可以显示周的模式
                .commit();// 返回对象并保存
        widget.setSelectedDate(new Date());
        widget.setOnDateChangedListener(this);

        //为dates中添加日期
        AddMyRunDateToCollection("2");
        //通过handler得到msg中的dates数据
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 0:
                        Collection<CalendarDay> dates = (Collection<CalendarDay>)msg.obj;
                        //添加装饰标记
                        widget.addDecorators(new MyRunDateDecorator(CalendarActivity.this.getResources().getDrawable(R.drawable.my_selector), dates));
                        break;
                }
            }
        };
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

        int year = widget.getSelectedDate().getYear();
        int month = widget.getSelectedDate().getMonth() + 1;
        int day = widget.getSelectedDate().getDay();
        //回传数据
        Intent data = new Intent();
        data.putExtra("year", year);
        data.putExtra("month", month);//获得的月份month为实际month-1
        data.putExtra("day", day);
        setResult(2, data);
        finish();
    }


    public void AddMyRunDateToCollection(String id) {

        String url = "http://120.79.36.200:8080/SportServer_war/getCalendarData?id=" + id;
        HttpUtil.sendOkHttpRequest(url, "", new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("日历数据请求失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Collection<CalendarDay> dates = new ArrayList<>();
                System.out.println("日历数据请求成功");
                //解析返回值
                String backcode = response.body().string();
                //Json的解析类对象
                JsonParser parser = new JsonParser();
                //将JSON的String 转成一个JsonArray对象
                JsonArray jsonArray = parser.parse(backcode).getAsJsonArray();
                for (JsonElement json : jsonArray) {
//                    System.out.println("element" + json.toString());
                    // 获得 根节点 的实际 节点类型
                    JsonObject element = json.getAsJsonObject();
                    JsonPrimitive yearJson = element.getAsJsonPrimitive("year");
                    int year = yearJson.getAsInt();
                    JsonPrimitive monthJson = element.getAsJsonPrimitive("month");
                    int month = monthJson.getAsInt() - 1;
                    JsonPrimitive dayJson = element.getAsJsonPrimitive("day");
                    int day = dayJson.getAsInt();
                    System.out.println("解析之后的年月日：" + year + month + day);
                    dates.add(new CalendarDay(year, month, day));
                    Message message = Message.obtain();
                    message.what = 0;
                    message.obj = dates;
                    handler.sendMessage(message);
                }
            }
        });
    }

    /**
     * 构造notification并发送到通知栏
     */
    private void sendNotification(){
        Intent intent = new Intent(this,MainActivity.class);//设置意图
        PendingIntent pintent = PendingIntent.getActivity(this, 0, intent, 0);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);//设置图标
        builder.setTicker("COMEONBRO");//手机状态栏的提示；
        builder.setWhen(System.currentTimeMillis());//设置时间
        builder.setContentTitle("Sports");//设置标题
        builder.setContentText("快点回来打卡继续坚持吧！");//设置通知内容
        builder.setContentIntent(pintent);//点击后的意图，这里是回到app
//		builder.setDefaults(Notification.DEFAULT_SOUND);//设置提示声音
//		builder.setDefaults(Notification.DEFAULT_LIGHTS);//设置指示灯
//		builder.setDefaults(Notification.DEFAULT_VIBRATE);//设置震动
        builder.setDefaults(Notification.DEFAULT_ALL);//设置指示灯、声音、震动
        builder.setAutoCancel(true);//设置被点击后自动清除
        Notification notification = builder.build();//4.1以上
        //builder.getNotification();
        manager.notify(notification_ID, notification);//发送通知到通知栏
    }
}
