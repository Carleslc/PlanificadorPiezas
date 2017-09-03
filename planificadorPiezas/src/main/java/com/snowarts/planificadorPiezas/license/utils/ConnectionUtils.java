package com.snowarts.planificadorPiezas.license.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import me.carleslc.serialnumber.Hardware;

public class ConnectionUtils {
	
    public static String getParamsString(Map<String, String> params) {
    	try {
	        StringBuilder result = new StringBuilder();
	 
	        for (Map.Entry<String, String> entry : params.entrySet()) {
	          result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
	          result.append("=");
	          result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
	          result.append("&");
	        }
	 
	        String resultString = result.toString();
	        return resultString.length() > 0
	          ? "?" + resultString.substring(0, resultString.length() - 1)
	          : resultString;
    	} catch (UnsupportedEncodingException e) {
    		throw new RuntimeException(e.getMessage(), e);
    	}
    }
    
    public static String getFingerprintParameters() {
		Map<String, String> parameters = new HashMap<>();
		parameters.put("lang", "es");
		parameters.put("fingerprint", Hardware.getSerialNumber());
		return ConnectionUtils.getParamsString(parameters);
	}
    
    public static void connect(String url, OnConnectedCallback onConnected, OnTimeoutCallback onTimeout) throws IOException {
    	try {
			HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("Content-Type", "application/json");
			con.setConnectTimeout(10000);
			con.setReadTimeout(10000);
			
			onConnected.onConnected(con);
    	} catch (SocketTimeoutException e) {
			onTimeout.onTimeout(e);
		}
    }
    
    public static String read(BufferedReader in) throws IOException {
		String inputLine;
		StringBuilder builder = new StringBuilder();
		while ((inputLine = in.readLine()) != null) {
			builder.append(inputLine);
		}
		in.close();
		return builder.toString();
	}
    
    @FunctionalInterface
    public interface OnConnectedCallback {
    	void onConnected(HttpURLConnection con) throws IOException;
    }
    
    @FunctionalInterface
    public interface OnTimeoutCallback {
    	void onTimeout(SocketTimeoutException e);
    }
}
