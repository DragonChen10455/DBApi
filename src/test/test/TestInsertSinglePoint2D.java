package test;

import db.DBApiEntry;
import db.Point;
import db.PointVals;
import db.Util;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Random;

public class TestInsertSinglePoint2D {
    public static void main(String[] args) throws ParseException {
        Random random = new Random();
        double min = 10.00;
        double max = 100.00;
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

        long timeStampBegin = Util.dateStringToUTCMilliSeconds("2023-09-05 15:46:00");
        long testBegin = System.currentTimeMillis();
        int dataCountPerPoint = 1000000;
        long[] utcTimes = new long[dataCountPerPoint];
        double[] value1s = new double[dataCountPerPoint];
        double[] value2s = new double[dataCountPerPoint];

        for (int i = 0; i < dataCountPerPoint; i++) {
            double randomValue1 = min + (max - min) * random.nextDouble();
            double randomValue2 = min + (max - min) * random.nextDouble();
            utcTimes[i] = timeStampBegin - i;
            value1s[i] = Math.round(randomValue1 * 100.0) / 100.0;
            value2s[i] = Math.round(randomValue2 * 100.0) / 100.0;
        }
        PointVals pointVals = new PointVals(point, dataCountPerPoint, utcTimes, value1s, value2s);
        long testEndCompute = System.currentTimeMillis();
        entry.insertSinglPoint(pointVals);
        long testEnd = System.currentTimeMillis();
        System.out.println(String.format("spend total:%d (ms) compute:%d (ms)", testEnd-testBegin,
                testEndCompute-testBegin));
    }
}