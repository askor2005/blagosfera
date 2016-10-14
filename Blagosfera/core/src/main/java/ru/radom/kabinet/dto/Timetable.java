package ru.radom.kabinet.dto;

import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by rkorablin on 29.04.2015.
 */

public class Timetable {

    private static final Logger logger = LoggerFactory.getLogger(Timetable.class);

    public class Day {

        public Day(String title) {
            this.title = title;
        }

        private String title;
        private boolean full;
        private boolean[] hours = new boolean[24];

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public boolean isFull() {
            return full;
        }

        public void setFull(boolean full) {
            this.full = full;
        }

        public boolean[] getHours() {
            return hours;
        }

        public String getText() {
            int start = ArrayUtils.indexOf(hours, true);

            if (start == -1) {
                return "Выходной";
            } else {

                int end = ArrayUtils.lastIndexOf(hours, true) + 1;
                StringBuilder builder = new StringBuilder();
                builder.append("С ");
                if (start < 10) {
                    builder.append(0);
                }
                builder.append(start);
                builder.append(".00 до ");
                if (end < 10) {
                    builder.append(0);
                }
                builder.append(end);
                builder.append(".00");

                boolean breakTextAppended = false;
                boolean breakStarted = false;

                for (int i = start; i < end; i++) {
                    if (!hours[i]) {
                        if (!breakStarted) {
                            if (!breakTextAppended) {
                                builder.append("; перерыв");
                                breakTextAppended = true;
                            } else {
                                builder.append(",");
                            }
                            builder.append(" c ");
                            if (i < 10) {
                                builder.append(0);
                            }
                            builder.append(i);
                            builder.append(".00");
                            breakStarted = true;
                        }
                    } else {
                        if (breakStarted) {
                            builder.append(" до ");
                            if (i < 10) {
                                builder.append(0);
                            }
                            builder.append(i);
                            builder.append(".00");
                            breakStarted = false;
                        }
                    }
                }

                return builder.toString();
            }
        }

    }

    public class Hour {

        public Hour(String title) {
            this.title = title;
        }

        private String title;
        private boolean full;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public boolean isFull() {
            return full;
        }

        public void setFull(boolean full) {
            this.full = full;
        }
    }

    private Day[] days = new Day[7];
    private List<Day> combinedDays = new ArrayList<Day>();
    private Hour[] hours = new Hour[24];
    private boolean now;

    public Day[] getDays() {
        return days;
    }

    public List<Day> getCombinedDays() {
        return combinedDays;
    }

    ;

    public Hour[] getHours() {
        return hours;
    }

    public boolean getNow() {
        return now;
    }

    private String getDayTitle(int index) {
        switch (index) {
            case 0:
                return "Понедельник";
            case 1:
                return "Вторник";
            case 2:
                return "Среда";
            case 3:
                return "Четверг";
            case 4:
                return "Пятница";
            case 5:
                return "Суббота";
            case 6:
                return "Воскресенье";
            default:
                return "";
        }
    }

    private String getDayCombinedTitle(int index) {
        switch (index) {
            case 0:
                return "Пн";
            case 1:
                return "Вт";
            case 2:
                return "Ср";
            case 3:
                return "Чт";
            case 4:
                return "Пт";
            case 5:
                return "Сб";
            case 6:
                return "Вс";
            default:
                return "";
        }
    }

    private String getHourTitle(int index) {

        StringBuilder builder = new StringBuilder();
        if (index < 10) {
            builder.append(0);
        }
        builder.append(index);
        builder.append(".00 - ");
        index++;
        if (index < 10) {
            builder.append(0);
        }
        builder.append(index);
        builder.append(".00");
        return builder.toString();
    }

    public Timetable(String json) {

        for (int d = 0; d < 7; d++) {
            this.days[d] = new Day(getDayTitle(d));
        }

        for (int h = 0; h < 24; h++) {
            this.hours[h] = new Hour(getHourTitle(h));
        }

        if (json != null) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                JSONArray daysJsonArray = jsonObject.getJSONArray("days");
                for (int d = 0; d < 7; d++) {
                    JSONArray hoursJsonArray = daysJsonArray.getJSONArray(d);
                    int hoursCount = 0;
                    for (int h = 0; h < 24; h++) {
                        if (hoursJsonArray.getBoolean(h)) {
                            this.days[d].hours[h] = true;
                            hoursCount++;
                        }
                    }
                    if (hoursCount == 24) {
                        this.days[d].full = true;
                    }
                }
                for (int h = 0; h < 24; h++) {

                    int daysCount = 0;
                    for (int d = 0; d < 7; d++) {
                        if (this.days[d].hours[h]) {
                            daysCount++;
                        }
                    }
                    if (daysCount == 7) {
                        this.hours[h].full = true;
                    }
                }

            } catch (Exception e) {
                logger.info(e.getMessage(), e);
            }
        }

        List<int[]> combinedDaysBounds = new ArrayList<>();
        int lastStart = 0;
        for (int d = 0; d < 7; d++) {
            if (d == 6 || !Objects.deepEquals(days[lastStart].hours, days[d + 1].hours)) {
                combinedDaysBounds.add(new int[]{lastStart, d});
                lastStart = d + 1;
            }
        }

        for (int[] bounds : combinedDaysBounds) {
            String title;
            if (bounds[0] != bounds[1]) {
                StringBuilder builder = new StringBuilder();
                builder.append(getDayCombinedTitle(bounds[0]));
                builder.append(" - ");
                builder.append(getDayCombinedTitle(bounds[1]));
                title = builder.toString();
            } else {
                title = getDayCombinedTitle(bounds[0]);
            }
            Day day = new Day(title);
            for (int h = 0; h < 24; h++) {
                day.hours[h] = days[bounds[0]].hours[h];
            }
            this.combinedDays.add(day);
        }

        Date nowDate = new Date();
        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.setTime(nowDate);
        int nowHour = nowCalendar.get(Calendar.HOUR_OF_DAY);
        int nowDay = nowCalendar.get(Calendar.DAY_OF_WEEK);
        nowDay = (nowDay + 5) % 7;
        this.now = days[nowDay].hours[nowHour];
    }

}
