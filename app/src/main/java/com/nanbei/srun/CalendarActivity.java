package com.nanbei.srun;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;

public class CalendarActivity extends Activity{

    Bundle dateBundle;

    public int cyear;
    public int cmonth;
    public int cdayOfMonth1;
    private Button btConfirmDate;
    private CalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        calendarView = (CalendarView)findViewById(R.id.calendarview);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                cyear = year;
                cmonth = month + 1;//获得的月份month为实际month-1
                cdayOfMonth1 = dayOfMonth;//日
            }
        });

        btConfirmDate = (Button) findViewById(R.id.buttonConfirmDate);
        btConfirmDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //回传数据
                Intent data = new Intent();
                data.putExtra("year", cyear);
                data.putExtra("month", cmonth);
                data.putExtra("day", cdayOfMonth1);
                setResult(2, data);
                finish();
                //Log.i("tag", "CalendarActivity：" + cyear + ":" + cmonth + ":" + cdayOfMonth1);
            }
        });
    }
}
