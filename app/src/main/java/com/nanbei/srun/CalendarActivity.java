package com.nanbei.srun;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CalendarView;

public class CalendarActivity extends Activity{


    private CalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        calendarView = (CalendarView)findViewById(R.id.calendarview);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                //回传数据
                Intent data = new Intent();
                data.putExtra("year", year);
                data.putExtra("month", month + 1);//获得的月份month为实际month-1
                data.putExtra("day", dayOfMonth);
                setResult(2, data);
                finish();
            }
        });



    }
}
