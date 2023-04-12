package eu.metacloudservice.webserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.metacloudservice.Driver;
import eu.metacloudservice.configuration.ConfigDriver;
import eu.metacloudservice.configuration.dummys.authenticator.AuthenticatorKey;
import eu.metacloudservice.webserver.interfaces.IRest;
import lombok.SneakyThrows;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class RestDriver {

    protected static final Gson GSON = (new GsonBuilder()).serializeNulls().setPrettyPrinting().disableHtmlEscaping().create();
    private String ip;
    private int port;

    public RestDriver(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public RestDriver() {
    }

    @SneakyThrows
    public IRest convert(String json, Class<? extends IRest> tClass){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, tClass);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public String convert(IRest IRest){
        return GSON.toJson(IRest);
    }

    public String put(String route, String content){
        ConfigDriver configDriver = new ConfigDriver("./connection.key");
        AuthenticatorKey authConfig = (AuthenticatorKey) configDriver.read(AuthenticatorKey.class);
        String authCheckKey = Driver.getInstance().getMessageStorage().base64ToUTF8(authConfig.getKey());
        String urlString = String.format("http://%s:%d/%s%s", ip, port, authCheckKey, route);
        URL url  = null;
        HttpURLConnection con = null;
        BufferedReader in = null;
        try {
            url =  new URL(urlString);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("PUT");
            con.setDoOutput(true);
            con.setConnectTimeout(5000); // Set connection timeout to 5 seconds
            con.setReadTimeout(5000); // Set read timeout to 5 seconds
            OutputStream os = con.getOutputStream();
            os.write(content.getBytes());
            os.flush();
            os.close();
            int statusCode = con.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {
                in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                content = response.toString();
            } else {
            }
        } catch (IOException e) {

        } finally {
            // Clean up resources
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
            if (con != null) {
                con.disconnect();
            }
        }

        return content;

    }

    public String get(String route){
        AuthenticatorKey authConfig = (AuthenticatorKey) new ConfigDriver("./connection.key").read(AuthenticatorKey.class);
        String authCheckKey = Driver.getInstance().getMessageStorage().base64ToUTF8(authConfig.getKey());
        URL url = null;
        HttpURLConnection con = null;
        BufferedReader in = null;
        String content = null;
        try {
            url = new URL("http://" + ip + ":" + port + "/" + authCheckKey + route);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoOutput(false);
            con.setConnectTimeout(5000); // Set connection timeout to 5 seconds
            con.setReadTimeout(5000); // Set read timeout to 5 seconds
            con.connect();
            int statusCode = con.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {
                in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                content = response.toString();
            } else {
            }
        } catch (IOException e) {
        } finally {

            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // Handle exception
                }
            }
            if (con != null) {
                con.disconnect();
            }
        }

        return content;
    }
}