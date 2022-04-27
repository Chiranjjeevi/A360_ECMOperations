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
        name = "CreateFolder", label = "[[CreateFolder.label]]",
        node_label = "[[CreateFolder.node_label]]", description = "[[CreateFolder.description]]", icon = "pkg.svg",

        //Return type information. return_type ensures only the right kind of variable is provided on the UI.
        return_label = "[[CreateFolder.return_label]]", return_type = STRING, return_required = false)

public class CreateFolder {
    private static Logger logger = LogManager.getLogger(CreateFolder.class);

    @Sessions
    private Map<String, Object> sessionMap;

    @Execute
    public Value<String> action(
            //Idx 1 would be displayed first, with a text box for entering the value.
            @Idx(index = "1", type = TEXT)
            @Pkg(label = "[[SessionName.label]]", description = "[[SessionName.description]]", default_value_type = STRING, default_value = "Default")
            @NotEmpty
                    String sessionName,

            @Idx(index = "2", type = TEXT)
            @Pkg(label = "[[CreateFolder.ecmURL.label]]", description = "[[CreateFolder.ecmURL.description]]")
            @NotEmpty
                    String ecmCreateFolderURL,

            @Idx(index = "3", type = TEXT)
            @Pkg(label = "[[CreateFolder.ecmText.label]]", description = "[[CreateFolder.ecmText.description]]", default_value_type = STRING, default_value = "0")
            @NotEmpty
            String ecmType,

            @Idx(index = "4", type = TEXT)
            @Pkg(label = "[[CreateFolder.parentID.label]]")
            //Ensure that a validation error is thrown when the value is null.
            @NotEmpty
                    String parentID,

            @Idx(index = "5", type = TEXT)
            @Pkg(label = "[[CreateFolder.folderName.label]]")
            @NotEmpty
                    String folderName)  throws IOException, InterruptedException {

        //NULL Check
        if (sessionName == null || "".equals(sessionName.trim()))
            throw new BotCommandException("Please enter session name");

        if (!sessionMap.containsKey(sessionName))
            throw new BotCommandException("There are no existing session");

        if (ecmCreateFolderURL == null || "".equals(ecmCreateFolderURL.trim()))
            throw new BotCommandException("Please provide ECM Create Folder API");

        if (ecmType == null ||"".equals(ecmType.trim()))
            throw new BotCommandException("Please enter ECM type value");

        if (parentID == null || "".equals(parentID.trim()))
            throw new BotCommandException("Please enter Parent Node ID");

        if (folderName == null || "".equals(folderName.trim()))
            throw new BotCommandException("Please enter Folder Name");

        //Business Logic
        try {

            var sessionValues = (Map<String, Object>)sessionMap.get(sessionName);
            var ticket = sessionValues.get("Ticket").toString();

            //String request = "{ \"type\": \"" + 0 + "\", \"parent_id\": \"" + parentID + "\", \"name\": \"" + folderName+"\" }";
            var request = "type="+ecmType+"&parent_id="+parentID+"&name="+folderName;

            var url = new URL(ecmCreateFolderURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("OTCSTICKET",ticket);


            var response = HttpUtils.execute(con, request.toString());
            if (response.getCode() != HttpURLConnection.HTTP_OK) {
                throw new BotCommandException(Utils.getErrorDetails(response));
            }

            String folderID = JsonPath.parse(response.getBody()).read("$.id").toString();

            //Return StringValue.
            return new StringValue(folderID);
            
        }
        catch (Exception e)
        { throw new BotCommandException("Error occurred while creating folder in ECM: " + e.getMessage()); }

    }

    public void setSessionMap(Map<String, Object> sessionMap) {
        this.sessionMap = sessionMap;
    }

}
