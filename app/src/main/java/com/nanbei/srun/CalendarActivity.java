package com.nanbei.srun;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.CalendarView;
import com.prolificinteractive.materialcalendarview.*;
import util.AddMyRunDate;
import util.MyRunDateDecorator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

public class CalendarActivity extends Activity implements OnDateSelectedListener {


    private CalendarView calendarView;
    private MaterialCalendarView widget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

//        calendarView = (CalendarView)findViewById(R.id.calendarview);
//        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
//            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
//                //回传数据
//                Intent data = new Intent();
//                data.putExtra("year", year);
//                data.putExtra("month", month + 1);//获得的月份month为实际month-1
//                data.putExtra("day", dayOfMonth);
//                setResult(2, data);
//                finish();
//            }
//        });

        widget = (MaterialCalendarView) findViewById(R.id.calendarView);
        //编辑日历属性
        widget.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)   //设置每周开始的第一天
                .setMinimumDate(CalendarDay.from(2015, 1, 1))  //设置可以显示的最早时间
                .setMaximumDate(CalendarDay.from(2019, 12, 31))//设置可以显示的最晚时间
                .setCalendarDisplayMode(CalendarMode.MONTHS)//设置显示模式，可以显示月的模式，也可以显示周的模式
                .commit();// 返回对象并保存
        widget.setSelectedDate(new Date());
        widget.setOnDateChangedListener(this);

        //调用AddMyRunDate工具类为dates中添加日期
        Collection<CalendarDay> dates = AddMyRunDate.AddMyRunDateToCollection();
        //添加装饰标记
        widget.addDecorators(new MyRunDateDecorator(this.getResources().getDrawable(R.drawable.my_selector), dates));
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

        int year = widget.getSelectedDate().getYear();
        int month = widget.getSelectedDate().getMonth() + 1;
        int day = widget.getSelectedDate().getDay();
        //回传数据
        Intent data = new Intent();
        data.putExtra("year", year);
        data.putExtra("month", month);//获得的月份month为实际month-1
        data.putExtra("day", day);
        setResult(2, data);
        finish();
    }


}
