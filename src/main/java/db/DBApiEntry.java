package db;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class DBApiEntry{
    private String host;
    private int port;
    private int insertPort;
    private int selectPort;
    private boolean addNullValue = true;
    private static final double VALUE_INVALID = -10000;
    private int splitSize = 1000000;
    private int oneThreadDataSize = 100000;
    private String metricNamePrefix = "";
    private boolean trimTagName = true;
    private int splitSizeRealTime = 20;
    private int nameSpace = 1;
    private boolean isUseLB = false;

    /**
     * 静态方法初始化时序数据源驱动类
     *
     * @return DBApiEntry
     */
    public static DBApiEntry initApiEntry(String addressHost, int publicPort) {
        if (StringUtils.isBlank(addressHost) || StringUtils.isBlank(publicPort + "")) {
            return null;
        }
        try {
            return new DBApiEntry(addressHost, publicPort);
        } catch (Exception e) {
//            log.error("[DBApiEntry] initApiEntry 初始化Entry失败, addressHost: " + addressHost + ", publicPort: " + publicPort);
            return null;
        }
    }

    /**
     * 初始化数据驱动构造方法
     *
     * @throws IOException
     */
    public DBApiEntry(String addressHost, int publicPort) throws IOException {
        this.host = addressHost;
        this.port = publicPort;
        this.insertPort = this.port;
        this.selectPort = this.port;
    }

    public void setUseLB(boolean useLB) {
        isUseLB = useLB;

        if(this.isUseLB) {
            this.insertPort = this.port+1;
            this.selectPort = this.port+2;
        }
        else {
            this.insertPort = this.port;
            this.selectPort = this.port;
        }
    }

    public int getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(int nameSpace) {
        this.nameSpace = nameSpace;
    }

    public String getMetricNamePrefix() {
        return metricNamePrefix;
    }

    public void setMetricNamePrefix(String metricNamePrefix) {
        this.metricNamePrefix = metricNamePrefix;
    }

    /**
     * 是否进行 trim 操作
     *
     * @return
     */
    public boolean isTrimTagName() {
        return trimTagName;
    }

    public boolean isAddNullValue() {
        return addNullValue;
    }

    /**
     * 点名合法性检查函数，强化版
     *
     * @return
     * @date
     */

    protected String trimStrong(String tagName) {
        try {
            String regEx = "[\n`~!@#$%^&*(){}':;',\\[\\].<>/?~！@#￥%……&*（）+{}【】‘；：”“’。， 、？] \\s";
            tagName = tagName.replaceAll(regEx, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tagName;
    }

    protected List<DBVal> addNullValues(List<DBVal> dbValList, String tagName, long start, long end, int step) {
        Hashtable<Long, DBVal> dbValHashtable = new Hashtable<>();
        for (DBVal dbVal : dbValList) {
            dbValHashtable.put(dbVal.getUtcTime(), dbVal);
        }
        ArrayList<DBVal> result = new ArrayList<>();

        for (long time = start; time <= end; time += step) {
            DBVal dbVal = dbValHashtable.get(time);
            if (dbVal == null) {
                dbVal = new DBVal();
                dbVal.setUtcTime(time);
                dbVal.setMetricName(tagName);
                dbVal.setValue(VALUE_INVALID);
            }
            result.add(dbVal);
        }
        return result;
    }

    /**
     * 按指定大小，分隔集合，将集合按规定个数分为n个部分
     *
     * @param dbValList
     * @return
     * @date
     */
    protected static List<List<DBVal>> splitDataList(List<DBVal> dbValList, int splitSize) {
        if (dbValList == null || dbValList.size() == 0 || splitSize < 1) {
            return null;
        }

        List<List<DBVal>> resultList = new ArrayList<>();

        if (dbValList.size() <= splitSize) {
            resultList.add(dbValList);
            return resultList;
        }

        int size = dbValList.size();
        int count = (size + splitSize - 1) / splitSize;

        for (int i = 0; i < count; i++) {
            List<DBVal> subList = dbValList.subList(i * splitSize, (Math.min((i + 1) * splitSize, size)));
            resultList.add(subList);
        }
        return resultList;
    }

    protected static List<PointVals> splitPointVals(PointVals pointVals, int splitSize) {
        if (pointVals == null || pointVals.isEmpty() || splitSize < 1) {
            return null;
        }

        List<PointVals> resultList = new ArrayList<>();

        if (pointVals.getCount() <= splitSize) {
            resultList.add(pointVals);
            return resultList;
        }

        int size = pointVals.getCount();
        int count = (size + splitSize - 1) / splitSize;

        for (int i = 0; i < count; i++) {
            PointVals subPointVals = pointVals.subPointVals(i * splitSize, (Math.min((i + 1) * splitSize, size)));
            resultList.add(subPointVals);
        }
        return resultList;
    }


    /**
     * 配套插入大型数据集的json生成
     * 单点多条数据
     */
    protected static String convertPointValsToImportJson(PointVals pointVals, String metricNamePrefix) {
        JSONObject metrics = new JSONObject();
        JSONObject metric = new JSONObject();
        JSONArray value1s = new JSONArray();
        JSONArray value2s = new JSONArray();
        JSONArray timestamps = new JSONArray();
            metric.put("__name__", pointVals.getFinalMetricName(metricNamePrefix));
            metric.putAll(pointVals.getTags());
            for (int index=0;index<pointVals.getCount();index++) {
                value1s.add(pointVals.getValue1(index));
                value2s.add(pointVals.getValue2(index));
                timestamps.add(pointVals.getUtcTime(index));
            }
            metrics.put("metric", metric);
            metrics.put("timestamps", timestamps);
            metrics.put("value1s", value1s);
            metrics.put("value2s", value2s);
        return metrics.toJSONString();
    }

    public static String convertDBValToImportJson(String metricNamePrefix, DBVal val) {
        JSONObject metrics = new JSONObject();
        JSONObject metric = new JSONObject();
        JSONArray values = new JSONArray();
        JSONArray timestamps = new JSONArray();
        metric.put("__name__", val.getFinalMetricName(metricNamePrefix));
        metric.putAll(val.getTags());
        values.add(val.getValue());
        timestamps.add(val.getUtcTimeMilliSeconds());
        metrics.put("metric", metric);
        metrics.put("timestamps", timestamps);
        metrics.put("values", values);
        return metrics.toJSONString();
    }

    /**
     * @date
     * 写入多点多条数据
     * 被sendMultiPoint调用
     */
    protected int splitAndInsertMultiPoint(List<DBVal> dbVals) {
        try {
            int counts = 0;
            StringBuilder sentence = new StringBuilder(50000);
            String item = "";
            for (DBVal val : dbVals) {
                item = convertDBValToImportJson(this.getMetricNamePrefix(), val);
                sentence.append(item).append("\n");
                counts++;
                if (counts > splitSize) {
                    insertByHttp(sentence.toString());
                    counts = 0;
                    sentence.setLength(0);
                }
            }
            //将不够长的一起发出去
            if (sentence.length() != 0) {
                insertByHttp(sentence.toString());
            }
//            sentence.setLength(0);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        return 1;
    }

    /**
     * 本方法用于数据插入
     *
     * @param sentences String类型,json格式的数据,组装好之后直接作为参数传入
     * @return void
     * @date
     * sendMultiPoint最终调用
     */
    protected void insertByHttp(String sentences) {
        try {
//            String sendDataUrl = String.format("http://%s:%d/insert/%d/%s/api/v1/import",
//                    host, insertPort, nameSpace, Constants.ROUTE_PREFIX);
            String sendDataUrl = String.format("http://%s:%d/api/v1/import", host, insertPort);
            System.out.println(sendDataUrl);
            URL realUrl = new URL(sendDataUrl);
            URLConnection conn = realUrl.openConnection();
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("host", "application/x-www-form-urlencoded");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            dos.write(sentences.getBytes());
            dos.flush();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @date
     * 被sendSinglePoint调用
     */
    protected int splitAndInsertSinglPoint(PointVals pointVals) {
        if (pointVals.isEmpty()) {
            return 0;
        }
        int updateCount = 0;
        try {
            List<PointVals>  pointValsList = splitPointVals(pointVals, this.splitSize);
            for (PointVals subPointVals : pointValsList) {
                SendRequest.histDataImport(host, insertPort, nameSpace, subPointVals, this.metricNamePrefix);
                updateCount += subPointVals.getCount();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return updateCount;
    }

    public int insertSinglPoint(PointVals pointVals) {
        return this.splitAndInsertSinglPoint(pointVals);
    }

    /**
     * 写入多点的多条数据
     */
    public int insertMultiPoint(List<DBVal> dbValList) {
        return this.splitAndInsertMultiPoint(dbValList);
    }


    /**
     * getRTValueList调用
     */
    protected DBVal getPointValueAtUtc(Point point, long utcTimeMilliSeconds) {
        String queryUrl = String.format("http://%s:%d/select/%d/%s/api/v1/query?query=%s&time=%f",
        host, selectPort, nameSpace, Constants.ROUTE_PREFIX, point.calPointName(metricNamePrefix), utcTimeMilliSeconds/1000.0);
        DBVal dbVal = null;
        try {
            dbVal = SendRequest.getDataAtUtc(queryUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(dbVal==null) {
            dbVal = new DBVal(point, utcTimeMilliSeconds);
            dbVal.setValueStatusInValid();
        }
        return dbVal;
    }

    /**
     * 被getRTValueList调用
     */
    protected List<DBVal> getPointValueListAtUtc(List<Point> points, long utcTimeMilliSeconds) {
        List<DBVal> result = new LinkedList<>();
        for (Point point : points) {
            result.add(this.getPointValueAtUtc(point, utcTimeMilliSeconds));
        }
        return result;
    }

    /**
     * 被getHistSnap调用
     */
    protected List<DBVal> getDataInTimeRangeWithStep(Point point, long start, long end, long step) {
        List<DBVal> dbValList = new ArrayList<>();
        String queryUrl = String.format("http://%s:%d/select/%d/%s/api/v1/query_range?query=%s&start=%f&end=%f&step=%dms",
                host, selectPort, nameSpace, Constants.ROUTE_PREFIX, point.calPointName(this.metricNamePrefix),
                start/1000.0, end/1000.0, step);
        try {
            dbValList = SendRequest.getDataInTimeRange(queryUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dbValList;
    }

    /**
     * 被getHistRaw调用
     */
    protected List<DBVal> getDataInTimeRange(Point point, long start, long end) {
        List<DBVal> dbValList = new ArrayList<>();
//        String queryUrl = String.format("http://%s:%d/select/%d/%s/api/v1/export?match[]=%s&start=%f&end=%f",
//                host, selectPort, nameSpace, Constants.ROUTE_PREFIX,
//                point.calMatchPointName(this.metricNamePrefix), start/1000.0, end/1000.0);
        String queryUrl = String.format("http://%s:%d/api/v1/export?match[]=%s&start=%f&end=%f",
                host, selectPort, point.calMatchPointName(this.metricNamePrefix), start/1000.0, end/1000.0);
        System.out.println(queryUrl);
        try {
            dbValList = SendRequest.getDataInTimeRangeRaw(queryUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dbValList;
    }

    public DBVal getPointValueAtTime(Point point, long time) {
        return this.getPointValueAtUtc(point, time);
    }

    public List<DBVal>  getPointsValuesAtTime(List<Point> pointList, long time) {
        return this.getPointValueListAtUtc(pointList, time);
    }

    public DBVal getRTValue(Point point) {
        DBVal dbVal = null;
        long currentTimeMillis = System.currentTimeMillis();
        dbVal = getPointValueAtUtc(point,  currentTimeMillis);
        if (dbVal == null) {
            dbVal = new DBVal(point, currentTimeMillis);
            dbVal.setValueStatusInValid();
        }
        return dbVal;
    }

     public List<DBVal> getRTValueList(List<Point> points) {
        List<DBVal> dbValList;
        dbValList = getPointValueListAtUtc(points, System.currentTimeMillis());
        return dbValList;
    }

    /**
     * 获取单点历史数据，指定步长
     */
    public List<DBVal> getHistSnap(Point point, long start, long end, long step) {
        List<DBVal> dbValList;
        dbValList = getDataInTimeRangeWithStep(point, start, end, step);
        return dbValList;
    }

    /**
     * 获取多点历史数据，指定步长
     */
    public Map<Point, List<DBVal>> getHistSnap(List<Point> pointList, long start, long end, long step) {
        Map<Point, List<DBVal>> dbValMapListList = new HashMap<>();
        if (pointList == null || pointList.isEmpty()) {
            return dbValMapListList;
        }

        for (Point point : pointList) {
            List<DBVal> result = this.getHistSnap(point, start, end, step);
            dbValMapListList.put(point, result);
        }

        return dbValMapListList;
    }

    /**
     * 查询单点时间段内所有值
     */
    public List<DBVal> getHistRaw(Point point, long start, long end) {
        List<DBVal> dbValList;
        dbValList = getDataInTimeRange(point, start, end);
        return dbValList;
    }


    /**
     * 查询多点时间段内所有值
     */
    public Map<Point, List<DBVal>> getHistRaw(List<Point> pointList, long start, long end) {
        Map<Point, List<DBVal>> dbValMapListList = new HashMap<>();
        if (pointList == null || pointList.isEmpty()) {
            return dbValMapListList;
        }
        dbValMapListList = getDataAtTimeRangeMap(pointList, start, end);
        return dbValMapListList;
    }

    /**
     * 被getHistRaw调用
     */
    protected Map<Point, List<DBVal>> getDataAtTimeRangeMap(List<Point> pointList, long start, long end) {
        Map<Point, List<DBVal>> dbValMapList = new HashMap<>();
        String queryUrl = String.format("http://%s:%d/select/%d/%s/api/v1/export",
                this.host, this.selectPort, this.nameSpace, Constants.ROUTE_PREFIX);

        try {
            dbValMapList = SendRequest.getDataInTimeRangeRawMap(queryUrl, this.metricNamePrefix, pointList,
                    start, end);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dbValMapList;
    }

    public DBVal getMinValue(Point point, long startTime, long endTime) {
        return calAggOverTime(point, "min_over_time", startTime, endTime);
    }

    public DBVal getMedianValue(Point point, long startTime, long endTime) {
        return calAggOverTime(point, "median_over_time", startTime, endTime);
     }

    public DBVal getStddevValue(Point point, long startTime, long endTime) {
        return calAggOverTime(point, "stddev_over_time", startTime, endTime);
     }

    public DBVal getStdvarValue(Point point, long startTime, long endTime) {
        return calAggOverTime(point, "stdvar_over_time", startTime, endTime);
     }

    public DBVal getSumValue(Point point, long startTime, long endTime) {
        return calAggOverTime(point, "sum_over_time", startTime, endTime);
     }

    protected DBVal calAggOverTime(Point point, String aggMethod, long startTime, long endTime) {
        if (startTime > endTime) {
            return null;
        }
        DBVal dbVal = null;
        try {
            long utcTime = System.currentTimeMillis();
            long offset = utcTime - endTime;
            long timeRange = endTime - startTime;

            String queryUrl = String.format("http://%s:%d/select/%d/%s/api/v1/query?query=%s(%s[%dms])&time=%f",
                    host, selectPort, nameSpace, Constants.ROUTE_PREFIX, aggMethod,
                    point.calPointName(this.metricNamePrefix), timeRange, endTime/1000.0);
//            String queryUrl = String.format("http://%s:%d/select/%d/%s/api/v1/query?query=%s(%s[%dms] offset %dms)",
//                    host, selectPort, nameSpace, Constants.ROUTE_PREFIX, aggMethod,
//                    point.calPointName(this.metricNamePrefix), timeRange, offset);
            queryUrl = queryUrl.replace(" ", "%20");
            dbVal = SendRequest.getDataAtUtc(queryUrl); // equal to return DBval
            if (dbVal == null) {
                dbVal = new DBVal();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return dbVal;
    }

    public DBVal getMaxValue(Point point, long startTime, long endTime) {
        return calAggOverTime(point, "max_over_time", startTime, endTime);
    }

    public DBVal getAvgValue(Point point, long startTime, long endTime) {
        return calAggOverTime(point, "avg_over_time", startTime, endTime);
    }

    protected int getSplitSize() {
        return splitSize;
    }

    protected void setSplitSize(int splitSize) {
        this.splitSize = splitSize;
    }

    protected int getOneThreadDataSize() {
        return oneThreadDataSize;
    }

    protected void setOneThreadDataSize(int oneThreadDataSize) {
        this.oneThreadDataSize = oneThreadDataSize;
    }

    //admin
    public String getStatus() {
        String response = "";
        try {
            String url = String.format("http://%s:%d/select/%d/%s/api/v1/status/tsdb",
                    host, selectPort, nameSpace, Constants.ROUTE_PREFIX);
            url = url.replace(" ", "%20");
            response = SendRequest.getResponse(url);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return response;

    }
}
