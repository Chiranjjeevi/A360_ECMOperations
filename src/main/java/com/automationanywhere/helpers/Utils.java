/*
 * Copyright (c) 2020 Automation Anywhere.
 * All rights reserved.
 *
 * This software is the proprietary information of Automation Anywhere.
 * You shall use it only in accordance with the terms of the license agreement
 * you entered into with Automation Anywhere.
 */
package com.automationanywhere.helpers;


import com.automationanywhere.objects.ResponseObject;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
/**
 * @author Fayaz Mohammed
 *
 */
public class Utils {

    public static String escape(String text) {
        text = text.replace("\\", "\\\\");
        text = text.replace("\"", "\\\"");
        text = text.replace("\b", "\\b");
        text = text.replace("\f", "\\f");
        text = text.replace("\n", "\\n");
        text = text.replace("\r", "\\r");
        text = text.replace("\t", "\\t");
        text = text.replace("/", "\\/");
        return text;
    }
    public static String getPropertyValue(String response, String property) {
        try {
            DocumentContext context = JsonPath.parse(response);
            return context.read(String.format("$.%s", property)).toString();
        }
        catch(Exception e) {
            return response;
        }
    }
    public static String getErrorDetails(ResponseObject responseObject) {
        return responseObject.getCode() + " - " + getPropertyValue(responseObject.getBody(), "message");
    }
}