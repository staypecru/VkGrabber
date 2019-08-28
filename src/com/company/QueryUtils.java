package com.company;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import javafx.scene.image.Image;

public class QueryUtils {

    private static final String LIST_OF_PEOPLE_WHO_LIKE_POST_URL_REQUEST = "https://api.vk.com/method/" +
            "likes.getList?type=post&v=5.52&" +
            "access_token=bef2bb7198bdf7215ca3b1d60ee75d5b23dca87318e0c4adadd2bf668e8b6f1e41964cad7df47a0707319";

    private static final String USER_INFO_URL_REQUEST = "https://api.vk.com/method/" +
            "users.get?fields=photo_50&v=5.52&" +
            "access_token=bef2bb7198bdf7215ca3b1d60ee75d5b23dca87318e0c4adadd2bf668e8b6f1e41964cad7df47a0707319&" +
            "user_ids=";


    public static ArrayList<User> getListOfUsersFromUrlRequest(String linkAddress) {

        URL requestUrl = createRequestURLForListWhoLikePost(linkAddress);

        String usersId = fetchUsersId(requestUrl);

        ArrayList<User> users = fetchUsersData(usersId);

        return users;
    }


    private static String fetchUsersId(URL requestUrl) {
        String response = makeHttpRequest(requestUrl);

        JSONObject responseWithUsersId = parseJsonFromString(response);
        return extractIdsFromJson(responseWithUsersId);

    }

    /**
     * @param linkAddress should looks like https://vk.com/tproger?w=wall-30666517_1611123
     *                    where "-30666517" is owner id: user(without minus) or group(with "-" (minus))
     *                    and "1611123" is item id (post number)
     **/
    private static URL createRequestURLForListWhoLikePost(String linkAddress) {

        String requestAddress = generateRequestFromLinkAddress(linkAddress);

        return parseURLFromString(requestAddress);
    }

    private static String generateRequestFromLinkAddress(String linkAddress) {

        String[] secondPartOfRequestAddress = linkAddress.split("/?w=wall");
        String[] userIdAndPostNumber = secondPartOfRequestAddress[1].split("_");

        String requestAddress = LIST_OF_PEOPLE_WHO_LIKE_POST_URL_REQUEST +
                "&owner_id=" +
                userIdAndPostNumber[0] +
                "&item_id=" +
                userIdAndPostNumber[1];

        return requestAddress;
    }


    private static String makeHttpRequest(URL requestUrl) {

        String response = "";

        if (requestUrl == null) {
            return response;
        }

        HttpURLConnection connection = null;
        InputStream inputStream = null;

        try {
            connection = setConnection(requestUrl);
            connection.connect();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = connection.getInputStream();
                response = readFromStream(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            disconnectConnection(connection);
            closeInputStream(inputStream);
        }
        return response;
    }


    private static HttpURLConnection setConnection(URL requestUrl) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(1000);
        connection.setReadTimeout(1000);
        return connection;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder response = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while (line != null) {
                response.append(line);
                line = bufferedReader.readLine();
            }
        }
        return response.toString();
    }

    private static void disconnectConnection(HttpURLConnection connection) {
        if (connection != null) {
            connection.disconnect();
        }
    }

    private static void closeInputStream(InputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Using json_simple_1.1 library from here https://www.geeksforgeeks.org/parse-json-java/
     * According to https://vk.com/dev/users.get  - users id should be divided by ","
     *
     * @param responseWithUsersId - is json object with structure from https://vk.com/dev/likes.getList
     **/
    private static String extractIdsFromJson(JSONObject responseWithUsersId) {

        JSONArray jsonItems = findJsonArrayWithItems(responseWithUsersId);

        return getIdsFromJsonArray(jsonItems);
    }

    private static JSONArray findJsonArrayWithItems(JSONObject responseWithUsersId) {
        JSONObject jsonResponse = (JSONObject) responseWithUsersId.get("response");
        JSONArray jsonItems = (JSONArray) jsonResponse.get("items");
        return jsonItems;
    }

    private static String getIdsFromJsonArray(JSONArray jsonItems) {
        String usersId = "";
        for (Object id : jsonItems) {
            usersId += id.toString() + ",";
        }
        return usersId;
    }

    /**
     * Logic from https://vk.com/dev/users.get
     *
     * @return ArrayList with data about Users
     */
    private static ArrayList<User> fetchUsersData(String usersId) {
        String request = USER_INFO_URL_REQUEST + usersId;

        URL requestURL = parseURLFromString(request);

        String responseWithUsers = makeHttpRequest(requestURL);

        return extractUsersFromJson(parseJsonFromString(responseWithUsers));

    }

    private static URL parseURLFromString(String request) {
        URL requestURL = null;
        try {
            requestURL = new URL(request);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return requestURL;
    }

    private static JSONObject parseJsonFromString(String responseWithUsers) {
        JSONObject jsonFile = null;
        try {
            jsonFile = (JSONObject) new JSONParser().parse(responseWithUsers);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return jsonFile;
    }

    /**
     * Using json_simple_1.1 library from here https://www.geeksforgeeks.org/parse-json-java/
     */
    private static ArrayList<User> extractUsersFromJson(JSONObject jsonFile) {

        JSONArray jsonArrayWithUsers = findJsonArrayWithUsers(jsonFile);

        return getUsersFromJsonArray(jsonArrayWithUsers);
    }

    private static JSONArray findJsonArrayWithUsers(JSONObject jsonFile) {
        return (JSONArray) jsonFile.get("response");
    }

    private static ArrayList<User> getUsersFromJsonArray(JSONArray jsonArrayWithUsers) {
        ArrayList<User> users = new ArrayList<>();

        for (int i = 0; i < jsonArrayWithUsers.size(); i++) {

            JSONObject userJsonObject = (JSONObject) jsonArrayWithUsers.get(i);

            User user = createUserFromJsonObject(userJsonObject);

            users.add(user);
        }


        return users;
    }

    private static User createUserFromJsonObject(JSONObject userObject) {

        Long id = (Long) userObject.get("id");
        String firstName = (String) userObject.get("first_name");
        String lastName = (String) userObject.get("last_name");
        String photoLink = (String) userObject.get("photo_50");

//        TODO: create async thread for each photo downloading
        BufferedImage image = downloadImageFromStringUrl(photoLink);

        return new User(id, firstName, lastName, image);
    }


    private static BufferedImage downloadImageFromStringUrl(String photoLink) {

        BufferedImage bufferedUserPhoto = null;
        try {
            bufferedUserPhoto = ImageIO.read(new URL(photoLink));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        TODO change new WritableImage(40, 40) to placeholder

//        Image userPhoto = SwingFXUtils.toFXImage(bufferedUserPhoto, new WritableImage(40, 40));
        return bufferedUserPhoto;
    }
}
