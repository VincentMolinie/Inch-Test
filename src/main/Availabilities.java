package main;

import java.util.*;
import java.util.Calendar;

/**
 * Created by Vince on 9/16/2016.
 */
public class Availabilities {
    protected static final String[] MONTHS = {"January", "February", "March", "April", "May", "June",
                                            "July", "August", "September", "October", "November", "December"};

    protected ArrayList<Integer> hours;
    protected int year;
    protected int month;
    protected int day;


    public Availabilities(int day, int month, int year, ArrayList<Integer> hours) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.hours = hours;
    }

    public static ArrayList<Availabilities> constructAvailabilities(Date fromDate, Date toDate, Availabilities latestAvailabilities) {
        ArrayList<Availabilities> availabilities = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(toDate);
        int lastDay = calendar.get(Calendar.DAY_OF_MONTH);
        int lastMonth = calendar.get(Calendar.MONTH);
        int lastYear = calendar.get(Calendar.YEAR);
        int lastHour = calendar.get(Calendar.HOUR_OF_DAY);
        int lastMinute = calendar.get(Calendar.MINUTE);

        calendar.setTime(fromDate);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        while (true) {
            ArrayList<Integer> hoursAvailable = new ArrayList<>();
            if (year < lastYear || month < lastMonth || day < lastDay) {
                while (hour < 24) {
                    while (minute < 60) {
                        hoursAvailable.add(hour * 100 + minute);
                        minute += 30;
                    }
                    minute = 0;
                    hour += 1;
                }
                if (latestAvailabilities != null &&
                        latestAvailabilities.month == month &&
                        latestAvailabilities.day == day &&
                        latestAvailabilities.year == year) {
                    latestAvailabilities.addHours(hoursAvailable);
                    latestAvailabilities = null;
                } else {
                    availabilities.add(new Availabilities(day, month, year, hoursAvailable));
                }

                calendar.add(Calendar.DAY_OF_MONTH, 1);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                month = calendar.get(Calendar.MONTH);
                year = calendar.get(Calendar.YEAR);
                hour = 0;
                minute = 0;
            } else {
                while (hour <= lastHour) {
                    while ((hour < lastHour && minute < 60) || minute < lastMinute) {
                        hoursAvailable.add(hour * 100 + minute);
                        minute += 30;
                    }
                    minute = 0;
                    hour += 1;
                }
                if (latestAvailabilities != null &&
                        latestAvailabilities.month == month &&
                        latestAvailabilities.day == day &&
                        latestAvailabilities.year == year) {
                    latestAvailabilities.addHours(hoursAvailable);
                } else {
                    availabilities.add(new Availabilities(day, month, year, hoursAvailable));
                }
                break;
            }
        }

        return availabilities;
    }

    public void addHours(ArrayList<Integer> hours) {
        this.hours.addAll(hours);
    }

    public ArrayList<Integer> getHours() {
        return hours;
    }

    public void setHours(ArrayList<Integer> hours) {
        this.hours = hours;
    }

    public String getDate() {
        String end = "th";
        if (day == 1)
            end = "st";
        else if (day == 2)
            end = "nd";
        else if (day == 3)
            end = "rd";

        return MONTHS[month] + " " + day + end;
    }
}
