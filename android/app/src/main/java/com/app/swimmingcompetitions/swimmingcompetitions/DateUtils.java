package com.app.swimmingcompetitions.swimmingcompetitions;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    private String[] months = {"ינואר", "פברואר", "מרץ", "אפריל", "מאי", "יוני", "יולי", "אוגוסט", "ספטמבר", "אוקטובר", "נובמבר", "דצמבר"};
    private String[] days = {"ראשון", "שני", "שלישי", "רביעי", "חמישי", "שישי", "שבת"};

    public String getHebrewDate(Calendar calendar) {
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int monthDay = calendar.get(Calendar.DAY_OF_MONTH);
        int hours = calendar.get(Calendar.HOUR);
        int minutes = calendar.get(Calendar.MINUTE);

        String hebrewMonth = this.months[month];
        String hebrewDayOfWeek = this.days[day];

        return "מתקיימת ביום " + hebrewDayOfWeek + ", ה - " + monthDay + " ל" + hebrewMonth + ", " + year + ", בשעה - " + hours + ":" + minutes;
    }

    public Calendar dateToCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    public Date createNewDate(String date, String time) {

        String[] dateArr = date.split("/");
        int day = Integer.valueOf(dateArr[0]);
        int month = Integer.valueOf(dateArr[1]) - 1;
        int year = Integer.valueOf(dateArr[2]);

        String[] timeArr = time.split(":");
        int hours =Integer.valueOf(timeArr[0]);
        int minutes = Integer.valueOf(timeArr[1]);

        return new Date(year, month, day, hours, minutes);
    }


}
