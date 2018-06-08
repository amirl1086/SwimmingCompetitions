package com.app.swimmingcompetitions.swimmingcompetitions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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

    public String getShortDate(String dateTime) {
        String[] arrDateTime = dateTime.split(" ");
        String[] arrDate = arrDateTime[0].split("/");
        return arrDate[1] + "/" + arrDate[2];
    }

    public String getFullDate(String dateTime) {
        return dateTime.split(" ")[0];
    }

    public String getCompleteDate(Calendar dateStr){
        int hours = dateStr.get(Calendar.HOUR);
        if(dateStr.get(Calendar.AM_PM) == Calendar.PM) {
            hours += 12;
        }

        int minutes = dateStr.get(Calendar.MINUTE);
        String minutesStr = (minutes < 10) ? "0" + String.valueOf(minutes) : String.valueOf(minutes);

        String result = "";
        if(dateStr.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
            result +=  "התקיימה ב - ";
        }
        else {
            result +=  "תתקיים ב - ";
        }
        result += getDate(dateStr) + ",   בשעה - " + hours + ":" + minutesStr;
        return result;
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

    public Calendar stringToCalendar(String date) {
        System.out.println("date " + date);
        Calendar calendar = Calendar.getInstance();
        String[] birthDateArr = date.split(" ");

        String[] dateArr = birthDateArr[0].split("/");
        int day = Integer.valueOf(dateArr[0]);
        int month = Integer.valueOf(dateArr[1]) - 1;
        int year = Integer.valueOf(dateArr[2]);

        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);

        String[] timeArr = birthDateArr[1].split(":");
        int hours = Integer.valueOf(timeArr[0]);
        int minutes = Integer.valueOf(timeArr[1]);

        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);
        System.out.println("date CAL " + calendar.get(Calendar.DAY_OF_MONTH) + " " + calendar.get(Calendar.MONTH) + " " + calendar.get(Calendar.YEAR));
        return calendar;
    }

    public Calendar strTimeStampToCalendar(String isoDate) {
        Calendar calendar = Calendar.getInstance();
        String[] birthDateArr = isoDate.split(" ");

        String[] dateArr = birthDateArr[0].split("/");
        int day = Integer.valueOf(dateArr[0]);
        int month = Integer.valueOf(dateArr[1]) - 1;
        int year = Integer.valueOf(dateArr[2]);

        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);

        String[] timeArr = birthDateArr[1].split(":");
        int hours = Integer.valueOf(timeArr[0]);
        int minutes = Integer.valueOf(timeArr[1]);
        int seconds = Integer.valueOf(timeArr[2]);

        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);
        calendar.set(Calendar.SECOND, seconds);

        return calendar;
    }

    public String getTime(Calendar calendar) {
        return calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE);
    }

    public Date getDateFromString(String date) {
        String[] birthDateArr = date.split(" ");

        String[] dateArr = birthDateArr[0].split("/");
        int day = Integer.valueOf(dateArr[0]);
        int month = Integer.valueOf(dateArr[1]) - 1;
        int year = Integer.valueOf(dateArr[2]);
        Calendar calendar = Calendar.getInstance(); /*Calendar.(year, month, day, hours, minutes);*/

        String[] timeArr = birthDateArr[1].split(":");
        int hours = Integer.valueOf(timeArr[0]);
        int minutes = Integer.valueOf(timeArr[1]);
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);

        return new Date(year, month, day, hours, minutes);
    }

    public String getDate(Calendar calendar) {
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        String dayStr = (day < 10) ? "0" + String.valueOf(day) : String.valueOf(day);
        String monthStr = (month < 10) ? "0" + String.valueOf(month) : String.valueOf(month);

        return dayStr + "/" + monthStr + "/" + year;
    }

    public int getAgeByDate(String birthDate){
        String[] birthDateArr = birthDate.split(" ");

        String[] dateArr = birthDateArr[0].split("/");
        int day = Integer.valueOf(dateArr[0]);
        int month = Integer.valueOf(dateArr[1]) - 1;
        int year = Integer.valueOf(dateArr[2]);
        Calendar dob = Calendar.getInstance(); /*Calendar.(year, month, day, hours, minutes);*/

        if(birthDateArr.length > 1) {
            String[] timeArr = birthDateArr[1].split(":");
            int hours = Integer.valueOf(timeArr[0]);
            int minutes = Integer.valueOf(timeArr[1]);
            dob.set(Calendar.HOUR_OF_DAY, hours);
            dob.set(Calendar.MINUTE, minutes);
        }

        dob.set(Calendar.DAY_OF_MONTH, day);
        dob.set(Calendar.MONTH, month);
        dob.set(Calendar.YEAR, year);


        Calendar today = Calendar.getInstance();

        System.out.println(dob.get(Calendar.YEAR));
        System.out.println(today.get(Calendar.YEAR));

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        Integer ageInt = age;
        return ageInt;
    }
}
