package com.automationanywhere.botcommand;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.*;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.helpers.HttpUtils;
import com.automationanywhere.helpers.Utils;
import com.jayway.jsonpath.JsonPath;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import static com.automationanywhere.commandsdk.model.AttributeType.TEXT;
import static com.automationanywhere.commandsdk.model.DataType.STRING;
/**
 * @author Chiranjjeevi Vijayakumar (CJ)
 *
 */

//BotCommand makes a class eligible for being considered as an action.
@BotCommand
//CommandPks adds required information to be displayable on GUI.
@CommandPkg(
        //Unique name inside a package and label to display.
        name = "Delete", label = "[[Delete.label]]",
        node_label = "[[Delete.node_label]]", description = "[[Delete.description]]", icon = "pkg.svg",

        //Return type information. return_type ensures only the right kind of variable is provided on the UI.
        return_label = "[[Delete.return_label]]", return_type = STRING, return_required = false)

public class Delete {
    private static Logger logger = LogManager.getLogger(Delete.class);

    @Sessions
    private Map<String, Object> sessionMap;

    @Execute
    public Value<String> action(

            @Idx(index = "1", type = TEXT)
            @Pkg(label = "[[SessionName.label]]", description = "[[SessionName.description]]", default_value_type = STRING, default_value = "Default")
            @NotEmpty
                    String sessionName,

            @Idx(index = "2", type = TEXT)
            @Pkg(label = "[[Delete.ecmURL.label]]", description = "[[Delete.ecmURL.description]]")
            @NotEmpty
                    String ecmDeleteURL) throws IOException, InterruptedException {

        //NULL Check
        if (sessionName == null || "".equals(sessionName.trim()))
            throw new BotCommandException("Please enter Session name");

        if (!sessionMap.containsKey(sessionName))
            throw new BotCommandException("There are no existing session");

        if (ecmDeleteURL == null || "".equals(ecmDeleteURL.trim()))
            throw new BotCommandException("Please enter ECM Delete URL");

        //Business Logic
        try {

            var sessionValues = (Map<String, Object>)sessionMap.get(sessionName);
            var ticket = sessionValues.get("Ticket").toString();

            var request = "";

            var url = new URL(ecmDeleteURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("DELETE");
            con.setRequestProperty("OTCSTICKET",ticket);

            var response = HttpUtils.execute(con, request.toString());
            if (response.getCode() != HttpURLConnection.HTTP_OK) {
                throw new BotCommandException(Utils.getErrorDetails(response));
            }

            String body = response.getBody().toString();

            //Return StringValue.
            return new StringValue(body);

        }
        catch (Exception e)
        { throw new BotCommandException("Error occurred while ECM Delete node: " + e.getMessage()); }

    }

    public void setSessionMap(Map<String, Object> sessionMap) {
        this.sessionMap = sessionMap;
    }
}
