package com.app.swimmingcompetitions.swimmingcompetitions;

import java.util.Calendar;
import java.util.Date;

public class HebrewDate {

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


}
