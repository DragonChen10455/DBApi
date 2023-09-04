package test;

import db.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
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

        long timeStampBegin = Util.dateStringToUTCMilliSeconds("2023-09-04 17:02:00");
        long testBegin = System.currentTimeMillis();
        int dataCountPerPoint = 10;
        long[] utcTimes = new long[dataCountPerPoint];
        double[] value1s = new double[dataCountPerPoint];
        double[] value2s = new double[dataCountPerPoint];

        for (int i = 0; i < dataCountPerPoint; i++) {
            utcTimes[i] = timeStampBegin - i;
            value1s[i] = i;
            value2s[i] = i;
        }
        PointVals pointVals = new PointVals(point, dataCountPerPoint, utcTimes, value1s, value2s);
        long testEndCompute = System.currentTimeMillis();
        entry.insertSinglPoint(pointVals);
        long testEnd = System.currentTimeMillis();
        System.out.println(String.format("spend total:%d (ms) compute:%d (ms)", testEnd-testBegin,
                testEndCompute-testBegin));
    }
}
