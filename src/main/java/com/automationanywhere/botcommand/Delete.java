package com.automationanywhere.botcommand;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.*;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.helpers.HttpUtils;
import com.automationanywhere.helpers.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import static com.automationanywhere.commandsdk.model.AttributeType.GROUP;
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

    @Idx(index = "3", type = GROUP)
    @Pkg(label = "[[AuthGroup.label]]")
    String authGroup;

    @Execute
    public Value<String> action(

            @Idx(index = "1", type = TEXT)
            @Pkg(label = "[[SessionName.label]]", description = "[[SessionName.description]]", default_value_type = STRING, default_value = "Default")
            @NotEmpty
                    String sessionName,

            @Idx(index = "2", type = TEXT)
            @Pkg(label = "[[EcmURL.label]]", description = "[[Delete.ecmURL.description]]")
            @NotEmpty
                    String ecmDeleteURL,

            @Idx(index = "3.1", type = TEXT)
            @Pkg(label = "[[AuthName.label]]", description = "[[AuthName.description]]", default_value_type = STRING, default_value = "OTCSTICKET")
            @NotEmpty
                    String authKey) throws IOException, InterruptedException {

        //NULL Check
        if (sessionName == null || "".equals(sessionName.trim()))
            throw new BotCommandException("Please enter Session name");

        if (!sessionMap.containsKey(sessionName))
            throw new BotCommandException("There are no existing session");

        if (ecmDeleteURL == null || "".equals(ecmDeleteURL.trim()))
            throw new BotCommandException("Please enter ECM Delete URL");

        if (authKey == null || "".equals(authKey.trim()))
            throw new BotCommandException("Please provide ECM Auth Key parameter name");

        //Business Logic
        try {
            /*..Initialize Session*/
            var sessionValues = (Map<String, Object>)sessionMap.get(sessionName);
            var ticket = sessionValues.get("Ticket").toString();

            var request = "";

            var url = new URL(ecmDeleteURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("DELETE");
            con.setRequestProperty(authKey,ticket);

            var response = HttpUtils.execute(con, request.toString());
            String returnValue = "Success";
            if (response.getCode() != HttpURLConnection.HTTP_OK) {
                returnValue = Utils.getErrorDetails(response);
                //throw new BotCommandException(Utils.getErrorDetails(response));
            }
            /*..Return StringValue*/
            return new StringValue(returnValue);

        }
        catch (Exception e)
        { throw new BotCommandException("Error occurred while ECM node Delete: " + e.getMessage()); }

    }

    public void setSessionMap(Map<String, Object> sessionMap) {
        this.sessionMap = sessionMap;
    }
}
