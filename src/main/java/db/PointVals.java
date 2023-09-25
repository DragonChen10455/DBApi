package db;

import java.io.Serializable;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

public class PointVals implements Serializable {
	private static final long serialVersionUID = 1L;
	private Point point;
	private int count;
	private long[] utcTimes; // 数值型实时UTC时间
	private double[] values; // 数值型实时数值
	private double[] value1s; // 数值型实时数值
	private double[] value2s; // 数值型实时数值
	private int beginIndex = 0;

	public PointVals(Point point, int count, long[] utcTimes, double[] value1s, double[] value2s) {
		this.point = point;
		this.count = count;
		this.utcTimes = utcTimes;
		this.value1s = value1s;
		this.value2s = value2s;
	}

	public PointVals(Point point, int count, long[] utcTimes, double[] values) {
		this.point = point;
		this.count = count;
		this.utcTimes = utcTimes;
		this.values = values;
	}

	public PointVals() {}

	public String getMetricName() {
		return this.point.getMetricName();
	}

	public void setMetricName(String metricName) {
		this.point.setMetricName(metricName);
	}

	public void setBeginIndex(int beginIndex) {
		this.beginIndex = beginIndex;
	}

	public HashMap<String, String> getTags() {
		return this.point.getTags();
	}

	public void setTags(HashMap<String, String> tags) {
		this.point.setTags(tags);
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public long getUtcTime(int index) {
		return this.utcTimes[beginIndex+index];
	}

	public double getValue(int index) {
		return this.values[beginIndex+index];
	}

	public double getValue1(int index) {
		return this.value1s[beginIndex+index];
	}

	public double getValue2(int index) {
		return this.value2s[beginIndex+index];
	}
	public String getFinalMetricName(String metricNamePrefix) {
		return metricNamePrefix + this.getMetricName();
	}

	public boolean isEmpty() {
		return count==0;
	}

	// 2维数据
	public PointVals subPointVals(int begin, int end) {
		int count = end-begin;
		PointVals resultPointVals = new PointVals(this.point, count, this.utcTimes, this.value1s, this.value2s);
		resultPointVals.setBeginIndex(begin);
		return resultPointVals;
	}

//	// 1维数据
//	public PointVals subPointVals(int begin, int end) {
//		int count = end-begin;
//		PointVals resultPointVals = new PointVals(this.point, count, this.utcTimes, this.values);
//		resultPointVals.setBeginIndex(begin);
//		return resultPointVals;
//	}
}

