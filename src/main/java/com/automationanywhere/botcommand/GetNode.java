package com.automationanywhere.botcommand;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.data.impl.TableValue;
import com.automationanywhere.botcommand.data.model.Schema;
import com.automationanywhere.botcommand.data.model.table.Row;
import com.automationanywhere.botcommand.data.model.table.Table;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.automationanywhere.commandsdk.model.AttributeType.TEXT;
import static com.automationanywhere.commandsdk.model.DataType.STRING;
import static com.automationanywhere.commandsdk.model.DataType.TABLE;
/**
 * @author Chiranjjeevi Vijayakumar (CJ)
 *
 */

//BotCommand makes a class eligible for being considered as an action.
@BotCommand
//CommandPks adds required information to be displayable on GUI.
@CommandPkg(
        //Unique name inside a package and label to display.
        name = "GetNode", label = "[[GetNode.label]]",
        node_label = "[[GetNode.node_label]]", description = "[[GetNode.description]]", icon = "pkg.svg",

        //Return type information. return_type ensures only the right kind of variable is provided on the UI.
        return_label = "[[GetNode.return_label]]",  return_description = "[[GetNode.return_description]]", return_type = TABLE, return_required = true)

public class GetNode {
    private static Logger logger = LogManager.getLogger(GetNode.class);

    @Sessions
    private Map<String, Object> sessionMap;

    @Execute
    public Value<Table> action(

            @Idx(index = "1", type = TEXT)
            @Pkg(label = "[[SessionName.label]]", description = "[[SessionName.description]]", default_value_type = STRING, default_value = "Default")
            @NotEmpty
                    String sessionName,

            @Idx(index = "2", type = TEXT)
            @Pkg(label = "[[GetNode.ecmURL.label]]", description = "[[GetNode.ecmURL.description]]")
            @NotEmpty
                    String ecmGetNodeURL) throws IOException, InterruptedException{

        //NULL Check
        if (sessionName == null || "".equals(sessionName.trim()))
            throw new BotCommandException("Please enter session name");

        if (!sessionMap.containsKey(sessionName))
            throw new BotCommandException("There are no existing session");

        if (ecmGetNodeURL == null || "".equals(ecmGetNodeURL.trim()))
            throw new BotCommandException("Please provide ECM GetNode API");

        //Business Logic
        Table table = new Table();
        try{

            var sessionValues = (Map<String, Object>)sessionMap.get(sessionName);
            var ticket = sessionValues.get("Ticket").toString();

            var request = "";

            var url = new URL(ecmGetNodeURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("OTCSTICKET",ticket);


            var response = HttpUtils.execute(con, request.toString());
            if (response.getCode() != HttpURLConnection.HTTP_OK) {
                throw new BotCommandException(Utils.getErrorDetails(response));
            }

            //Total Count of Files or Folders present
            String totalCount = JsonPath.parse(response.getBody()).read("$.total_count").toString();
            int totalCountNumber = Integer.parseInt(totalCount);

            //Define Schema for Table: Headers
            List<Schema> headers = new ArrayList<Schema>();
            headers.add(new Schema("NodeID"));
            headers.add(new Schema("Name"));
            headers.add(new Schema("Type"));

            //Commit headers to table
            table.setSchema(headers);

            //Define Rows for Table: Row
            List<Row> allRows = new ArrayList<Row>();

            for (int i = 0; i < totalCountNumber; i=i+1) {

                String nodeID = JsonPath.parse(response.getBody()).read("$.data["+i+"].id").toString();
                String name = JsonPath.parse(response.getBody()).read("$.data["+i+"].name").toString();
                String type = JsonPath.parse(response.getBody()).read("$.data["+i+"].type_name").toString();

                //Create Value List for accepting row values
                List<Value> currentRow = new ArrayList<>();

                //Based on Headers sequence, add the values
                currentRow.add(new StringValue(nodeID));
                currentRow.add(new StringValue(name));
                currentRow.add(new StringValue(type));

                //Set currentRow data into row object
                Row row = new Row();
                row.setValues(currentRow);

                //Commit row object to allRows
                allRows.add(row);
            }
            //Commit allRows to table
            table.setRows(allRows);

            //Return table value
            return new TableValue(table);
        }
        catch (Exception e)
        { throw new BotCommandException("Error occurred while ECM GetNode: " + e.getMessage()); }

    }
    public void setSessionMap(Map<String, Object> sessionMap) {
        this.sessionMap = sessionMap;
    }
}
