package db;

import java.io.Serializable;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Vector;

public class DBVal implements Serializable {
	public enum ValueStatus {
		VALID, INVALID
	}

	private static final long serialVersionUID = 1L;
	private Point point;
	private long utcTime; // 数值型实时UTC时间，单位毫秒
	private double value; // 数值型实时数值
	private ValueStatus valueStatus = ValueStatus.VALID;

	public DBVal(Point point, long utcTime, double value) {
		this.point = point;
		this.utcTime = utcTime;
		this.value = value;
	}

	public DBVal(Point point, long utcTime) {
		this.point = point;
		this.utcTime = utcTime;
	}

	public DBVal() {}

	public String getMetricName() {
		return this.point.getMetricName();
	}

	public void setMetricName(String metricName) {
		this.point.setMetricName(metricName);
	}

	/*
	 * 返回时间戳，单位为秒
	 */
	public long getUtcTime() {
		return utcTime/1000;
	}

	/*
	 * 返回时间戳，单位为毫秒
	 */
	public long getUtcTimeMilliSeconds() {
		return utcTime;
	}

	/*
	 * 返回时间戳，单位为毫秒
	 */
	public void setUtcTimeMilliSeconds(long utcTime) {
		this.utcTime = utcTime;
	}

	/*
	 * utcTime 单位为秒
	 */
	public void setUtcTime(long utcTime) {
		this.utcTime = utcTime*1000;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public Point getPoint() {
		return point;
	}

	public void setPoint(Point point) {
		this.point = point;
	}

	public HashMap<String, String> getTags() {
		return this.point.getTags();
	}

	public void setTags(HashMap<String, String> tags) {
		this.point.setTags(tags);
	}

	public String getFinalMetricName(String metricNamePrefix) {
		return metricNamePrefix + this.point.getMetricName();
	}

	public boolean isValueValid() {
		return this.valueStatus.equals(ValueStatus.VALID);
	}

	public void setValueStatusInValid() {
		this.valueStatus = ValueStatus.INVALID;
	}

	public String toString() {
		try {
			return String.format("[metricName:%s tags:%s utcTime:%d(ms) date:%s value:%f valueStatus:%s]\n",
                    this.getMetricName(), this.getTags(), this.utcTime,
					Util.uTCMilliSecondsToDateStringWithMs(this.utcTime),
                    this.value,
					this.valueStatus.toString());
		} catch (ParseException e) {
			e.printStackTrace();
			return "";
		}
	}
}

