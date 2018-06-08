package com.nanbei.srun;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by john on 2018/6/7.
 */

public class MedalActivity  extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("medal");
        setContentView(R.layout.activity_medal);
    }
}
