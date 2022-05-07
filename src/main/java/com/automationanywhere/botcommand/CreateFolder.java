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
        name = "CreateFolder", label = "[[CreateFolder.label]]",
        node_label = "[[CreateFolder.node_label]]", description = "[[CreateFolder.description]]", icon = "pkg.svg",
        return_label = "[[CreateFolder.return_label]]", return_type = STRING, return_required = false)

public class CreateFolder {
    private static Logger logger = LogManager.getLogger(CreateFolder.class);
    @Sessions
    private Map<String, Object> sessionMap;

    @Idx(index = "3", type = GROUP)
    @Pkg(label = "[[AuthGroup.label]]")
    String authGroup;

    @Idx(index = "4", type = GROUP)
    @Pkg(label = "[[CreateFolder.typeGroup.label]]")
    String typeGroup;

    @Idx(index = "5", type = GROUP)
    @Pkg(label = "[[CreateFolder.parentIDGroup.label]]")
    String parentIDGroup;

    @Idx(index = "6", type = GROUP)
    @Pkg(label = "[[CreateFolder.nameGroup.label]]")
    String nameGroup;

    @Execute
    public Value<String> action(
            //Idx 1 would be displayed first, with a text box for entering the value.
            @Idx(index = "1", type = TEXT)
            @Pkg(label = "[[SessionName.label]]", description = "[[SessionName.description]]", default_value_type = STRING, default_value = "Default")
            @NotEmpty
                    String sessionName,

            @Idx(index = "2", type = TEXT)
            @Pkg(label = "[[EcmURL.label]]", description = "[[EcmURL.description]]")
            @NotEmpty
                    String ecmCreateFolderURL,

            @Idx(index = "3.1", type = TEXT)
            @Pkg(label = "[[AuthName.label]]", description = "[[AuthName.description]]", default_value_type = STRING, default_value = "OTCSTICKET")
            @NotEmpty
                    String authKey,

            @Idx(index = "4.1", type = TEXT)
            @Pkg(label = "[[Key.label]]",default_value_type = STRING, default_value = "type")
            @NotEmpty
                    String ecmTypeKey,

            @Idx(index = "4.2", type = TEXT)
            @Pkg(label = "[[Value.label]]", description = "[[CreateFolder.ecmText.description]]", default_value_type = STRING, default_value = "0")
            @NotEmpty
                    String ecmTypeValue,

            @Idx(index = "5.1", type = TEXT)
            @Pkg(label = "[[Key.label]]",default_value_type = STRING, default_value = "parent_id")
            @NotEmpty
                    String parentIDKey,

            @Idx(index = "5.2", type = TEXT)
            @Pkg(label = "[[Value.label]]")
            @NotEmpty
                    String parentIDValue,

            @Idx(index = "6.1", type = TEXT)
            @Pkg(label = "[[Key.label]]",default_value_type = STRING, default_value = "name")
            @NotEmpty
                    String folderNameKey,

            @Idx(index = "6.2", type = TEXT)
            @Pkg(label = "[[Value.label]]")
            @NotEmpty
                    String folderNameValue)  throws IOException, InterruptedException {

        //NULL Check
        if (sessionName == null || "".equals(sessionName.trim()))
            throw new BotCommandException("Please enter session name");

        if (!sessionMap.containsKey(sessionName))
            throw new BotCommandException("There are no existing session");

        if (ecmCreateFolderURL == null || "".equals(ecmCreateFolderURL.trim()))
            throw new BotCommandException("Please provide ECM Create Folder API");

        if (authKey == null || "".equals(authKey.trim()))
            throw new BotCommandException("Please provide ECM Auth Key parameter name");

        if (ecmTypeKey == null ||"".equals(ecmTypeKey.trim()))
            throw new BotCommandException("Please enter ECM type key parameter");

        if (ecmTypeValue == null ||"".equals(ecmTypeValue.trim()))
            throw new BotCommandException("Please enter ECM type value");

        if (parentIDKey == null || "".equals(parentIDKey.trim()))
            throw new BotCommandException("Please enter Parent Node ID key parameter");

        if (parentIDValue == null || "".equals(parentIDValue.trim()))
            throw new BotCommandException("Please enter Parent Node ID value");

        if (folderNameKey == null || "".equals(folderNameKey.trim()))
            throw new BotCommandException("Please enter Parent Node ID key parameter");

        if (folderNameValue == null || "".equals(folderNameValue.trim()))
            throw new BotCommandException("Please enter Folder Name value");

        //Business Logic
        try {
            /*..Initialize Session*/
            var sessionValues = (Map<String, Object>)sessionMap.get(sessionName);
            var ticket = sessionValues.get("Ticket").toString();

            //String request = "{ \"type\": \"" + 0 + "\", \"parent_id\": \"" + parentID + "\", \"name\": \"" + folderName+"\" }";
            var request = ecmTypeKey+"="+ecmTypeValue+"&"+parentIDKey+"="+parentIDValue+"&"+folderNameKey+"="+folderNameValue;

            var url = new URL(ecmCreateFolderURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty(authKey,ticket);

            var response = HttpUtils.execute(con, request.toString());
            String returnValue = response.getBody();
            if (response.getCode() != HttpURLConnection.HTTP_OK) {
                returnValue = Utils.getErrorDetails(response);
                //throw new BotCommandException(Utils.getErrorDetails(response));
            }

            /*..Return StringValue*/
            return new StringValue(returnValue);
            
        }
        catch (Exception e)
        { throw new BotCommandException("Error occurred while creating folder in ECM: " + e.getMessage()); }

    }

    public void setSessionMap(Map<String, Object> sessionMap) {
        this.sessionMap = sessionMap;
    }

}
