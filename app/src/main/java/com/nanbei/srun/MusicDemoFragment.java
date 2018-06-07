package com.nanbei.srun;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class MusicDemoFragment extends Fragment {

    private MediaPlayer mediaPlayer = new MediaPlayer();
    private Button btstartmusictest;
    private Button btendmusictest;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_musicdemo, container, false);

        btstartmusictest = (Button) view.findViewById(R.id.buttonstartmusictest);
        btstartmusictest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mediaPlayer.isPlaying()) {
                    initMediaPlayer(R.raw.startmusic);//初始化MediaPlayer的开始运动资源
                    mediaPlayer.start(); // 开始播放
                    Toast.makeText(getActivity(), "开始运动", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btendmusictest = (Button) view.findViewById(R.id.buttonendmusictest);
        btendmusictest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.reset(); // 停止播放
                initMediaPlayer(R.raw.endmusic);//初始化MediaPlayer的结束运动资源
                mediaPlayer.start();
                Toast.makeText(getActivity(), "停止运动", Toast.LENGTH_SHORT).show();
            }
        });

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.
                WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }else{
            initMediaPlayer(R.raw.startmusic);//初始化MediaPlayer的开始运动资源
        }


        return view;
    }


    private void initMediaPlayer(int res) {
        try {
            mediaPlayer = MediaPlayer.create(getActivity(), res);
            mediaPlayer.prepare(); // 让MediaPlayer进入到准备状态
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initMediaPlayer(R.raw.startmusic);//初始化MediaPlayer的开始运动资源
                } else {
                    Toast.makeText(getActivity(), "拒绝权限将无法使用程序", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
                break;
            default:
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }


}
