package test;

import db.DBApiEntry;
import db.DBVal;
import db.Point;
import db.Util;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

public class TestGetHistRawAllPoints {
    public static void main(String[] args) throws ParseException {
        String host = Globals.HOST;
//        String host = "192.168.50.130";
        int port = Globals.port;
        String metricName = ".*";
        HashMap<String, String> tags = new HashMap<>();

        Point point = new Point(metricName, tags);

        String start = "2021-08-15 09:59:58";
        String end =  "2021-08-15 10:00:00";
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
    }
}
