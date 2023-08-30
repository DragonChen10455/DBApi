package test;

import db.DBApiEntry;
import db.DBVal;
import db.Point;
import db.Util;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestGetHistRawMultiPoint {
    public static void main(String[] args) throws ParseException {
        String host = Globals.HOST;
//        String host = "192.168.50.130";
        int port = Globals.port;
        String metricName = "metricName1";
        HashMap<String, String> tags0 = new HashMap<>();
        tags0.put("pointName", "pointName_test0");
        tags0.put("status", "3");
        Point point0 = new Point(metricName, tags0);

        HashMap<String, String> tags1 = new HashMap<>();
        tags1.put("pointName", "pointName_test1");
        tags1.put("status", "3");
        Point point1 = new Point(metricName, tags1);

        HashMap<String, String> tags2 = new HashMap<>();
        tags2.put("pointName", "pointName_tes");
        tags2.put("status", "3");
        Point point2 = new Point(metricName, tags2);

        ArrayList<Point> points = new ArrayList<>();
        points.add(point0);
        points.add(point1);
        points.add(point2);

        String start = "2021-08-15 09:59:58";
        String end =  "2021-08-15 10:00:00";
        long startTime = Util.dateStringToUTCMilliSeconds(start);
        long endTime = Util.dateStringToUTCMilliSeconds(end);

        long testBegin = System.currentTimeMillis();
        DBApiEntry entry = DBApiEntry.initApiEntry(host,port);
        //        entry.setUseLB(false);
//        entry.setUseLB(true);

        Map<Point, List<DBVal>> result =  entry.getHistRaw(points,startTime,endTime);
        long testEnd = System.currentTimeMillis();
        System.out.println(String.format("spend %d (ms)", testEnd-testBegin));

        for (Point point : result.keySet()) {
            System.out.println(point);
        }
        System.out.println(result.toString());
    }
}
