package com.automationanywhere.botcommand;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.*;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.rules.LocalFile;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.helpers.MimeUtils;
import com.automationanywhere.helpers.OkHttpUtils;
import com.automationanywhere.helpers.Utils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import static com.automationanywhere.commandsdk.model.AttributeType.*;
import static com.automationanywhere.commandsdk.model.DataType.STRING;

/**
 * @author Chiranjjeevi Vijayakumar (CJ)
 *
 */
@BotCommand

@CommandPkg(
        //Unique name inside a package and label to display.
        name = "AddDocumentVersion", label = "[[AddDocumentVersion.label]]",
        node_label = "[[AddDocumentVersion.node_label]]", description = "[[AddDocumentVersion.description]]", icon = "pkg.svg",
        return_label = "[[AddDocumentVersion.return_label]]", return_type = STRING)

public class AddDocumentVersion {
    private static Logger logger = LogManager.getLogger(AddDocumentVersion.class);
    @Sessions
    private Map<String, Object> sessionMap;

    @Idx(index = "3", type = GROUP)
    @Pkg(label = "[[AuthGroup.label]]")
    String authGroup;

    @Idx(index = "4", type = GROUP)
    @Pkg(label = "[[AddDocumentVersion.nameGroup.label]]")
    String nameGroup;

    @Idx(index = "5", type = GROUP)
    @Pkg(label = "[[AddDocumentVersion.fileBodyGroup.label]]")
    String fileBodyGroup;

    @Execute
    public Value<String> action(

            @Idx(index = "1", type = TEXT)
            @Pkg(label = "[[SessionName.label]]", description = "[[SessionName.description]]", default_value_type = STRING, default_value = "Default")
            @NotEmpty
            String sessionName,

            @Idx(index = "2", type = TEXT)
            @Pkg(label = "[[EcmURL.label]]", description = "[[AddDocumentVersion.ecmURL.description]]",default_value_type = STRING)
            @NotEmpty
            String ecmAddDocumentVersionURL,

            @Idx(index = "3.1", type = TEXT)
            @Pkg(label = "[[AuthName.label]]", description = "[[AuthName.description]]", default_value_type = STRING, default_value = "OTCSTICKET")
            @NotEmpty
            String authKey,

            @Idx(index = "4.1", type = TEXT)
            @Pkg(label = "[[Key.label]]", default_value_type = STRING, default_value = "name")
            @NotEmpty
            String nameKey,

            @Idx(index = "4.2", type = TEXT)
            @Pkg(label = "[[Value.label]]", description = "[[AddDocumentVersion.ecmName.description]]", default_value_type = STRING)
            String nameValue,

            @Idx(index = "5.1", type = TEXT)
            @Pkg(label = "[[Key.label]]", description = "[[AddDocumentVersion.fileContent.description]]", default_value_type = STRING, default_value = "file")
            @NotEmpty
            String fileKey,

            @Idx(index = "5.2", type = FILE)
            @Pkg(label = "[[AddDocumentVersion.fullFilePath.label]]",description = "[[AddDocumentVersion.fullFilePath.description]]")
            @LocalFile
            @NotEmpty
            String fullFilePath)  throws IOException, InterruptedException{

        //NULL Check
        if (sessionName == null || "".equals(sessionName.trim()))
            throw new BotCommandException("Please enter session name");

        if (!sessionMap.containsKey(sessionName))
            throw new BotCommandException("There are no existing session");

        if (ecmAddDocumentVersionURL == null || "".equals(ecmAddDocumentVersionURL.trim()))
            throw new BotCommandException("Please provide ECM Upload API");

        if (authKey == null || "".equals(authKey.trim()))
            throw new BotCommandException("Please provide ECM Auth Key parameter name");

        if (nameKey == null ||"".equals(nameKey.trim()))
            throw new BotCommandException("Please enter ECM name key");

        if (fileKey == null ||"".equals(fileKey.trim()))
            throw new BotCommandException("Please enter ECM file key");

        if (fullFilePath == null || "".equals(fullFilePath.trim()) || "".equals(FilenameUtils.getExtension(fullFilePath).trim()))
            throw new BotCommandException("Please enter File Path with extension");

        try{
            /*..Initialize Session*/
            var sessionValues = (Map<String, Object>)sessionMap.get(sessionName);
            var ticket = sessionValues.get("Ticket").toString();

            var url = new URL(ecmAddDocumentVersionURL);

            File uploadFile = new File(fullFilePath);

            RequestBody fileBody = RequestBody.create(MediaType.parse(MimeUtils.mimeType(fullFilePath)),uploadFile);

            if (nameValue == null ||"".equals(nameValue.trim()))
                nameValue = FilenameUtils.getName(fullFilePath);

            MultipartBody multipartBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart(nameKey,nameValue)
                    .addFormDataPart(fileKey,nameValue,fileBody)
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader(authKey,ticket)
                    .post(multipartBody)
                    .build();

            var response = OkHttpUtils.upload(request);
            String returnValue = "Success";
            if (response.getCode() != HttpURLConnection.HTTP_OK) {
                returnValue = Utils.getErrorDetails(response);
                //throw new BotCommandException(Utils.getErrorDetails(response));
            }

            /*..Return StringValue*/
            return new StringValue(returnValue);

        }
        catch (Exception e)
        { throw new BotCommandException("Error occurred while ECM Add document version: " + e.getMessage()); }

    }
    public void setSessionMap(Map<String, Object> sessionMap) {
        this.sessionMap = sessionMap;
    }

}
