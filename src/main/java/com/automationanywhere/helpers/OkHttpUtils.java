package com.automationanywhere.helpers;

import com.automationanywhere.objects.ResponseObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
/**
 * @author Chiranjjeevi Vijayakumar (CJ)
 *
 */
public class OkHttpUtils {
    /**
     * This method initiates OkHttpClient call and execute the request
     *
     * @param request : Accepts okhttp3.Request object
     * @throws IOException
     * @throws InterruptedException
     */
    public static ResponseObject upload(Request request) throws IOException, InterruptedException{

        String response = "";
        Integer code = -1;

        /*Default values
        * ConnectionTimeout = 10secs
        * ReadTimeout = 10secs
        * WriteTimeout = 10secs
        */
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        Response res = client.newCall(request).execute();

        code = res.code();
        response = res.body().string();

        res.close();

        return new ResponseObject(code, response);
    }
}
