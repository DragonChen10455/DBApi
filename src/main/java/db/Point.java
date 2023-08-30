package db;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Objects;
import java.util.Vector;

public class Point {
    private String metricName; // 数值型度量名
    private HashMap<String, String> tags;

    public Point(String metricName, HashMap<String, String> tags) {
        this.metricName = metricName;
        this.tags = tags;
    }

    public Point() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return metricName.equals(point.metricName) && tags.equals(point.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(metricName, tags);
    }

    public static Point fromJSONObject(JSONObject jsonObject) {
        Point point = new Point();
        HashMap<String, String> tags = new HashMap<>();
        point.setMetricName(jsonObject.getString("__name__"));

        for (String tagName : jsonObject.keySet()) {
            if(tagName.equals("__name__")) continue;
            tags.put(tagName, jsonObject.get(tagName).toString());
        }
        point.setTags(tags);
        return point;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public HashMap<String, String> getTags() {
        return tags;
    }

    public void setTags(HashMap<String, String> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "Point{" +
                "metricName='" + metricName + '\'' +
                ", tags=" + tags +
                '}';
    }

    public String calPointName(String metricNamePrefix) {
        StringBuffer sb = new StringBuffer();
        sb.append(metricNamePrefix);
        sb.append(this.getMetricName());
        sb.append("{");

        String tagName, tagValue;
        Vector<String> tagNames = new Vector<>();
        tagNames.addAll(this.getTags().keySet());
        for(int i=0;i<tagNames.size();i++) {
            if(i!=0) {
                sb.append(",");
            }

            tagName = tagNames.elementAt(i);
            tagValue = this.getTags().get(tagName);
            sb.append(String.format("%s=\"%s\"", tagName, tagValue));
        }
        sb.append("}");
        return sb.toString();
    }

    /*
     * {__name__=\"%s\",pointName=\"%s\"," +
                        "status=\"%d\"}
     */
    public String calMatchPointName(String metricNamePrefix) {
        StringBuffer sb = new StringBuffer();
        sb.append("{");
//        sb.append(String.format("%s=\"%s\"", "__name__", metricNamePrefix+this.getMetricName()));
        sb.append(String.format("%s=~\"%s\"", "__name__", metricNamePrefix+this.getMetricName()));

        String tagName, tagValue;
        Vector<String> tagNames = new Vector<>();
        tagNames.addAll(this.getTags().keySet());
        for(int i=0;i<tagNames.size();i++) {
            tagName = tagNames.elementAt(i);
            tagValue = this.getTags().get(tagName);
            sb.append(",");
            sb.append(String.format("%s=\"%s\"", tagName, tagValue));
        }
        sb.append("}");
        return sb.toString();
    }


}
