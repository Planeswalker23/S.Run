package util;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.ArrayList;
import java.util.Collection;

public class AddMyRunDate {

    public static Collection<CalendarDay> AddMyRunDateToCollection() {
        Collection<CalendarDay> dates=new ArrayList<>();

        //添加需要标记的日期
        dates.add(new CalendarDay(2018, 5, 2));//month设置为5，实际显得的是6月

        return dates;
    }


}
