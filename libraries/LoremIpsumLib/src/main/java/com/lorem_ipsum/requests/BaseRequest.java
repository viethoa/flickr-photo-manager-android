package com.lorem_ipsum.requests;

import android.content.Context;
import android.util.Base64;

import com.google.gson.JsonElement;
import com.lorem_ipsum.managers.UserSessionDataManager;
import com.lorem_ipsum.utils.AppUtils;
import com.lorem_ipsum.utils.RetrofitUtils;
import com.lorem_ipsum.utils.StringUtils;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.EncodedPath;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.QueryMap;
import retrofit.http.Streaming;

/**
 * @author Originally.US
 */
public class BaseRequest {

    private static final String LOG_TAG = "BaseRequest";

    public static final int DEVELOPMENT = 1;
    public static final int PRODUCTION = 2;

    // TODO hardcode
    private static final String BASIC_AUTH_USER_AND_PASS = "garlock:garl0ck";

    private static final String DEVELOPMENT_SERVER = "http://garlock.originally.us";
    private static final String PRODUCTION_SERVER = "https://garlock.originally.us";

    protected static final String GENERIC_URL = "/{url}";
    private static final String API_VERSION_STRING = "/v1";

    public static String getBaseUrl(int appStatus, boolean withPrefix) {
        StringBuilder builder = new StringBuilder();
        // get domain
        switch (appStatus) {
            case DEVELOPMENT:
                builder.append(DEVELOPMENT_SERVER);
                break;

            case PRODUCTION:
                builder.append(PRODUCTION_SERVER);
                break;
        }
        // get prefix
        if (withPrefix) {
            builder.append(API_VERSION_STRING);
        }
        return builder.toString();
    }


    // Request interface
    protected interface BaseRequestInterface {

        /**
         * Generic API
         */
        @GET(GENERIC_URL)
        void getGeneric(
                @EncodedPath("url") String url,
                @QueryMap Map<String, Object> params,
                Callback<JsonElement> callback);

        @PUT(GENERIC_URL)
        void putGeneric(
                @EncodedPath("url") String url,
                @Body Map<String, Object> params,
                Callback<JsonElement> callback);

        @POST(GENERIC_URL)
        void postGeneric(
                @EncodedPath("url") String url,
                @Body Map<String, Object> params,
                Callback<JsonElement> callback);

        @DELETE(GENERIC_URL)
        void deleteGeneric(
                @EncodedPath("url") String url,
                @QueryMap Map<String, Object> params,
                Callback<JsonElement> callback);


        //********************* Stream *********************

        @GET(GENERIC_URL)
        @Streaming
        void getStream(
                @EncodedPath("url") String url,
                @QueryMap Map<String, Object> params,
                Callback<Response> callback);

        @POST(GENERIC_URL)
        @Streaming
        void postStream(
                @EncodedPath("url") String url,
                @Body Map<String, Object> params,
                Callback<Response> callback);

        @POST(GENERIC_URL)
        @Streaming
        Response postStreamSynchronous(
                @EncodedPath("url") String url,
                @Body Map<String, Object> params);
    }

    /**
     * Convenient function to obtain the request interface
     * 'requestInterceptor' is for custom headers
     */
    protected static BaseRequestInterface getBaseRequestInterface(String url) {
        //Auto-detect if we should use prefix or not
        boolean usePrefix = !url.startsWith("/");
        if (!usePrefix)
            url.substring(1);

        //Custom header (authentication)
        HashMap<String, String> headers = getStandardHeaderParameters();
        RequestInterceptor requestInterceptor = getRequestInterceptor(headers);

        RestAdapter restAdapter = RetrofitUtils.getRestApdater(usePrefix, requestInterceptor);
        return restAdapter.create(BaseRequestInterface.class);
    }

    /**
     * Common method to construct standard parameters such as device parameters
     */
    public static HashMap<String, Object> getStandardParameters() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("os", "android");
        params.put("ver", AppUtils.getAppVersionName());
        return params;
    }

    /**
     * Common method to construct auth header parameters
     */
    public static HashMap<String, String> getStandardHeaderParameters() {
        HashMap<String, String> params = new HashMap<>();

        String accessToken = UserSessionDataManager.getCurrentAccessToken();
        if (accessToken != null)
            params.put("secret", accessToken);

        Number userId = UserSessionDataManager.getCurrentUserID();
        if (userId != null)
            params.put("userid", "" + userId);

        // TODO re-check: Basic Auth for debug
//        String basicAuth = encodeCredentialsForBasicAuthorization();
//        if (basicAuth != null) {
//            params.put("Authorization", basicAuth);
//        }

        //Return null if there is no parameters
        if (params.size() <= 0)
            params = null;
        return params;
    }

    private static String encodeCredentialsForBasicAuthorization() {
        if (StringUtils.isNull(BASIC_AUTH_USER_AND_PASS)) {
            return null;
        }
        return "Basic " + Base64.encodeToString(BASIC_AUTH_USER_AND_PASS.getBytes(), Base64.NO_WRAP);
    }

    /**
     * Helper method to construct the authenticated headers
     */
    private static RequestInterceptor getRequestInterceptor(final HashMap<String, String> headers) {
        if (headers == null || headers.size() <= 0)
            return null;

        //For the headers
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    request.addHeader(key, value);
                }
            }
        };

        return requestInterceptor;
    }

    /**
     * Convenient function to perform a HTTP GET
     */
    protected static void GET(final Context context, final String url, HashMap<String, Object> params, final Type theType, String dataWrapperElement, final MyDataCallback myCallback) {
        HashMap<String, Object> finalParams = getStandardParameters();
        if (params != null && params.size() > 0)
            finalParams.putAll(params);

        String functionName = Thread.currentThread().getStackTrace()[3].getMethodName();

        Callback<JsonElement> theCallback = JsonHelper.callbackJsonElement(context, myCallback, theType, dataWrapperElement, LOG_TAG, functionName);

        BaseRequestInterface requestInterface = getBaseRequestInterface(url);
        requestInterface.getGeneric(url, finalParams, theCallback);
    }

    /**
     * Convenient function to perform a HTTP PUT
     */
    protected static void PUT(final Context context, final String url, HashMap<String, Object> params, final Type theType, String dataWrapperElement, final MyDataCallback myCallback) {
        HashMap<String, Object> finalParams = getStandardParameters();
        if (params != null && params.size() > 0)
            finalParams.putAll(params);

        String functionName = Thread.currentThread().getStackTrace()[3].getMethodName();

        Callback<JsonElement> theCallback = JsonHelper.callbackJsonElement(context, myCallback, theType, dataWrapperElement, LOG_TAG, functionName);

        BaseRequestInterface requestInterface = getBaseRequestInterface(url);
        requestInterface.putGeneric(url, finalParams, theCallback);
    }

    /**
     * Convenient function to perform a HTTP POST
     */
    protected static void POST(final Context context, final String url, HashMap<String, Object> params, final Type theType, String dataWrapperElement, final MyDataCallback myCallback) {
        HashMap<String, Object> finalParams = getStandardParameters();
        if (params != null && params.size() > 0)
            finalParams.putAll(params);

        String functionName = Thread.currentThread().getStackTrace()[3].getMethodName();

        Callback<JsonElement> theCallback = JsonHelper.callbackJsonElement(context, myCallback, theType, dataWrapperElement, LOG_TAG, functionName);

        BaseRequestInterface requestInterface = getBaseRequestInterface(url);
        requestInterface.postGeneric(url, finalParams, theCallback);
    }

    /**
     * Convenient function to perform a HTTP POST
     */
    protected static void DELETE(final Context context, final String url, HashMap<String, Object> params, final Type theType, String dataWrapperElement, final MyDataCallback myCallback) {
        HashMap<String, Object> finalParams = getStandardParameters();
        if (params != null && params.size() > 0)
            finalParams.putAll(params);

        String functionName = Thread.currentThread().getStackTrace()[3].getMethodName();

        Callback<JsonElement> theCallback = JsonHelper.callbackJsonElement(context, myCallback, theType, dataWrapperElement, LOG_TAG, functionName);

        BaseRequestInterface requestInterface = getBaseRequestInterface(url);
        requestInterface.deleteGeneric(url, finalParams, theCallback);
    }

    /**
     * Convenient function to perform a HTTP GET with byte streaming
     */
    protected static void GET_STREAM(String url, HashMap<String, Object> params, Callback<Response> myCallback) {
        HashMap<String, Object> finalParams = getStandardParameters();
        if (params != null && params.size() > 0)
            finalParams.putAll(params);

        BaseRequestInterface requestInterface = getBaseRequestInterface(url);
        requestInterface.getStream(url, finalParams, myCallback);
    }

    /**
     * Convenient function to perform a HTTP POST with byte streaming
     */
    protected static void POST_STREAM(String url, HashMap<String, Object> params, Callback<Response> myCallback) {
        HashMap<String, Object> finalParams = getStandardParameters();
        if (params != null && params.size() > 0)
            finalParams.putAll(params);

        BaseRequestInterface requestInterface = getBaseRequestInterface(url);
        requestInterface.postStream(url, finalParams, myCallback);
    }

    /**
     * Convenient function to perform a HTTP POST with byte streaming
     */
    protected static Response POST_STREAM_SYNCHRONOUS(String url, HashMap<String, Object> params) {
        HashMap<String, Object> finalParams = getStandardParameters();
        if (params != null && params.size() > 0)
            finalParams.putAll(params);

        BaseRequestInterface requestInterface = getBaseRequestInterface(url);
        return requestInterface.postStreamSynchronous(url, finalParams);
    }

}
