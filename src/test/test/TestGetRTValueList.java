package test;

import db.DBVal;
import db.DBApiEntry;
import db.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TestGetRTValueList {
    public static void main(String[] args) {
        String host = Globals.HOST;
//        String host = "39.104.80.229";
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

        long testBegin = System.currentTimeMillis();
        List<DBVal> dbvals = entry.getRTValueList(points);
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
