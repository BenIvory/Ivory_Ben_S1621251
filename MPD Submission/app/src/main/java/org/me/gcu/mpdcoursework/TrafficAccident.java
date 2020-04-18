package org.me.gcu.mpdcoursework;
//Ben Ivory S1621251

import java.io.Serializable;
import java.util.Calendar;

public class TrafficAccident implements Serializable {

    public String title;
    public String description;
    public String link;
    public String geoPoint;
    public String author;
    public String comments;
    public String date;
    public long time;
    public int type;
    public Calendar calendarStart;
    public Calendar calendarEnd;
    public float latitude;
    public float longitude;

    public TrafficAccident () {
        title = "";
        description = "";
        link = "";
        geoPoint = "";
        author = "";
        comments = "";
        date = "";
        time = -1;
        type = -1;
        latitude = 0.0f;
        longitude = 0.0f;
        calendarStart = Calendar.getInstance();
        calendarEnd = Calendar.getInstance();
    }
}
