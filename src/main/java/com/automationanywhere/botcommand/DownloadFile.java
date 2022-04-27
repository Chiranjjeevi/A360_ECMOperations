package com.automationanywhere.botcommand;


import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.*;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.rules.LocalFile;
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

import static com.automationanywhere.commandsdk.model.AttributeType.FILE;
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
        name = "DownloadFile", label = "[[DownloadFile.label]]",
        node_label = "[[DownloadFile.node_label]]", description = "[[DownloadFile.description]]", icon = "pkg.svg",

        //Return type information. return_type ensures only the right kind of variable is provided on the UI.
        return_label = "[[DownloadFile.return_label]]", return_type = STRING, return_required = false)

public class DownloadFile {
    private static Logger logger = LogManager.getLogger(DownloadFile.class);

    @Sessions
    private Map<String, Object> sessionMap;

    @Execute
    public Value<String> action(

            @Idx(index = "1", type = TEXT)
            @Pkg(label = "[[SessionName.label]]", description = "[[SessionName.description]]", default_value_type = STRING, default_value = "Default")
            @NotEmpty
            String sessionName,

            @Idx(index = "2", type = TEXT)
            @Pkg(label = "[[DownloadFile.ecmURL.label]]", description = "[[DownloadFile.ecmURL.description]]")
            @NotEmpty
            String ecmDownloadURL,

            @Idx(index = "3", type = TEXT)
            @Pkg(label = "[[DownloadFile.folderPath.label]]", description = "[[DownloadFile.folderPath.description]]")
            @NotEmpty
            String folderPath,

            @Idx(index = "4", type = TEXT)
            @Pkg(label = "[[DownloadFile.filename.label]]", description = "[[DownloadFile.filename.description]]")
            @NotEmpty
            String filename)  throws IOException, InterruptedException{


        //NULL Check
        if (sessionName == null || "".equals(sessionName.trim()))
            throw new BotCommandException("Please enter session name");

        if (!sessionMap.containsKey(sessionName))
            throw new BotCommandException("There are no existing session");

        if (ecmDownloadURL == null || "".equals(ecmDownloadURL.trim()))
            throw new BotCommandException("Please provide ECM Download API");

        if(folderPath == null || "".equals(folderPath.trim()))
            throw new BotCommandException("Please enter Folder Path");

        if(filename == null || "".equals(filename.trim()))
            throw new BotCommandException("Please enter Filename");

        try{

            //Initialize Session
            var sessionValues = (Map<String, Object>)sessionMap.get(sessionName);
            var ticket = sessionValues.get("Ticket").toString();

            var request = "";

            var url = new URL(ecmDownloadURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("OTCSTICKET",ticket);

            var response = HttpUtils.download(con, filename, folderPath);
            if (response.getCode() != HttpURLConnection.HTTP_OK) {
                throw new BotCommandException("Response:"+Utils.getErrorDetails(response));
            }

            String body = response.getBody().toString();

            return new StringValue(body);

        }
        catch (Exception e)
        { throw new BotCommandException("Error occurred while ECM download: " + e.getMessage()); }

    }
    public void setSessionMap(Map<String, Object> sessionMap) {
        this.sessionMap = sessionMap;
    }
}
