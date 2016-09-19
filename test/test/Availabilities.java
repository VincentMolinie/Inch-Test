package test;

import main.Window;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by Vince on 9/19/2016.
 */
public class Availabilities {
    @Test
    public void testGetDate() {
        assert new main.Availabilities(1, 1, 2015, null).getDate().equalsIgnoreCase("February 1st");
        assert new main.Availabilities(2, 0, 2015, null).getDate().equalsIgnoreCase("January 2nd");
        assert new main.Availabilities(3, 2, 2015, null).getDate().equalsIgnoreCase("March 3rd");
        assert new main.Availabilities(4, 3, 2015, null).getDate().equalsIgnoreCase("April 4th");
        assert new main.Availabilities(10, 4, 2015, null).getDate().equalsIgnoreCase("May 10th");
        assert new main.Availabilities(11, 5, 2015, null).getDate().equalsIgnoreCase("June 11th");
        assert new main.Availabilities(12, 6, 2015, null).getDate().equalsIgnoreCase("July 12th");
        assert new main.Availabilities(13, 7, 2015, null).getDate().equalsIgnoreCase("August 13th");
        assert new main.Availabilities(14, 8, 2015, null).getDate().equalsIgnoreCase("September 14th");
        assert new main.Availabilities(15, 9, 2015, null).getDate().equalsIgnoreCase("October 15th");
        assert new main.Availabilities(21, 10, 2015, null).getDate().equalsIgnoreCase("November 21th");
        assert new main.Availabilities(31, 11, 2015, null).getDate().equalsIgnoreCase("December 31th");
    }

    @Test
    public void testOneDay() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("y M d H m");
        Date date = format.parse("2016 7 1 10 30"); // July 1st, 10:30
        Date endDate = format.parse("2016 7 1 14 00"); // July 1st, 14:00

        ArrayList<main.Availabilities> availabilities = main.Availabilities.constructAvailabilities(date, endDate, null);
        assert availabilities.size() == 1;
        assert availabilities.get(0).getDate().equalsIgnoreCase("July 1st");
        Integer[] hours = {1030, 1100, 1130, 1200, 1230, 1300, 1330};
        assert Arrays.equals(availabilities.get(0).getHours().toArray(), hours);
    }

    @Test
    public void testMultipleDays() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("y M d H m");
        Date date = format.parse("2016 7 1 21 30"); // July 1st, 21:30
        Date endDate = format.parse("2016 7 2 3 00"); // July 2nd, 3:00

        ArrayList<main.Availabilities> availabilities = main.Availabilities.constructAvailabilities(date, endDate, null);
        assert availabilities.size() == 2;
        assert availabilities.get(0).getDate().equalsIgnoreCase("July 1st");
        assert availabilities.get(1).getDate().equalsIgnoreCase("July 2nd");
        Integer[] hours = {2130, 2200, 2230, 2300, 2330};
        assert Arrays.equals(availabilities.get(0).getHours().toArray(), hours);
        hours = new Integer[]{0, 30, 100, 130, 200, 230};
        assert Arrays.equals(availabilities.get(1).getHours().toArray(), hours);
    }

    @Test
    public void testOneDayWithLatestAvailabilities() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("y M d H m");
        Date date = format.parse("2016 7 1 10 30"); // July 1st, 10:30
        Date endDate = format.parse("2016 7 1 14 00"); // July 1st, 14:00

        ArrayList<Integer> hoursAvailable = new ArrayList<>();
        hoursAvailable.add(900);
        main.Availabilities latestAvailabilities = new main.Availabilities(1, 6, 2016, hoursAvailable);
        ArrayList<main.Availabilities> availabilities = main.Availabilities.constructAvailabilities(date, endDate, latestAvailabilities);
        assert availabilities.size() == 0;
        assert latestAvailabilities.getDate().equalsIgnoreCase("July 1st");
        Integer[] hours = {900, 1030, 1100, 1130, 1200, 1230, 1300, 1330};
        assert Arrays.equals(latestAvailabilities.getHours().toArray(), hours);

        date = format.parse("2016 7 2 10 30"); // July 1st, 10:30
        endDate = format.parse("2016 7 2 14 00"); // July 1st, 14:00

        hoursAvailable = new ArrayList<>();
        hoursAvailable.add(900);
        latestAvailabilities = new main.Availabilities(1, 6, 2016, hoursAvailable);
        availabilities = main.Availabilities.constructAvailabilities(date, endDate, latestAvailabilities);
        assert availabilities.size() == 1;
        assert latestAvailabilities.getDate().equalsIgnoreCase("July 1st");
        hours = new Integer[]{900};
        assert Arrays.equals(latestAvailabilities.getHours().toArray(), hours);
        assert availabilities.get(0).getDate().equalsIgnoreCase("July 2nd");
        hours = new Integer[]{1030, 1100, 1130, 1200, 1230, 1300, 1330};
        assert Arrays.equals(availabilities.get(0).getHours().toArray(), hours);
    }
}
