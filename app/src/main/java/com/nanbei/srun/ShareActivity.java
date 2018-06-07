package com.nanbei.srun;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

/**
 * Created by john on 2018/6/3.
 */

public class ShareActivity  extends Activity {

    private static Context context;
    Button btSharePicture;
    Button btReturnHome;

//    private long startTime;
//    private long endTime;
//    private double distance;

    TextView tvStartTime;
    TextView tvFinishTime;
    TextView tvDistance;
    ImageView imSharePic;
    LinearLayout shareLayout;

    private static final int WRITE_PERMISSION = 0x01;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        context = context;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
//        shareLayout = (LinearLayout)findViewById(R.id.shareAll);


/////////随机生成一个share背景图
        Random rand = new Random();
        int rr = rand.nextInt(4);
        LinearLayout temp=(LinearLayout)findViewById(R.id.shareAll);
        if(rr == 1) {
            temp.setBackgroundResource(R.drawable.pic1);
        }else if(rr == 2){
            temp.setBackgroundResource(R.drawable.pic2);
        }else if(rr == 3){
            temp.setBackgroundResource(R.drawable.pic3);
        }else if(rr == 4){
            temp.setBackgroundResource(R.drawable.pic4);
        }
//        view.setImageResource(R.drawable.pic3);
//        Drawable d=Drawable.createFromPath(String.valueOf(R.drawable.pic1));
//        temp.setBackgroundDrawable(share1);


        tvStartTime = (TextView) findViewById(R.id.tvStartTime);
        tvFinishTime = (TextView) findViewById(R.id.tvFinishTime);
        tvDistance = (TextView) findViewById(R.id.tvDistance);
//        imSharePic.setImageResource(R.drawable.pic3);
//        imSharePic.setScaleType(ImageView.ScaleType.FIT_XY);

        Intent intent = getIntent();
        tvStartTime.setText("开始运动时间："+intent.getLongExtra("startTime",0));
        tvFinishTime.setText("结束运动时间："+intent.getLongExtra("endTime",0));
        tvDistance.setText("运动距离："+intent.getDoubleExtra("distance",0.0f));


        btReturnHome = (Button) findViewById(R.id.buttonReturnHome);
        btReturnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShareActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btSharePicture = (Button) findViewById(R.id.buttonSharePicture);
        btSharePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Bitmap bitmap = getBitmap(findViewById(R.id.shareAll));
                    //sd卡路径
                    requestWritePermission();
                    String sdCardPath = Environment.getExternalStorageDirectory().getPath();
                    FileOutputStream fout = new FileOutputStream(sdCardPath + File.separator + "part.png");
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fout);
                    System.out.println("xxxxx" + "截图成功");

                    //从本地把截图分享
                    File file = null;
                    String path = Environment.getExternalStorageDirectory().getPath() + File.separator;
                    file = new File(path + "part.png");
                    sharePictrue(file);


                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    view.setDrawingCacheEnabled(false);
                }
            }
        });

    }

    private Bitmap getBitmap(View view) throws Exception {

        View screenView = getWindow().getDecorView();
        screenView.setDrawingCacheEnabled(true);
        screenView.buildDrawingCache();

        //获取屏幕整张图片
        Bitmap bitmap = screenView.getDrawingCache();
        if (bitmap != null) {
            //需要截取的长和宽
            int outWidth = view.getWidth();
            int outHeight = view.getHeight();
            //获取需要截图部分的在屏幕上的坐标(view的左上角坐标）
            int[] viewLocationArray = new int[2];
            view.getLocationOnScreen(viewLocationArray);
            //从屏幕整张图片中截取指定区域
            bitmap = Bitmap.createBitmap(bitmap, viewLocationArray[0], viewLocationArray[1], outWidth, outHeight);
        }
        return bitmap;
    }

    private void requestWritePermission(){
        if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},WRITE_PERMISSION);
        }
    }

    private void sharePictrue(File file){
        Intent shareIntent = new Intent();
        //解决android.os.FileUriExposedException问题

        //由文件得到uri
        Uri imageUri;
        // 判断版本大于等于7.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // "net.csdn.blog.ruancoder.fileprovider"即是在清单文件中配置的authorities
            imageUri = FileProvider.getUriForFile(context, "net.csdn.blog.ruancoder.fileprovider", file);
            // 给目标应用一个临时授权
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            imageUri = Uri.fromFile(file);
        }
        shareIntent.setDataAndType(imageUri, "application/vnd.android.package-archive");
        //通过微信分享
        //ComponentName comp = new ComponentName("[图片]com.tencent.mm","[图片]com.tencent.mm.ui.tools.ShareToTimeLineUI");
        //shareIntent.setComponent(comp);
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setType("image/*");
        startActivity(Intent.createChooser(shareIntent, "分享到"));
    }


}
