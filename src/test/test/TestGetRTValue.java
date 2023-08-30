package test;

import db.DBApiEntry;
import db.DBVal;
import db.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TestGetRTValue {
    public static void main(String[] args) {
        String host = Globals.HOST;
//        String host = "39.104.80.229";
        int port = Globals.port;
        DBApiEntry entry = DBApiEntry.initApiEntry(host, port);
        String metricName = "metricName1";
        String pointNamePrefix = "pointName_test";
        int pointIndex = 0;
        HashMap<String, String> tags = new HashMap<>();
        tags.put("pointName", pointNamePrefix + pointIndex);
        tags.put("status", "3");

        Point point = new Point(metricName, tags);

        long testBegin = System.currentTimeMillis();
        DBVal dbval = entry.getRTValue(point);
        long testEnd = System.currentTimeMillis();
        System.out.println(String.format("spend %d (ms)", testEnd-testBegin));

        boolean isShowResult = true;
        if(isShowResult) {
            System.out.println(dbval.toString());
        }
    }
}
