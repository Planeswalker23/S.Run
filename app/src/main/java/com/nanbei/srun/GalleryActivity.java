package com.nanbei.srun;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.*;
import android.widget.ImageView.ScaleType;

public class GalleryActivity extends Activity  implements AdapterView.OnItemSelectedListener,ViewSwitcher.ViewFactory {

    // 准备数据源
    private int[] res = { R.drawable.pic1, R.drawable.pic2,
            R.drawable.pic3, R.drawable.pic4};
    private ImageAdapter adapter;
    private Gallery gallery;
    private ImageSwitcher is;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        gallery = (Gallery) findViewById(R.id.gallery);
        is=(ImageSwitcher) findViewById(R.id.is);
        // gallery加载适配器
        adapter = new ImageAdapter(res, this);
        gallery.setAdapter(adapter);
        gallery.setOnItemSelectedListener(this);
        is.setFactory(this);
        is.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        is.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //image.setBackgroundResource(res[position%res.length]);
        is.setBackgroundResource(res[position%res.length]);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public View makeView() {
        ImageView image=new ImageView(this);
        image.setScaleType(ScaleType.FIT_CENTER);
        return image;
    }
}
