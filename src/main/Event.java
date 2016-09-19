package main;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.JSONArray;

import java.util.Date;

/**
 * Created by Vince on 9/15/2016.
 */
public class Event {
    private Date date;
    private Date endDate;


    public Event(Date date, Date endDate) {
        super();
        this.date = date;
        this.endDate = endDate;

        MyCalendar.registerEvent(this);
    }


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getEndDate() {
        return endDate;
    }

    public static JSONArray availabilities(Date fromDate, Date toDate) throws JsonProcessingException {
        return MyCalendar.availabilities(fromDate, toDate);
    }
}
