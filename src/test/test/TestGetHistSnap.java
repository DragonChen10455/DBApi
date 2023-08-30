package test;

import db.DBApiEntry;
import db.DBVal;
import db.Point;
import db.Util;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

public class TestGetHistSnap {
    public static void main(String[] args) throws ParseException {
        String host = Globals.HOST;
//        String host = "192.168.50.130";
        int port = Globals.port;
        String metricName = "metricName1";
        HashMap<String, String> tags = new HashMap<>();
        tags.put("pointName", "pointName_test1");
        tags.put("status", "3");
        Point point = new Point(metricName, tags);

        String start = "2021-08-15 09:59:58";
        String end =  "2021-08-15 10:00:00";
        long startTime = Util.dateStringToUTCMilliSeconds(start);
        long endTime = Util.dateStringToUTCMilliSeconds(end);

        long testBegin = System.currentTimeMillis();
        DBApiEntry entry = DBApiEntry.initApiEntry(host, port);
        List<DBVal> lists = entry.getHistSnap(point, startTime, endTime, 10);
        long testEnd = System.currentTimeMillis();
        System.out.println(String.format("spend %d (ms)", testEnd-testBegin));

        System.out.println(lists.toString());

    }
}
