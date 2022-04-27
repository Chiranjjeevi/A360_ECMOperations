/*
 * Copyright (c) 2020 Automation Anywhere.
 * All rights reserved.
 *
 * This software is the proprietary information of Automation Anywhere.
 * You shall use it only in accordance with the terms of the license agreement
 * you entered into with Automation Anywhere.
 */
package com.automationanywhere.botcommand;

import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.*;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.core.security.SecureString;
import com.automationanywhere.helpers.HttpUtils;
import com.automationanywhere.helpers.Utils;
import com.jayway.jsonpath.JsonPath;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static com.automationanywhere.commandsdk.model.AttributeType.CREDENTIAL;
import static com.automationanywhere.commandsdk.model.AttributeType.TEXT;
import static com.automationanywhere.commandsdk.model.DataType.STRING;

/**
 * @author Fayaz Mohammed
 *
 */
@BotCommand
@CommandPkg(name = "connect", label = "[[Connect.label]]",
        node_label = "[[Connect.node_label]]", description = "[[Connect.description]]", icon = "pkg.svg")

public class Connect {
    private static Logger logger = LogManager.getLogger(Connect.class);

    @Sessions
    private Map<String, Object> sessionMap;

    @Execute
    public void execute(
            @Idx(index = "1", type = TEXT)
            @Pkg(label = "[[SessionName.label]]", description = "[[SessionName.description]]", default_value_type = STRING, default_value = "Default")
            @NotEmpty
                    String sessionName,

            @Idx(index = "2", type = TEXT)
            @Pkg(label = "[[Connect.ecmURL.label]]", description = "[[Connect.ecmURL.description]]")
            @NotEmpty
                    String ecmAuthUrl,

            @Idx(index = "3", type = CREDENTIAL)
            @Pkg(label = "[[Connect.username.label]]")
            @NotEmpty
                    SecureString username,

            @Idx(index = "4", type = CREDENTIAL)
            @Pkg(label = "[[Connect.password.label]]")
            @NotEmpty
                    SecureString password) throws IOException, InterruptedException {

        if (sessionName == null || "".equals(sessionName.trim()))
            throw new BotCommandException("Please enter session name");

        if (username == null || "".equals(username.getInsecureString().trim()))
            throw new BotCommandException("Please enter username");

        if (password == null || "".equals(password.getInsecureString().trim()))
            throw new BotCommandException("Please enter either password");

        if (ecmAuthUrl == null || "".equals(ecmAuthUrl.trim()))
            throw new BotCommandException("Please provide ECM Authentication API");

        if (sessionMap != null && sessionMap.containsKey(sessionName))
            throw new BotCommandException("Session name already exists");

        try {

            String usernameVal = username.getInsecureString();
            String passwordVal = password.getInsecureString();

            var request = "username="+usernameVal+"&password="+passwordVal;

            var url = new URL(ecmAuthUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");

            var response = HttpUtils.execute(con, request.toString());
            if (response.getCode() != HttpURLConnection.HTTP_OK) {
                throw new BotCommandException(Utils.getErrorDetails(response));
            }

            String ticket = JsonPath.parse(response.getBody()).read("$.ticket");
            ticket = ticket.replace("\\","");

            /** Session Initialize */
            Map<String, Object> sessionValues = new HashMap<>();
            sessionValues.put("SessionName", sessionName);
            sessionValues.put("Ticket", ticket);

            sessionMap.put(sessionName, sessionValues);


        } catch (Exception e) {
            throw new BotCommandException("Error occurred while ECM Connect: " + e.getMessage());
        }
    }

    public void setSessionMap(Map<String, Object> sessionMap) {
        this.sessionMap = sessionMap;
    }

}
