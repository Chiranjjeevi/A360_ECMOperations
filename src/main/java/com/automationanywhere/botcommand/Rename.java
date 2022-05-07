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

@BotCommand

@CommandPkg(
        //Unique name inside a package and label to display.
        name = "Rename", label = "[[Rename.label]]", node_label = "[[Rename.node_label]]", description = "[[Rename.description]]", icon = "pkg.svg",
        return_label = "[[Rename.return_label]]", return_type = STRING, return_required = false)

public class Rename {
    private static Logger logger = LogManager.getLogger(Rename.class);
    @Sessions
    private Map<String, Object> sessionMap;

    @Idx(index = "3", type = GROUP)
    @Pkg(label = "[[AuthGroup.label]]")
    String authGroup;

    @Idx(index = "4", type = GROUP)
    @Pkg(label = "[[Rename.newNameGroup.label]]")
    String newNameGroup;

    @Execute
    public Value<String> action(
            @Idx(index = "1", type = TEXT)
            @Pkg(label = "[[SessionName.label]]", description = "[[SessionName.description]]", default_value_type = STRING, default_value = "Default")
            @NotEmpty
                String sessionName,

            @Idx(index = "2", type = TEXT)
            @Pkg(label = "[[EcmURL.label]]", description = "[[Rename.ecmURL.description]]")
            @NotEmpty
                String ecmRenameURL,

            @Idx(index = "3.1", type = TEXT)
            @Pkg(label = "[[AuthName.label]]", description = "[[AuthName.description]]", default_value_type = STRING, default_value = "OTCSTICKET")
            @NotEmpty
                String authKey,

            @Idx(index = "4.1", type = TEXT)
            @Pkg(label = "[[Key.label]]", default_value_type = STRING, default_value = "name")
            @NotEmpty
                String newNameKey,

            @Idx(index = "4.2", type = TEXT)
            @Pkg(label = "[[Value.label]]", description = "[[Rename.newName.description]]")
            @NotEmpty
                String newNameValue)  throws IOException, InterruptedException {

        //NULL Check
        if (sessionName == null || "".equals(sessionName.trim()))
            throw new BotCommandException("Please enter session name");

        if (!sessionMap.containsKey(sessionName))
            throw new BotCommandException("There are no existing session");

        if (ecmRenameURL == null || "".equals(ecmRenameURL.trim()))
            throw new BotCommandException("Please provide ECM Rename API URL");

        if (authKey == null || "".equals(authKey.trim()))
            throw new BotCommandException("Please provide ECM Auth Key parameter name");

        if (newNameKey == null || "".equals(newNameKey.trim()))
            throw new BotCommandException("Please enter Name key parameter name");

        if (newNameValue == null || "".equals(newNameValue.trim()))
            throw new BotCommandException("Please enter New name");

        //Business Logic
        try {
            /*..Initialize Session*/
            var sessionValues = (Map<String, Object>)sessionMap.get(sessionName);
            var ticket = sessionValues.get("Ticket").toString();

            var request = newNameKey+"="+newNameValue;

            var url = new URL(ecmRenameURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("PUT");
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
        { throw new BotCommandException("Error occurred while ECM rename: " + e.getMessage()); }

    }
    public void setSessionMap(Map<String, Object> sessionMap) {
        this.sessionMap = sessionMap;
    }
}
