package com.nanbei.srun;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by john on 2018/6/1.
 */

public class MyspaceFragment extends Fragment {

    private Button btn_login;
    private Button btn_register;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myspace, container, false);
        btn_login = (Button)view.findViewById(R.id.Btn_Login);
        btn_register = (Button)view.findViewById(R.id.Btn_Regist);
        btn_login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),LoginActivity.class);
                startActivity(intent);
            }
        });
        btn_register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),RegistActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

}
