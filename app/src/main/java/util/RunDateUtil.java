package util;


import com.google.gson.Gson;
import entity.RunDate;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class RunDateUtil {

    public static String returnAllDate(String id, String password, String sportsType,
                                       long startTime, long endTime, double totalDistance,
                                       double mCurrentLat, double mCurrentLon) {
        double CaloriesPerM = 0.072;
        double MetersPerStep = 0.45;
        String RunDateJson = null;
        RunDate runDate = new RunDate();
        DecimalFormat df = new DecimalFormat("#.00");
        SimpleDateFormat sdf  = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        String sTime = sdf.format(startTime);

        double totalTime = (endTime-startTime) / 1000;//单位秒

        runDate.setId(id);
        runDate.setPassword(password);
        runDate.setSportsType(sportsType);
        runDate.setStartTime(sTime);
        runDate.setTotalTime(totalTime);//单位秒
        runDate.setTotalDistance(Double.parseDouble(df.format(totalDistance)));//单位米
        runDate.setCalories(Double.parseDouble(df.format(CaloriesPerM * totalDistance)));
        if (totalDistance != 0){
            runDate.setTimePerKM(Double.parseDouble(df.format(1000 * totalTime / totalDistance)));
        } else {
            runDate.setTimePerKM(0.0);
        }
        runDate.setStepCount((int)(totalDistance / MetersPerStep));
        runDate.setmCurrentLat(mCurrentLat);
        runDate.setmCurrentLon(mCurrentLon);
        //分数
        runDate.setScore(judgeScore(sportsType, totalDistance, totalTime));

        //封装JSON格式数据
        Gson gson = new Gson();
        RunDateJson = gson.toJson(runDate);

        return RunDateJson;
    }

    public static String toTimeMMHH(double totalTime){
        String timeHHMM = null;
        int h = (int)(totalTime / 3600);
        int m = (int)((totalTime % 3600) / 60);
        timeHHMM = h + ":" + m;
        return timeHHMM;
    }

    public static int judgeScore(String sportsType, double distance, double totaltime){
        int score = 0;
        switch (sportsType){
            case "run":
                score = (int)(distance / 1000) * 2;
                break;
            case "walk":
                score = (int)(distance / 1000);
                break;
            case "ball":
                score = (int)(totaltime / 1800) * 2;
                break;
        }
        return score;
    }



}