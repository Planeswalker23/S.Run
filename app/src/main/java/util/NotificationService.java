package util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.util.Log;
import com.nanbei.srun.MainActivity;
import com.nanbei.srun.R;

public class NotificationService extends JobService{

    NotificationManager manager;//通知控制类
    int notification_ID;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("tagonstartcommand", "onStartCommand");
        return START_STICKY;
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.i("TagNotification", "定时推送消息onStartJob:" + jobParameters);

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
        System.out.println("发送通知");
        manager.notify(notification_ID, notification);//发送通知到通知栏

//        jobFinished(jobParameters, true);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.i("TagNotification", "定时推送消息onStopJob:" + jobParameters);
        return false;
    }
}
