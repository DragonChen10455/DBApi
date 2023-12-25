package test;

import db.*;

import java.text.ParseException;
import java.util.*;

public class TestInsertSinglePoint {
    public static void main(String[] args) throws ParseException {
        Random random = new Random();
        double min = 1.00;
        double max = 10000.00;
        String host = Globals.HOST;
        int port = Globals.port;
        DBApiEntry entry = DBApiEntry.initApiEntry(host, port);
        entry.setUseLB(false);
//        entry.setUseLB(true);

        String metricName = "metricName1";
        HashMap<String, String> tags = new HashMap<>();
        tags.put("pointName", "pointName_test2");
        tags.put("status", "1");
        Point point = new Point(metricName, tags);

        long timeStampBegin = Util.dateStringToUTCMilliSeconds("2023-12-15 00:00:00");
        long timeStampEnd = Util.dateStringToUTCMilliSeconds("2023-12-25 00:00:00");
        long testBegin = System.currentTimeMillis();
        int dataCountPerPoint = 1000000;
        long[] utcTimes = new long[dataCountPerPoint];
        double[] values = new double[dataCountPerPoint];

        for (int i = 0; i < dataCountPerPoint; i++) {
            double randomValue = min + (max - min) * random.nextDouble();
            long randomTimeStamp = timeStampBegin + (long) (random.nextFloat() * (timeStampEnd - timeStampBegin + 1));
            utcTimes[i] = randomTimeStamp;
            values[i] = Math.round(randomValue * 100.0) / 100.0;
        }
        PointVals pointVals = new PointVals(point, dataCountPerPoint, utcTimes, values);
        long testEndCompute = System.currentTimeMillis();
        entry.insertSinglPoint(pointVals);
        long testEnd = System.currentTimeMillis();
        System.out.println(String.format("spend total:%d (ms) compute:%d (ms)", testEnd - testBegin,
                testEndCompute - testBegin));
    }
}
