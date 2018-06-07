package com.nanbei.srun;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

import entity.LocalUser;

/**
 * Created by john on 2018/6/1.
 */

public class MyspaceInFragment extends Fragment {
    private Button btn;
    private LocalUserManager localUserManager;
    private LocalUser localUser;
    private String idvalue;
    private String schoolvalue;
    private String passwordvalue;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inmyspace, container, false);

        idvalue = (String)getArguments().get("idvalue");
        schoolvalue = (String)getArguments().get("schoolvalue");
        passwordvalue = (String)getArguments().get("passwordvalue");

        ((TextView) view.findViewById(R.id.showSchool)).setText(schoolvalue);
        ((TextView) view.findViewById(R.id.showstudentid)).setText(idvalue);

//        ((TextView) view.findViewById(R.id.showUser)).setText("您好，"+" "+schoolvalue+" "+idvalue);

        btn = (Button)view.findViewById(R.id.Btn_Logout);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //删除sqlite的账号，退出登录状态
                localUserManager = new LocalUserManager(new LocalUserSQLiteHelper(getActivity()));
                localUserManager.deleteAll();
                Intent intent = new Intent(getActivity(),MainActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

}
