package com.automationanywhere.botcommand;

import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.*;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Map;

import static com.automationanywhere.commandsdk.model.AttributeType.TEXT;
import static com.automationanywhere.commandsdk.model.DataType.STRING;
/**
 * @author Chiranjjeevi Vijayakumar (CJ)
 *
 */

@BotCommand

@CommandPkg(name = "Disconnect", label = "[[Disconnect.label]]",
        node_label = "[[Disconnect.node_label]]", description = "[[Disconnect.description]]", icon = "pkg.svg")

public class Disconnect {
    private static Logger logger = LogManager.getLogger(Disconnect.class);

    @Sessions
    private Map<String, Object> sessionMap;

    @Execute
    public void execute(
            @Idx(index = "1", type = TEXT)
            @Pkg(label = "[[SessionName.label]]", description = "[[SessionName.description]]", default_value_type = STRING, default_value = "Default")
            @NotEmpty
                    String sessionName) throws IOException, InterruptedException {

        if (sessionName == null || "".equals(sessionName.trim()))
            throw new BotCommandException("Please enter session name");

        //Business Logic
        try {

            if(sessionMap.containsKey(sessionName))
                sessionMap.remove(sessionName);
            else
                throw new BotCommandException("There are no open Session with: " + sessionName);

        }
        catch (Exception e) {throw new BotCommandException("Error occurred while Session disconnect: " + e.getMessage());}
    }

    public void setSessionMap(Map<String, Object> sessionMap) {
        this.sessionMap = sessionMap;
    }
}

