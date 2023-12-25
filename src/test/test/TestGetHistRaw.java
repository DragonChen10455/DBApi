package test;

import db.DBApiEntry;
import db.DBVal;
import db.Point;
import db.Util;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

public class TestGetHistRaw {
    public static void main(String[] args) throws ParseException {
        String host = Globals.HOST;
//        String host = "192.168.50.130";
        int port = Globals.port;
        String metricName = "metricName1";
        HashMap<String, String> tags = new HashMap<>();
        tags.put("pointName", "pointName_test1");
        tags.put("status", "1");

        Point point = new Point(metricName, tags);

        String start = "2023-12-15 00:00:00";
        String end =  "2023-12-25 00:00:00";
        long startTime = Util.dateStringToUTCMilliSeconds(start);
        long endTime = Util.dateStringToUTCMilliSeconds(end);

        long testBegin = System.currentTimeMillis();
        DBApiEntry entry = DBApiEntry.initApiEntry(host,port);
//        entry.setUseLB(false);
//        entry.setUseLB(true);

        List<DBVal> lists =  entry.getHistRaw(point,startTime,endTime);
        long testEnd = System.currentTimeMillis();
        System.out.println(String.format("spend %d (ms)", testEnd-testBegin));
        System.out.println(lists.toString());
        System.out.println("数量：" + lists.size());
    }
}
