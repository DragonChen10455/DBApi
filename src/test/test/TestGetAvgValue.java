package test;

import db.DBVal;
import db.DBApiEntry;
import db.Point;
import db.Util;

import java.text.ParseException;
import java.util.HashMap;

public class TestGetAvgValue {
    public static void main(String[] args) throws ParseException {
        String host = Globals.HOST;
        int port = Globals.port;
        String metricName = "metricName1";
        HashMap<String, String> tags = new HashMap<>();
        tags.put("pointName", "pointName_test1");
        tags.put("status", "3");
        Point point = new Point(metricName, tags);

        String start = "2021-05-00 00:00:00";
        String end =  "2021-06-01 00:00:00";
        long startTime = Util.dateStringToUTCSeconds(start);
//        long endTime = Util.dateStringToUTCSeconds(end);
        long endTime = Util.currentUTCSeconds();

        long testBegin = System.currentTimeMillis();
        DBApiEntry entry = DBApiEntry.initApiEntry(host,port);
        DBVal dbval = entry.getAvgValue(point,startTime,endTime);
        long testEnd = System.currentTimeMillis();
        System.out.println(String.format("spend %d (ms)", testEnd-testBegin));

        System.out.println(String.format("result:%s", dbval));
    }
}
