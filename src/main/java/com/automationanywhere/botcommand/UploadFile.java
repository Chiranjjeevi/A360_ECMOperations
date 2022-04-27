package com.automationanywhere.botcommand;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.*;
import com.automationanywhere.commandsdk.annotations.rules.LocalFile;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.helpers.MimeUtils;
import com.automationanywhere.helpers.OkHttpUtils;
import com.automationanywhere.helpers.Utils;
import okhttp3.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
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

@BotCommand

//CommandPks adds required information to be displayable on GUI.
@CommandPkg(
        //Unique name inside a package and label to display.
        name = "UploadFile", label = "[[UploadFile.label]]",
        node_label = "[[UploadFile.node_label]]", description = "[[UploadFile.description]]", icon = "pkg.svg",

        //Return type information. return_type ensures only the right kind of variable is provided on the UI.
        return_label = "[[UploadFile.return_label]]", return_type = STRING, return_required = false)

public class UploadFile {
    private static Logger logger = LogManager.getLogger(UploadFile.class);

    @Sessions
    private Map<String, Object> sessionMap;

    @Execute
    public Value<String> action(

            @Idx(index = "1", type = TEXT)
            @Pkg(label = "[[SessionName.label]]", description = "[[SessionName.description]]", default_value_type = STRING, default_value = "Default")
            @NotEmpty
                    String sessionName,

            @Idx(index = "2", type = TEXT)
            @Pkg(label = "[[UploadFile.ecmURL.label]]", description = "[[UploadFile.ecmURL.description]]")
            @NotEmpty
                    String ecmUploadURL,

            //Idx 1 would be displayed first, with a text box for entering the value.
            @Idx(index = "3", type = TEXT)
            //UI labels.
            @Pkg(label = "[[UploadFile.parentID.label]]", description = "[[UploadFile.parentID.description]]")
            //Ensure that a validation error is thrown when the value is null.
            @NotEmpty
                    String parentID,

            @Idx(index = "4", type = TEXT)
            @Pkg(label = "[[UploadFile.ecmText.label]]", description = "[[UploadFile.ecmText.description]]", default_value_type = STRING, default_value = "144")
            @NotEmpty
            String ecmType,

            @Idx(index = "5", type = FILE)
            @Pkg(label = "[[UploadFile.fullFilePath.label]]")
            @LocalFile
            @NotEmpty
                    String fullFilePath)  throws IOException, InterruptedException{

        //NULL Check
        if (sessionName == null || "".equals(sessionName.trim()))
            throw new BotCommandException("Please enter session name");

        if (!sessionMap.containsKey(sessionName))
            throw new BotCommandException("There are no existing session");

        if (ecmUploadURL == null || "".equals(ecmUploadURL.trim()))
            throw new BotCommandException("Please provide ECM Upload API");

        if (parentID == null || "".equals(parentID.trim()))
            throw new BotCommandException("Please enter Parent Node ID");

        if (ecmType == null ||"".equals(ecmType.trim()))
            throw new BotCommandException("Please enter ECM type value");

        if (fullFilePath == null || "".equals(fullFilePath.trim()) || "".equals(FilenameUtils.getExtension(fullFilePath).trim()))
            throw new BotCommandException("Please enter File Path with extension");

        try{

            //Initialize Session
            var sessionValues = (Map<String, Object>)sessionMap.get(sessionName);
            var ticket = sessionValues.get("Ticket").toString();

            var url = new URL(ecmUploadURL);

            File uploadFile = new File(fullFilePath);

            RequestBody fileBody = RequestBody.create(MediaType.parse(MimeUtils.mimeType(fullFilePath)),uploadFile);

            MultipartBody multipartBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("parent_id",parentID)
                    .addFormDataPart("type",ecmType)
                    .addFormDataPart("name",FilenameUtils.getBaseName(fullFilePath))
                    .addFormDataPart("file",FilenameUtils.getName(fullFilePath),fileBody)
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("OTCSTICKET",ticket)
                    .post(multipartBody)
                    .build();

            var response = OkHttpUtils.upload(request);
            if (response.getCode() != HttpURLConnection.HTTP_OK) {
                throw new BotCommandException(Utils.getErrorDetails(response));
            }

            String body = response.getBody().toString();

            return new StringValue(body);

        }
        catch (Exception e)
        { throw new BotCommandException("Error occurred while ECM Upload: " + e.getMessage()); }

    }
    public void setSessionMap(Map<String, Object> sessionMap) {
        this.sessionMap = sessionMap;
    }
}
