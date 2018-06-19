package com.swimming.amirl.swimmimg_competitions;

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

    public String getShortDate(String dateTime) {
        String[] arrDateTime = dateTime.split(" ");
        String[] arrDate = arrDateTime[0].split("/");
        if(Integer.valueOf(arrDate[1]) < 10) {
            arrDate[1] = "0" + arrDate[1];
        }
        if(Integer.valueOf(arrDate[2]) < 10) {
            arrDate[2] = "0" + arrDate[2];
        }
        return arrDate[1] + "/" + arrDate[2];
    }

    public String getFullDate(String dateTime) {
        return dateTime.split(" ")[0];
    }
    public String getCompleteDate(String dateStr){
        return getCompleteDate(stringToCalendar(dateStr));
    }

    public String getCompleteDate(Calendar date){
        int hours = date.get(Calendar.HOUR);
        if(date.get(Calendar.AM_PM) == Calendar.PM) {
            hours += 12;
        }

        int minutes = date.get(Calendar.MINUTE);
        String minutesStr = (minutes < 10) ? "0" + String.valueOf(minutes) : String.valueOf(minutes);

        String result = "";

        long currentDateTime = date.getTimeInMillis();
        Calendar today = Calendar.getInstance();
        today.set(Calendar.MONTH, today.get(Calendar.MONTH) + 1);
        today.set(Calendar.DAY_OF_YEAR, today.get(Calendar.DAY_OF_YEAR) - 1);

        if(currentDateTime < today.getTimeInMillis()) {
            result +=  "התקיימה ב - ";
        }
        else {
            result +=  "תתקיים ב - ";
        }
        result += getDate(date) + ", בשעה - " + hours + ":" + minutesStr;
        return result;
    }

    public boolean isDatePassed(String dateStr) {
        Calendar date = stringToCalendar(dateStr);
        long currentDateTime = date.getTimeInMillis();
        Calendar today = Calendar.getInstance();
        today.set(Calendar.MONTH, today.get(Calendar.MONTH) + 1);
        today.set(Calendar.DAY_OF_YEAR, today.get(Calendar.DAY_OF_YEAR) - 1);
        return currentDateTime < today.getTimeInMillis();
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
        Calendar calendar = Calendar.getInstance();
        String[] birthDateArr = date.split(" ");

        String[] dateArr = birthDateArr[0].split("/");
        int day = Integer.valueOf(dateArr[0]);
        int month = Integer.valueOf(dateArr[1]);
        int year = Integer.valueOf(dateArr[2]);

        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);

        String[] timeArr = birthDateArr[1].split(":");
        int hours = Integer.valueOf(timeArr[0]);
        int minutes = Integer.valueOf(timeArr[1]);

        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);
        return calendar;
    }

    public Calendar strTimeStampToCalendar(String isoDate) {
        Calendar calendar = Calendar.getInstance();
        String[] birthDateArr = isoDate.split(" ");

        String[] dateArr = birthDateArr[0].split("/");
        int day = Integer.valueOf(dateArr[0]);
        int month = Integer.valueOf(dateArr[1]);
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
        int month = Integer.valueOf(dateArr[1]);
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
        int month = calendar.get(Calendar.MONTH);
        if(month == 0) {
            month = 12;
        }
        int year = calendar.get(Calendar.YEAR);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        String dayStr = (day < 10) ? "0" + String.valueOf(day) : String.valueOf(day);
        String monthStr = (month < 10) ? "0" + String.valueOf(month) : String.valueOf(month);

        return dayStr + "/" + monthStr + "/" + year;
    }

    public int getAgeByDate(String birthDate){
        String[] birthDateArr = birthDate.split(" ");

        String[] dateArr = birthDateArr[0].split("/");
        int day = Integer.valueOf(dateArr[0]);
        int month = Integer.valueOf(dateArr[1]);
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

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        int todayDay = today.get(Calendar.DAY_OF_YEAR);
        int dobDay = dob.get(Calendar.DAY_OF_YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        Integer ageInt = age;
        return ageInt;
    }
}
