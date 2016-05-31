package com.xhj.pm;

public interface Constant {
	public static final String HOST="http://106.39.38.21:8081/Project/";
	//public static final String HOST="http://192.168.2.107/Project/";
	public static final String API_BASE_URL=HOST+"Api/";
	public static final String API_USER_BASE_URL=API_BASE_URL+"User/";
	public static final String USER_API_URL=API_USER_BASE_URL+"api.aspx";
	
	public static final String PAGES_BASE_URL=HOST+"Pages/";
	public static final String MOBILE_PAGES_BASE_URL=PAGES_BASE_URL+"Mobile/";
	
	public static final int RESULT_STATUS_ERROR=1;
	public static final int RESULT_STATUS_OK=0;
	public static final String JSON_TAG_RESULT_STATUS="status";
	public static final String JSON_TAG_RESULT_DATA="data";
	public static final String JSON_TAG_RESULT_INFO="info";
	public static final String UTF8="UTF-8";
}
