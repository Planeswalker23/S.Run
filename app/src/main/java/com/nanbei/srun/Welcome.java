package com.nanbei.srun;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

public class Welcome extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_welcome);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent it = new Intent();
                it.setClass(Welcome.this,MainActivity.class);
                startActivity(it);
                finish();
            }
        },2*1000);
    }

}
