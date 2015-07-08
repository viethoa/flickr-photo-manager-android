package us.originally.sgparkwhere.managers;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.lorem_ipsum.requests.BaseRequest;
import com.lorem_ipsum.requests.MyDataCallback;

import java.lang.reflect.Type;
import java.util.ArrayList;

import us.originally.sgparkwhere.models.Park;
import us.originally.sgparkwhere.models.ResponseToken;

public class ApiManager extends BaseRequest {

    public static void getToken(final Context context, final MyDataCallback<ResponseToken> myCallback) {
        Type type = new TypeToken<ResponseToken>() {
        }.getType();
        String url = "parkwhere/startapi";
        GET(context, url, null, type, null, myCallback);
    }

    public static void getCarParkList(final Context context, final MyDataCallback<ArrayList<Park>> myCallback) {
        Type type = new TypeToken<ArrayList<Park>>() {
        }.getType();
        String url = "parkwhere/getcarpark/";
        GET(context, url, null, type, null, myCallback);
    }
}