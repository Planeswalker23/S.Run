package com.nanbei.srun;

import android.app.Activity;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class NotificationDemo extends Activity{
    private Button btn_send;
    private Button btn_cancle;
    NotificationManager manager;//通知控制类
    int notification_ID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        btn_send = (Button) findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                sendNotification();
            }
        });
        btn_cancle = (Button) findViewById(R.id.btn_cancle);
        btn_cancle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                manager.cancel(notification_ID);//取消已发送的通知
            }
        });
    }
    /**
     * 构造notification并发送到通知栏
     */
    private void sendNotification(){
        Intent intent = new Intent(this,MainActivity.class);//设置意图
        PendingIntent pintent = PendingIntent.getActivity(this, 0, intent, 0);
        Builder builder = new Builder(this);
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

