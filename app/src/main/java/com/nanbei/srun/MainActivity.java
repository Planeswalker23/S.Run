package com.nanbei.srun;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;

import entity.LocalUser;
import util.NotificationService;

public class MainActivity extends AppCompatActivity {

    private Fragment fg;

    private boolean islogin;
    private LocalUserManager localUserManager;
    private LocalUser localUser;

    private int cyear;
    private int cmonth;
    private int cday;

    Bundle userBundle;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {


        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            FragmentTransaction transaction = getFragmentManager().
                    beginTransaction();

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    HomeFragment homeFragment = new HomeFragment();
                    fg = homeFragment;

                    homeFragment.setArguments(userBundle);

                    transaction.replace(R.id.content, homeFragment);
                    transaction.commit();
                    return true;
                case R.id.navigation_music:
                    MusicWebFragment musicWebFragment = new MusicWebFragment();
                    fg = musicWebFragment;

                    transaction.replace(R.id.content, musicWebFragment);
                    transaction.commit();
                    return true;
                case R.id.navigation_mall:
                    MallFragment mallFragment = new MallFragment();
                    fg = mallFragment;

                    mallFragment.setArguments(userBundle);
                    transaction.replace(R.id.content, mallFragment);
                    transaction.commit();
                    return true;
                case R.id.navigation_my:

                    if (islogin) {
                        MyspaceInFragment myspaceInFragment = new MyspaceInFragment();
                        fg = myspaceInFragment;

                        myspaceInFragment.setArguments(userBundle);

                        transaction.replace(R.id.content,myspaceInFragment);

                    } else{
                        MyspaceFragment myspaceFragment = new MyspaceFragment();
                        fg = myspaceFragment;

                        transaction.replace(R.id.content,myspaceFragment);


//                    Bundle args1 = new Bundle();
//                    args1.putString("idvalue",idvalue);
//                    System.out.print("id"+idvalue);
                    }

                    transaction.commit();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(MainActivity.this,new String[]{"android.permission.INTERNET"},1);


        //#表示sqlite是空的，没有已经登陆的账号
        localUserManager = new LocalUserManager(new LocalUserSQLiteHelper(this));
        localUser = localUserManager.queryAll();
        islogin = localUser.getSchool() != "#" || localUser.getId() != "#";

//        idvalue = getIntent().getStringExtra("idvalue");
        //从mainActivity传递账号信息参数到各个fragment
        userBundle = new Bundle();
        userBundle.putString("idvalue",localUser.getId());
        userBundle.putString("schoolvalue",localUser.getSchool());
        userBundle.putString("passwordvalue",localUser.getPassword());
        //System.out.print("lll::"+localUser.getId());


        //默认加载home_fragment,要和上面保持一致
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        HomeFragment homeFragment = new HomeFragment();
        fg = homeFragment;
        homeFragment.setArguments(userBundle);
        transaction.replace(R.id.content, homeFragment);
        transaction.commit();


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_home);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("ActionBar", "OnKey事件");
        if(fg instanceof MusicWebFragment){
            MusicWebFragment.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

}
