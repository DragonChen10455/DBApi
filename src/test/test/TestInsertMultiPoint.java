package test;

import db.DBVal;
import db.DBApiEntry;
import db.Point;
import db.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TestInsertMultiPoint {
    public static void main(String[] args) {
        String host = Globals.HOST;
//        String host = "192.168.50.130";

        int port = Globals.port;
        DBApiEntry entry = DBApiEntry.initApiEntry(host, port);
        String metricName = "metricName1";
        List<DBVal> dbvals = new ArrayList<DBVal>();

        long timeStampBegin = Util.currentUTCMilliSeconds();
        int pointCount = 1000000;
        int dataCountPerPoint = 1;
        for (int pointIndex = 0; pointIndex < pointCount; pointIndex++) {
            HashMap<String, String> tags = new HashMap<>();
            tags.put("pointName", "pointName_test");
            tags.put("status", "3");
            Point point = new Point(metricName, tags);

            for(int i=1;i<=dataCountPerPoint;i++) {
                DBVal val = new DBVal();
                val.setPoint(point);
                val.setValue(pointIndex);
                val.setUtcTimeMilliSeconds(timeStampBegin - i*pointIndex*1000);
                dbvals.add(val);
            }
        }
        long testBegin = System.currentTimeMillis();
        entry.insertMultiPoint(dbvals);
        long testEnd = System.currentTimeMillis();
        System.out.println(String.format("spend %d (ms)", testEnd-testBegin));
    }
}
