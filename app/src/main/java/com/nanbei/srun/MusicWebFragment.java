package com.nanbei.srun;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.*;

public class MusicWebFragment extends Fragment {
    private static WebView webView;
    private String url = "http://music.baidu.com/songs/new";
    private ProgressDialog dialog;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_music_web, container, false);

        initWeb();
        return view;
    }

    private void initWeb(){
        webView = (WebView) view.findViewById(R.id.musicWeb);
        //WebView加载web资源
        webView.loadUrl(url);

        // 覆盖WebView默认通过第三方或者是系统浏览器打开网页的行为，使得网页可以在WebVIew中打开
        //WebViewClient帮助WebView去处理一些页面控制和请求通知
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //返回值是true的时候控制网页在WebView中去打开，如果为false调用系统浏览器或第三方浏览器去打开
                view.loadUrl(url);
                return true;
            }
        });
        //启用支持JavaScript
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        //WebView加载页面优先使用缓存加载
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                //newProgress 1-100之间的整数
//                if(newProgress==100) {
//                    //网页加载完毕，关闭ProgressDialog
//                    closeDialog();
//                } else {
//                    //网页正在加载,打开ProgressDialog
//                    openDialog(newProgress);
//                }
            }
            private void closeDialog() {
                if(dialog!=null&&dialog.isShowing()) {
                    dialog.dismiss();
                    dialog=null;
                }
            }
            private void openDialog(int newProgress) {
                if(dialog==null) {
                    dialog=new ProgressDialog(getActivity());
                    dialog.setTitle("正在加载");
                    dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    dialog.setProgress(newProgress);
                    dialog.show();
                } else {
                    dialog.setProgress(newProgress);
                }
            }
        });
    }


    //改写物理按键——返回的逻辑
    public static boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            if(webView.canGoBack()) {
                webView.goBack();//返回上一页面
                return true;
            } else {
                System.exit(0);//退出程序
            }
        }
        return true;
    }
}
