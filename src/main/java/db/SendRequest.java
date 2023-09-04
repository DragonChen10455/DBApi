package db;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class SendRequest {
	private static String charSet = "utf-8";
	public static final String QUERY_HOST = "query.org";
	public static final String INSERT_HOST = "insert.org";
	public static final String HOST_HEADER = "host";

	public static DBVal getDataAtUtc(String url) throws Exception {
		HttpURLConnection conn = (HttpURLConnection)(new URL(url)).openConnection();
		conn.setConnectTimeout(5000);
		setConnHost(conn, QUERY_HOST);
		conn.setRequestMethod("GET");
		conn.connect();
		if (conn.getResponseCode() != 200) {
			return null;
		}

		InputStream inputStream = conn.getInputStream();
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream, charSet);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		String str = null;
		while ((str = bufferedReader.readLine()) != null) {

			JSONObject res = JSONObject.parseObject(str);
			JSONArray result = res.getJSONObject("data").getJSONArray("result");
			if (result.size() == 0)
				return null;

			int index = 0; if (index < result.size()) {
				JSONObject object = result.getJSONObject(index);
				JSONObject metricInfo = object.getJSONObject("metric");
				Point point = Point.fromJSONObject(metricInfo);
				JSONArray value = object.getJSONArray("value");
				DBVal dbVal = new DBVal();
				dbVal.setPoint(point);
				dbVal.setUtcTimeMilliSeconds((long)(value.getDoubleValue(0)*1000));
				dbVal.setValue(Double.parseDouble(value.getString(1)));
				return dbVal;
			}
		}
		return null;
	}

	public static void histDataImport(String host, int port, int nameSpace, PointVals pointVals, String metricNamePrefix) {
		histDataImportJsonLine(host, port, nameSpace, DBApiEntry.convertPointValsToImportJson(pointVals, metricNamePrefix));
	}

	/*
	多点的历史数据导入，content不以换行符分割
	 */
	public static void histDataImportJsonLine(String host, int port, int nameSpace, String content) {
		System.out.println("import content:" + content);
		String result = "";
		try {
//			host = String.format("http://%s:%d/insert/%d/%s/api/v1/import",
//					host, port, nameSpace, Constants.ROUTE_PREFIX);
			host = String.format("http://%s:%d/api/v1/import", host, port);
			System.out.println("import host:" + host);
			URL realUrl = new URL(host);
			HttpURLConnection conn = (HttpURLConnection)realUrl.openConnection();
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			setConnHost(conn, INSERT_HOST);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setReadTimeout(0);
			conn.connect();

			DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
			dos.write(content.getBytes());
			dos.flush();
			dos.close();

			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			conn.disconnect();
			//TODO
		}
		catch (Exception e) {
			System.out.println("Exception" + e.getMessage());
			e.printStackTrace();
		}
	}

	public static void setConnHost(HttpURLConnection conn, String host) {
		conn.setRequestProperty(HOST_HEADER, host);
	}

	public static List<DBVal> getDataInTimeRange(String url) throws Exception {
		List<DBVal> dbValList = new ArrayList<>();
		HttpURLConnection conn = (HttpURLConnection)(new URL(url)).openConnection();
		conn.setConnectTimeout(5000);
		conn.setRequestMethod("GET");
		setConnHost(conn, QUERY_HOST);

		if (conn.getResponseCode() == 200) {

			InputStream inputStream = conn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, charSet);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				JSONObject res = JSONObject.parseObject(str);
				JSONArray result = res.getJSONObject("data").getJSONArray("result");
				if (result.size() == 0)
					return dbValList;
				for (int i = 0; i < result.size(); i++) {
					JSONObject object = result.getJSONObject(i);
					JSONArray values = object.getJSONArray("values");
					JSONObject metricInfo = object.getJSONObject("metric");
					Point point = Point.fromJSONObject(metricInfo);

					for (int j = 0; j < values.size(); j++) {
						JSONArray value = values.getJSONArray(j);
						DBVal dbVal = new DBVal();
						dbVal.setPoint(point);
						dbVal.setUtcTimeMilliSeconds((long)(value.getDoubleValue(0)*1000));
						dbVal.setValue(Double.parseDouble(value.getString(1)));
						dbValList.add(dbVal);
					}
				}
			}
		}
		return dbValList;
	}

	public static Map<Point, List<DBVal>> getDataInTimeRangeRawMap(String url, String metricNamePrefix,
																	List<Point> pointList,
																	long start, long end) throws Exception {
		Map<Point, List<DBVal>>  result = new HashMap<>();
		HttpURLConnection conn = (HttpURLConnection)(new URL(url)).openConnection();
		conn.setRequestProperty("Connection", "Keep-Alive");
		conn.setConnectTimeout(5000);
		conn.setRequestProperty("accept", "*/*");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setDoInput(true);

		StringBuffer body = new StringBuffer();
		for (int i=0;i<pointList.size();i++) {
			if(i==0) {
				body.append(String.format("match[]=%s", pointList.get(i).calMatchPointName(metricNamePrefix)));
			}
			else {
				body.append(String.format("&match[]=%s", pointList.get(i).calMatchPointName(metricNamePrefix)));
			}
		}
		body.append(String.format("&start=%f", start/1000.0));
		body.append(String.format("&end=%f", end/1000.0));
		conn.connect();

		BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
		bufferedWriter.write(body.toString());
		bufferedWriter.flush();
		bufferedWriter.close();

		if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
			InputStream inputStream = conn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, charSet);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				JSONObject res = JSONObject.parseObject(str);
				JSONObject metricInfo = res.getJSONObject("metric");
				Point point = Point.fromJSONObject(metricInfo);
				JSONArray values = res.getJSONArray("values");
				JSONArray timestamps = res.getJSONArray("timestamps");
				List<DBVal> pointDBVals = new ArrayList<>();
				for (int j = 0; j < values.size(); j++) {
					DBVal dbVal = new DBVal();
					dbVal.setPoint(point);
					dbVal.setUtcTimeMilliSeconds(timestamps.getLongValue(j));
					dbVal.setValue(values.getDoubleValue(j));
					pointDBVals.add(dbVal);
				}
				result.put(point, pointDBVals);
			}
		}

		for (Point point : pointList) {
			if(!result.containsKey(point)) {
				result.put(point, new ArrayList<>());
			}
		}
		return result;
	}


	public static List<DBVal> getDataInTimeRangeRaw(String url) throws Exception {
		List<DBVal> dbValList = new ArrayList<>();
		HttpURLConnection conn = (HttpURLConnection)(new URL(url)).openConnection();
		conn.setRequestMethod("GET");
		setConnHost(conn, QUERY_HOST);
		conn.setConnectTimeout(5000);
		conn.connect();

		if (conn.getResponseCode() == 200) {
			InputStream inputStream = conn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, charSet);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String str = null;

			while ((str = bufferedReader.readLine()) != null) {
				JSONObject res = JSONObject.parseObject(str);
				JSONObject metricInfo = res.getJSONObject("metric");
				Point point = Point.fromJSONObject(metricInfo);
				JSONArray values = res.getJSONArray("values");
				JSONArray timestamps = res.getJSONArray("timestamps");
				SortedMap<Long, DBVal> dbValMap = new TreeMap<>();
				for (int j = 0; j < values.size(); j++) {
					DBVal dbVal = new DBVal();
					dbVal.setPoint(point);
					dbVal.setUtcTimeMilliSeconds(timestamps.getLongValue(j));
					dbVal.setValue(values.getDoubleValue(j));
					dbValMap.put(dbVal.getUtcTimeMilliSeconds(), dbVal);
				}
				for (DBVal dbVal : dbValMap.values()) {
					dbValList.add(dbVal);
				}
			}
		}
		return dbValList;
	}

	public static String getResponse(String url) throws Exception {
		HttpURLConnection conn = (HttpURLConnection)(new URL(url)).openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(5000);
		conn.connect();

		StringBuffer sb = new StringBuffer(conn.getContentLength());

		if (conn.getResponseCode() == 200) {
			InputStream inputStream = conn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, charSet);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String str = null;

			while ((str = bufferedReader.readLine()) != null) {
				sb.append(str);
			}
		}
		return sb.toString();
	}

}
