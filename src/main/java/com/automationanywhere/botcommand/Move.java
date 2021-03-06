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
        name = "Move", label = "[[Move.label]]",
        node_label = "[[Move.node_label]]", description = "[[Move.description]]", icon = "pkg.svg",
        return_label = "[[Move.return_label]]", return_type = STRING, return_required = false)

public class Move {
    private static Logger logger = LogManager.getLogger(Move.class);
    @Sessions
    private Map<String, Object> sessionMap;

    @Idx(index = "3", type = GROUP)
    @Pkg(label = "[[AuthGroup.label]]")
    String authGroup;

    @Idx(index = "4", type = GROUP)
    @Pkg(label = "[[Move.destinationIDGroup.label]]")
    String destIDGroup;

    @Idx(index = "5", type = GROUP)
    @Pkg(label = "[[Move.originalIDGroup.label]]")
    String orgIDGroup;

    @Execute
    public Value<String> action(

            @Idx(index = "1", type = TEXT)
            @Pkg(label = "[[SessionName.label]]", description = "[[SessionName.description]]", default_value_type = STRING, default_value = "Default")
            @NotEmpty
                    String sessionName,

            @Idx(index = "2", type = TEXT)
            @Pkg(label = "[[EcmURL.label]]", description = "[[EcmURL.description]]")
            @NotEmpty
                    String ecmMoveURL,

            @Idx(index = "3.1", type = TEXT)
            @Pkg(label = "[[AuthName.label]]", description = "[[AuthName.description]]", default_value_type = STRING, default_value = "OTCSTICKET")
            @NotEmpty
                    String authKey,

            @Idx(index = "4.1", type = TEXT)
            @Pkg(label = "[[Key.label]]", default_value_type = STRING, default_value = "parent_id")
            @NotEmpty
                    String destinationIDKey,

            @Idx(index = "4.2", type = TEXT)
            @Pkg(label = "[[Value.label]]", description = "[[Move.destinationID.description]]")
            @NotEmpty
                    String destinationIDValue,

            @Idx(index = "5.1", type = TEXT)
            @Pkg(label = "[[Key.label]]", default_value_type = STRING, default_value = "original_id")
            @NotEmpty
                    String originalIDKey,

            @Idx(index = "5.2", type = TEXT)
            @Pkg(label = "[[Value.label]]", description = "[[Move.originalID.description]]")
            @NotEmpty
                    String originalIDValue) throws IOException, InterruptedException{

        //NULL Check
        if (sessionName == null || "".equals(sessionName.trim()))
            throw new BotCommandException("Please enter session name");

        if (!sessionMap.containsKey(sessionName))
            throw new BotCommandException("There are no existing session");

        if (ecmMoveURL == null || "".equals(ecmMoveURL.trim()))
            throw new BotCommandException("Please provide ECM Move API");

        if (authKey == null || "".equals(authKey.trim()))
            throw new BotCommandException("Please provide ECM Auth Key parameter name");

        if (destinationIDKey == null || "".equals(destinationIDKey.trim()))
            throw new BotCommandException("Please enter Destination Node ID key parameter name");

        if (destinationIDValue == null || "".equals(destinationIDValue.trim()))
            throw new BotCommandException("Please enter Destination Node ID value");

        if (originalIDKey == null || "".equals(originalIDKey.trim()))
            throw new BotCommandException("Please enter Folder or File Node ID key parameter name to Move ");

        if (originalIDValue == null || "".equals(originalIDValue.trim()))
            throw new BotCommandException("Please enter Folder or File Node ID to Move ");

        //Business Logic
        try{
            /*..Initialize Session*/
            var sessionValues = (Map<String, Object>)sessionMap.get(sessionName);
            var ticket = sessionValues.get("Ticket").toString();

            var request = destinationIDKey+"="+destinationIDValue+"&"+originalIDKey+"="+originalIDValue;

            var url = new URL(ecmMoveURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
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
        { throw new BotCommandException("Error occurred while ECM Move: " + e.getMessage()); }
    }
    public void setSessionMap(Map<String, Object> sessionMap) {
        this.sessionMap = sessionMap;
    }
}
