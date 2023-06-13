package com.example.todolistandroidapp;

import android.os.Build;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;

public class DateDifference {
    public static final String FORMAT_STRING = "dd/MM/yyyy HH:mm:ss"; // day/month/year hour:minute:second
    private int seconds, minutes, hours, days, months, years;
    private boolean isBefore;


    private DateDifference(LocalDateTime date1, LocalDateTime date2) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(FORMAT_STRING);
            isBefore = date1.isBefore(date2);
            if (!isBefore) {
                // swap dates
                LocalDateTime temp = date1;
                date1 = date2;
                date2 = temp;
            }
            years = date2.getYear() - date1.getYear();
            months = date2.getMonthValue() - date1.getMonthValue();
            days = date2.getDayOfMonth() - date1.getDayOfMonth();
            hours = date2.getHour() - date1.getHour();
            minutes = date2.getMinute() - date1.getMinute();
            seconds = date2.getSecond() - date1.getSecond();
            if (seconds < 0) {
                minutes -= 1;
                seconds += 60;
            }
            if (minutes < 0) {
                hours -= 1;
                minutes += 60;
            }
            if (hours < 0) {
                days -= 1;
                hours += 24;
            }
            if (days < 0) {
                YearMonth yearMonth = YearMonth.of(date1.getYear(), date1.getMonthValue());
                months -= 1;
                days += yearMonth.lengthOfMonth();
            }
            if (months < 0) {
                years -= 1;
                months += 12;
            }
        }
        else {
            years = months = days = hours = minutes = seconds = 0;
        }
    }

    public static DateDifference between(String date1, String date2) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(FORMAT_STRING);
            return new DateDifference(LocalDateTime.parse(date1, DATE_TIME_FORMATTER), LocalDateTime.parse(date2, DATE_TIME_FORMATTER));
        }
        return null;
    }

    public static DateDifference fromNow(String dstTime) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(FORMAT_STRING);
            return new DateDifference(LocalDateTime.now(), LocalDateTime.parse(dstTime, DATE_TIME_FORMATTER));
        }
        return null;
    }

    public static boolean isCorrectFormat(String dateStr) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(FORMAT_STRING);
                LocalDateTime.parse(dateStr, DATE_TIME_FORMATTER);
                return true;
            }
            return false;
        }
        catch (Exception e) {
            return false;
        }
    }

    public static String dateToString(LocalDateTime localDateTime) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(FORMAT_STRING);
            return localDateTime.format(DATE_TIME_FORMATTER);
        }
        return "";
    }

    public static String nowToString() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return dateToString(LocalDateTime.now());
        }
        return "";
    }
    @Override
    public String toString() {
        LinkedList<String> parts = new LinkedList<>();

        tryAdd(parts, years, "year");
        tryAdd(parts, months, "month");
        tryAdd(parts, days, "day");
        tryAdd(parts, hours, "hour");
        tryAdd(parts, minutes, "minute");
        tryAdd(parts, seconds, "second");
        if (parts.isEmpty()) {
            return "0 seconds";
        }
        String result = "";
        for (String part: parts) {
            if (part != parts.getFirst()) {
                if (part == parts.getLast())
                    result += " and ";
                else
                    result += ", ";
            }
            result += part;
        }
        return result;
    }

    public String toString(String beforeFormat, String afterFormat) {
        String result = "";
        String format;
        if (isBefore)
            format = beforeFormat;
        else
            format = afterFormat;
        char c;
        for (int i = 0; i < format.length(); i++) {
            c = format.charAt(i);
            if (c == '\0')
                result += this;
            else
                result += c;
        }
        return result;
    }

    private void tryAdd(LinkedList<String> parts, long number, String timePart) {
        if (number > 0) {
            String str = String.format("%d %s", number, timePart);
            if (number > 1)
                str += "s";
            parts.addLast(str);
        }
    }

    public boolean isBefore() {
        return isBefore;
    }
}
