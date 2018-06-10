package com.nanbei.srun;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import entity.LocalUser;

/**
 * Created by john on 2018/6/1.
 */

public class MyspaceInFragment extends Fragment {
    private Button btn;
    private ImageButton btn_jinjilianxiren, btn_shouhuodizhi, btn_history_bill;
    private Button btnMedal;
    private LocalUserManager localUserManager;
    private LocalUser localUser;
    private String idvalue;
    private String schoolvalue;
    private String passwordvalue,usernamevalue;

    private TextView tv_Emergency_Contact, tv_ShAddress;


    private CreateUserDialog createUserDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inmyspace, container, false);

        idvalue = (String)getArguments().get("idvalue");
        schoolvalue = (String)getArguments().get("schoolvalue");
        passwordvalue = (String)getArguments().get("passwordvalue");
        usernamevalue = (String)getArguments().get("usernamevalue");

        ((TextView) view.findViewById(R.id.showSchool)).setText(schoolvalue);
        ((TextView) view.findViewById(R.id.showstudentid)).setText(idvalue);
        ((TextView)view.findViewById(R.id.showUser)).setText(usernamevalue);

        tv_Emergency_Contact = (TextView) view.findViewById(R.id.Emergency_Contact);
        tv_ShAddress = (TextView) view.findViewById(R.id.ShAddress);

        btn_history_bill = (ImageButton) view.findViewById(R.id.ImageButton_History_bill);
        btn_history_bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),HistoryBillAcitvity.class);
                intent.putExtra("idvalue",idvalue);
                startActivity(intent);
            }

        });

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

        btnMedal=  (Button)view.findViewById(R.id.showmedal);
        btnMedal.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),MedalActivity.class);
                startActivity(intent);
            }
        });

        btn_jinjilianxiren = (ImageButton) view.findViewById(R.id.btn_jinjilianxiren);
        btn_jinjilianxiren.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog(1,v);
            }
        });

        btn_shouhuodizhi = (ImageButton) view.findViewById(R.id.btn_shouhuodizhi);
        btn_shouhuodizhi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog(2,v);
            }
        });

        return view;
    }

    public void showEditDialog(int flag, View view) {
        if (flag == 1) {
            createUserDialog = new CreateUserDialog(flag, getActivity(), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String Emergency_Contact = createUserDialog.text_name.getText().toString().trim();
                    System.out.println(Emergency_Contact);
                    tv_Emergency_Contact.setText("紧急联系人  " + Emergency_Contact);
                    createUserDialog.hide();
                }
            });
        } else {
            createUserDialog = new CreateUserDialog(flag, getActivity(), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String ShAddress = createUserDialog.text_name.getText().toString().trim();
                    System.out.println(ShAddress);
                    tv_ShAddress.setText("收获地址  " + ShAddress);
                    createUserDialog.hide();
                }
            });
        }
        createUserDialog.show();
    }

}
