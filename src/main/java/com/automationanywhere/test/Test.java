package com.automationanywhere.test;

import java.io.IOException;

public class Test {
    public static void main(String[] args)
            throws IOException
    {

         try {

             //String filepath = "D:\\AA\\Projects\\Sabic\\getNode_Sample2.txt";

             //Path fileName = Path.of(filepath);
            //String str = Files.readString(fileName);

            //System.out.println(FilenameUtils.getBaseName(filepath)); // PersonalPlanning
            //System.out.println(FilenameUtils.getName(filepath)); // PersonalPlanning.xlsx
            //System.out.println(file.getName());  // PersonalPlanning.xlsx

            //System.out.println(connection.guessContentTypeFromName(file.getName())); // null


            /**

            for (int i = 0; i < totalCountNumber; i=i+1) {

                nodeID = JsonPath.read(str,"$.data["+i+"].id").toString();
                name = JsonPath.read(str,"$.data["+i+"].name").toString();
                type = JsonPath.read(str,"$.data["+i+"].type_name").toString();

                System.out.println("\nNode Details:");
                System.out.println("Node ID: "+ nodeID);
                System.out.println("Name: "+ name);
                System.out.println("Type: "+ type);

                //table1.setRows("NodeID",);
            }
             */

        }
        catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }

    }
}
