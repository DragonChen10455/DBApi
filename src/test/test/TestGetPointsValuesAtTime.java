package test;

import db.DBApiEntry;
import db.DBVal;
import db.Point;
import db.Util;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TestGetPointsValuesAtTime {
    public static void main(String[] args) throws ParseException {
        String host = Globals.HOST;
        int port = Globals.port;
        DBApiEntry entry = DBApiEntry.initApiEntry(host, port);
        String metricName = "metricName1";
        String pointNamePrefix = "pointName_test";

        List<Point> points = new ArrayList<>();
        int pointCount = 10;
        for (int i = 0; i < pointCount; i++) {
            HashMap<String, String> tags = new HashMap<>();
            tags.put("pointName", pointNamePrefix + i);
            tags.put("status", "3");
            points.add(new Point(metricName, tags));
        }

        String timeString = "2021-08-15 09:59:59";
        long time =  Util.dateStringToUTCMilliSeconds(timeString);

        long testBegin = System.currentTimeMillis();
        List<DBVal> dbvals = entry.getPointsValuesAtTime(points, time);
        long testEnd = System.currentTimeMillis();
        System.out.println(String.format("spend %d (ms)", testEnd-testBegin));

        boolean isShowResult = true;
        if(isShowResult) {
            for (DBVal val : dbvals) {
                System.out.println(val.toString());
            }
        }
    }
}
