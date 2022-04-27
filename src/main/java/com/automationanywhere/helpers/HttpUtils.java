package com.automationanywhere.helpers;

import com.automationanywhere.objects.ResponseObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Chiranjjeevi Vijayakumar (CJ)
 *
 */

public class HttpUtils {

    /**
     * This method exceutes basic POST/GET and returns ResponseObject
     *
     * @param connection : accepts HttpURLConnection connection
     * @param request: API parameters Ex: request = "type=0&parent_id="+parentID+"&name="+folderName;
     * @throws IOException
     * @throws InterruptedException
     */
    public static ResponseObject execute(HttpURLConnection connection, String request) throws IOException, InterruptedException {

        String response = "";
        Integer code = -1;

        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        request = request.trim();
        if (request != "") {
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = request.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
        }

        code = connection.getResponseCode();

        var errorStream = connection.getErrorStream();
        InputStream stream = errorStream != null ? errorStream : connection.getInputStream();

        BufferedReader in = new BufferedReader(new InputStreamReader(stream));
        String inputLine;
        StringBuffer responseBuffer = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            responseBuffer.append(inputLine);
        }
        in.close();
        connection.disconnect();

        response = responseBuffer.toString();
        return new ResponseObject(code, response);
    }

    /**
     * This method Downloads file to Local Path and returns ResponseObject
     *
     * @param connection : accepts HttpURLConnection connection
     * @param filename : filename with extension. Example: Sample.txt
     * @param folderpath : local folder path where file to be saved
     * @throws IOException
     * @throws InterruptedException
     */
    public static ResponseObject download(HttpURLConnection connection, String filename, String folderpath) throws IOException, InterruptedException {

        String response = "";
        Integer code = -1;
        Integer size = 1024;

        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setRequestProperty("Cache-Control", "no-cache");

        code = connection.getResponseCode();

        var errorStream = connection.getErrorStream();
        InputStream stream = errorStream != null ? errorStream : connection.getInputStream();

        Path currentRelativePath = Paths.get("");
        Path currentDir = currentRelativePath.toAbsolutePath();
        Path filepath = currentDir.resolve(folderpath + File.separatorChar + filename);

        File file = new File(filepath.toString());

        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(stream);
            FileOutputStream fos = new FileOutputStream(file);
            BufferedReader in = new BufferedReader(new InputStreamReader(stream))) {

            byte[] buffer = new byte[size];

            Integer read = -1;
            while ((read = bufferedInputStream.read(buffer,0,size)) != -1){
                fos.write(buffer,0,read);
            }
            fos.flush();

            String inputLine;
            StringBuffer responseBuffer = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                responseBuffer.append(inputLine);
            }
            response = responseBuffer.toString();
        }

        connection.disconnect();

        return new ResponseObject(code, response);
    }

}
