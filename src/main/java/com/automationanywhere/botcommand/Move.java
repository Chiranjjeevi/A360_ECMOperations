package com.automationanywhere.botcommand;


import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.*;
import com.automationanywhere.commandsdk.annotations.BotCommand;
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
        name = "Move", label = "[[Move.label]]",
        node_label = "[[Move.node_label]]", description = "[[Move.description]]", icon = "pkg.svg",

        //Return type information. return_type ensures only the right kind of variable is provided on the UI.
        return_label = "[[Move.return_label]]", return_type = STRING, return_required = false)

public class Move {
    private static Logger logger = LogManager.getLogger(Move.class);

    @Sessions
    private Map<String, Object> sessionMap;

    @Execute
    public Value<String> action(

            @Idx(index = "1", type = TEXT)
            @Pkg(label = "[[SessionName.label]]", description = "[[SessionName.description]]", default_value_type = STRING, default_value = "Default")
            @NotEmpty
                    String sessionName,

            @Idx(index = "2", type = TEXT)
            @Pkg(label = "[[Move.ecmURL.label]]", description = "[[Move.ecmURL.description]]")
            @NotEmpty
                    String ecmMoveURL,

            @Idx(index = "3", type = TEXT)
            @Pkg(label = "[[Move.destinationID.label]]", description = "[[Move.destinationID.description]]")
            @NotEmpty
                    String destinationID,

            @Idx(index = "4", type = TEXT)
            @Pkg(label = "[[Move.originalID.label]]", description = "[[Move.originalID.description]]")
            @NotEmpty
                    String originalID) throws IOException, InterruptedException{

        //NULL Check
        if (sessionName == null || "".equals(sessionName.trim()))
            throw new BotCommandException("Please enter session name");

        if (!sessionMap.containsKey(sessionName))
            throw new BotCommandException("There are no existing session");

        if (ecmMoveURL == null || "".equals(ecmMoveURL.trim()))
            throw new BotCommandException("Please provide ECM Move API");

        if (destinationID == null || "".equals(destinationID.trim()))
            throw new BotCommandException("Please enter Destination Node ID");

        if (originalID == null || "".equals(originalID.trim()))
            throw new BotCommandException("Please enter Folder or File Node ID to Move ");

        //Business Logic
        try{
            // Error Handing --> Required
            var sessionValues = (Map<String, Object>)sessionMap.get(sessionName);
            var ticket = sessionValues.get("Ticket").toString();

            var request = "parent_id="+destinationID+"&original_id="+originalID;

            var url = new URL(ecmMoveURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
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
        { throw new BotCommandException("Error occurred while ECM Move: " + e.getMessage()); }
    }
    public void setSessionMap(Map<String, Object> sessionMap) {
        this.sessionMap = sessionMap;
    }
}
