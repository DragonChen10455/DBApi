package test;

import db.DBApiEntry;
import db.DBVal;
import db.Point;
import db.Util;

import java.text.ParseException;
import java.util.HashMap;

public class TestGetPointValueAtTime {
    public static void main(String[] args) throws ParseException {
        String host = Globals.HOST;
        int port = Globals.port;
        DBApiEntry entry = DBApiEntry.initApiEntry(host, port);
        String metricName = "metricName1";
        String pointNamePrefix = "pointName_test";
        int pointIndex = 0;
        HashMap<String, String> tags = new HashMap<>();
        tags.put("pointName", pointNamePrefix + pointIndex);
        tags.put("status", "3");

        Point point = new Point(metricName, tags);
        String timeString = "2021-08-15 09:59:58";
        long time =  Util.dateStringToUTCMilliSeconds(timeString);

        long testBegin = System.currentTimeMillis();
        DBVal dbval = entry.getPointValueAtTime(point, time);
        long testEnd = System.currentTimeMillis();
        System.out.println(String.format("spend %d (ms)", testEnd-testBegin));

        boolean isShowResult = true;
        if(isShowResult) {
            System.out.println(dbval.toString());
        }
    }
}
