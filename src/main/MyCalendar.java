package main;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * Created by Vince on 9/15/2016.
 */
public class MyCalendar {
    private static Collection<Event> events = new ArrayList<>();
    private static Collection<Window> windows = new ArrayList<>();

    public static void registerEvent(Event event) {
        ArrayList<Window> availableWindows = windows.stream()
                .filter(window -> window.isAvailable(event))
                .collect(Collectors.toCollection(ArrayList<Window>::new));

        //availableWindows.sort((o1, o2) -> (int)
        //        (o2.getStartDate().getTime() % main.Window.WEEK_IN_MS - o1.getStartDate().getTime() % main.Window.WEEK_IN_MS));
        for (Window window : availableWindows) {
            window.insertEvent(event);
            if (!window.isFree())
                windows.remove(window);
        }

        events.add(event);
    }

    public static void registerWindow(Window window) {
        ArrayList<Event> eventsInWindow = events.stream()
                .filter(event -> window.isAvailable(event))
                .collect(Collectors.toCollection(ArrayList<Event>::new));

        for (Event event : eventsInWindow) {
            window.insertEvent(event);
        }
        if (!window.isFree())
            windows.remove(window);

        windows.add(window);
    }

    public static JSONArray availabilities(Date fromDate, Date toDate) throws JsonProcessingException {
        ArrayList<Availabilities> availableWindows = new ArrayList<>();

        for (Window window : windows) {
            availableWindows.addAll(window.getAvailabilities(fromDate, toDate));
        }

        //Transform the object to JSON
        ObjectMapper mapper = new ObjectMapper();
        String resultAsString = mapper.writeValueAsString(availableWindows);

        return new JSONArray(resultAsString);
    }

    private static boolean isInsideWindow(Window window, Date fromDate, Date toDate) {
        //If the start date is between the available Date it's good.
        if (window.getStartDate().after(fromDate) && window.getStartDate().before(toDate))
            return true;
        //If the end date is between the available Date it's good.
        if (window.getEndDate().after(fromDate) && window.getEndDate().before(toDate))
            return true;
        //If the available date is between the from and to it's good.
        return window.getStartDate().before(fromDate) && window.getEndDate().after(toDate);
    }
}
