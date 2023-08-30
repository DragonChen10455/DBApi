package test;

import db.DBApiEntry;
import db.DBVal;
import db.Point;
import db.Util;

import java.text.ParseException;
import java.util.HashMap;

public class TestGetMedianValue {

    public static void main(String[] args) throws ParseException {
        String host = Globals.HOST;
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
        long currentTime = Util.currentUTCMilliSeconds();
        endTime = currentTime;

        long testBegin = System.currentTimeMillis();
        DBApiEntry entry = DBApiEntry.initApiEntry(host,port);
        DBVal dbval = entry.getMedianValue(point,startTime,endTime);
        long testEnd = System.currentTimeMillis();
        System.out.println(String.format("spend %d (ms)", testEnd-testBegin));
        System.out.println(String.format("result:%s", dbval));
    }

}
