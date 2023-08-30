package test;

import db.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TestInsertSinglePoint {
    public static void main(String[] args) throws ParseException {
//        String host = "192.168.101.87";
        String host = Globals.HOST;
        int port = Globals.port;
        DBApiEntry entry = DBApiEntry.initApiEntry(host, port);
        entry.setUseLB(false);
//        entry.setUseLB(true);

        String metricName = "metricName1";
        HashMap<String, String> tags = new HashMap<>();
        tags.put("pointName", "pointName_test1");
        tags.put("status", "1");
        Point point = new Point(metricName, tags);

        long timeStampBegin = Util.dateStringToUTCMilliSeconds("2023-08-30 19:21:00");
        long testBegin = System.currentTimeMillis();
        int dataCountPerPoint = 1000000;
        long[] utcTimes = new long[dataCountPerPoint];
        double[] values = new double[dataCountPerPoint];

        for (int i = 0; i < dataCountPerPoint; i++) {
            utcTimes[i] = timeStampBegin - i;
            values[i] = i;
        }
        PointVals pointVals = new PointVals(point, dataCountPerPoint, utcTimes, values);
        long testEndCompute = System.currentTimeMillis();
        entry.insertSinglPoint(pointVals);
        long testEnd = System.currentTimeMillis();
        System.out.println(String.format("spend total:%d (ms) compute:%d (ms)", testEnd-testBegin,
                testEndCompute-testBegin));
    }
}
