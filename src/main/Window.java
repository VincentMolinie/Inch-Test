package main;

import java.util.*;
import java.util.Calendar;

/**
 * Created by Vince on 9/15/2016.
 */
public class Window {
    private Boolean recurring;
    private Date startDate;
    private Date endDate;

    private SortedSet<Event> events;
    private boolean free;

    static final int WEEK_IN_MS = 7*24*60*60*1000;



    public Window(Date startDate, Date endDate) {
        super();
        this.recurring = false;
        this.startDate = startDate;
        this.endDate = endDate;

        Comparator<Event> comparator = new Comparator<Event>() {
            @Override
            public int compare(Event o1, Event o2) {
                return (int) (o1.getDate().getTime() - o2.getDate().getTime());
            }
        };
        this.events = new TreeSet<>(comparator);
        this.free = true;

        MyCalendar.registerWindow(this);
    }

    public Window(Date startDate, Date endDate, boolean recurring) {
        super();
        this.recurring = recurring;
        this.startDate = startDate;
        this.endDate = endDate;

        Comparator<Event> comparator = new Comparator<Event>() {
            @Override
            public int compare(Event o1, Event o2) {
                return (int) (o1.getDate().getTime() - o2.getDate().getTime());
            }
        };
        this.events = new TreeSet<>(comparator);
        this.free = true;

        MyCalendar.registerWindow(this);
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public Boolean isRecurring() {
        return recurring;
    }

    public void insertEvent(Event event) {
        events.add(event);

        //Check if there is still some time available
        if (!isRecurring()) {
            long startDate = 0;
            long endDate = 0;
            for (Event e : events) {
                if (startDate == 0) {
                    startDate = e.getDate().getTime();
                } else if (endDate != e.getDate().getTime()) {
                    return;
                }
                endDate = e.getEndDate().getTime();
            }

            if (this.getStartDate().getTime() == startDate &&
                    this.getEndDate().getTime() == endDate)
                free = false;
        }
    }

    public boolean isAvailable(Event newEvent) {
        //Is not free return immediately
        if (!isFree())
            return false;

        //Check if it's at the same time as another event
        for (Event event : events) {
            if (event.getDate().compareTo(newEvent.getDate()) <= 0 &&
                    event.getEndDate().compareTo(newEvent.getDate()) > 0)
                return false;
            if (event.getDate().compareTo(newEvent.getDate()) > 0 &&
                    event.getDate().compareTo(newEvent.getEndDate()) < 0)
                return false;
        }

        if (isRecurring()) {
            long startWindow = this.getStartDate().getTime();
            long lengthWindow = this.getEndDate().getTime() - startWindow;
            long startEvent = newEvent.getDate().getTime();
            long lengthEvent = newEvent.getEndDate().getTime() - startEvent;
            long result = (startEvent - startWindow);
            result = result % WEEK_IN_MS;
            result += lengthEvent;
            return ((WEEK_IN_MS + startEvent - startWindow) % WEEK_IN_MS) + lengthEvent <= lengthWindow;
        } else {
            return this.getStartDate().compareTo(newEvent.getDate()) <= 0 &&
                    this.getEndDate().compareTo(newEvent.getEndDate()) >= 0;
        }
    }

    public ArrayList<Availabilities> getAvailabilities(Date fromDate, Date toDate) {
        //Is not free return immediately
        if (!isFree())
            return null;

        ArrayList<Availabilities> availabilities = new ArrayList<>();

        if (isRecurring()) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(this.getStartDate());
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
            calendar.setTime(this.getEndDate());
            int lastDayOfYear = calendar.get(calendar.DAY_OF_YEAR);
            int lastHour = calendar.get(Calendar.HOUR_OF_DAY);
            int lastMinute = calendar.get(Calendar.MINUTE);
            int diffDayOfYear = (365 + lastDayOfYear - dayOfYear) % 365;

            calendar.setTime(fromDate);
            int diffDay = (7 + dayOfWeek - calendar.get(Calendar.DAY_OF_WEEK)) % 7;
            calendar.add(Calendar.DAY_OF_WEEK, diffDay);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            while (calendar.getTime().before(toDate)) {
                calendar.set(Calendar.HOUR_OF_DAY, lastHour);
                calendar.set(Calendar.MINUTE, lastMinute);
                calendar.add(Calendar.DAY_OF_YEAR, diffDayOfYear);
                Date endDateOfWeek = calendar.getTime();
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                calendar.add(Calendar.DAY_OF_YEAR, -diffDayOfYear);
                Date startDateOfWeek = calendar.getTime();

                availabilities.addAll(getAvailabilitiesNotRecursive(fromDate, toDate, startDateOfWeek, endDateOfWeek));

                calendar.add(Calendar.WEEK_OF_YEAR, 1);
            }
        } else {
            availabilities = getAvailabilitiesNotRecursive(fromDate, toDate, this.getStartDate(), this.getEndDate());
        }

        return availabilities;
    }

    private ArrayList<Availabilities> getAvailabilitiesNotRecursive(Date fromDate, Date toDate,
                                                                    Date startDateWindow, Date endDateWindow) {
        ArrayList<Availabilities> availabilities = new ArrayList<>();

        fromDate = fromDate.before(startDateWindow) ? startDateWindow : fromDate;
        toDate = toDate.before(endDateWindow) ? toDate : endDateWindow;
        Availabilities latestAvailabilities = null;
        //Check if it's at the same time as another event
        for (Event event : events) {
            if (event.getDate().after(fromDate)) {
                if (event.getDate().after(toDate)) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(fromDate);
                    ArrayList<Availabilities> newAvailabilities = Availabilities.constructAvailabilities(fromDate, toDate, latestAvailabilities);
                    availabilities.addAll(newAvailabilities);

                    if (availabilities.size() > 0)
                        latestAvailabilities = availabilities.get(availabilities.size() - 1);

                    fromDate = toDate;
                    break;
                } else {
                    availabilities.addAll(Availabilities.constructAvailabilities(fromDate, event.getDate(), latestAvailabilities));
                    if (availabilities.size() > 0)
                        latestAvailabilities = availabilities.get(availabilities.size() - 1);

                    fromDate = event.getEndDate();
                }
            } else {
                //main.Event Start is before
                //fromDate is between start and end
                if (fromDate.before(event.getEndDate())) {
                    //main.Event is before the from date
                    continue;
                } else if (toDate.before(event.getEndDate())) {
                    //Already occupied
                    break;
                } else {
                    fromDate = event.getDate();
                }
            }
        }

        if (fromDate.compareTo(toDate) < 0)
            availabilities.addAll(Availabilities.constructAvailabilities(fromDate, toDate, latestAvailabilities));

        return availabilities;
    }

    public boolean isFree() {
        return free;
    }

    public SortedSet<Event> getEvents() {
        return events;
    }
}
