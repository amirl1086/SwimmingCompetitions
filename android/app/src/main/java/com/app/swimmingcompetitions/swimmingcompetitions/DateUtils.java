package com.app.swimmingcompetitions.swimmingcompetitions;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    private String[] months = {"ינואר", "פברואר", "מרץ", "אפריל", "מאי", "יוני", "יולי", "אוגוסט", "ספטמבר", "אוקטובר", "נובמבר", "דצמבר"};
    private String[] days = {"ראשון", "שני", "שלישי", "רביעי", "חמישי", "שישי", "שבת"};

    public String getCompleteHebrewDate(Calendar calendar) {
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

    public String getCompleteDate(Calendar calendar) {
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int monthDay = calendar.get(Calendar.DAY_OF_MONTH);
        int hours = calendar.get(Calendar.HOUR);
        int minutes = calendar.get(Calendar.MINUTE);

        String hebrewMonth = this.months[month];
        String hebrewDayOfWeek = this.days[day];

        return "מתקיימת ב: " + getDate(calendar) + ", בשעה - " + hours + ":" + minutes;
    }

    public String getHebrewDate(Calendar calendar) {
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int monthDay = calendar.get(Calendar.DAY_OF_MONTH);

        String hebrewMonth = this.months[month];
        String hebrewDayOfWeek = this.days[day];

        return "יום " + hebrewDayOfWeek + ", ה - " + monthDay + " ל" + hebrewMonth + ", " + year;
    }

    public Calendar dateToCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    public String getTime(Calendar calendar) {
        return calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE);
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

    public String getDate(Calendar calendar) {
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        return day + "/" + month + "/" + year;
    }

    public String getAge(Date birthDate){
        Calendar dob = this.dateToCalendar(birthDate);
        Calendar today = Calendar.getInstance();

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        return ageS;
    }


}
