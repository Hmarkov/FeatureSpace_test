package com.company;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

import org.json.*;
/*
https://stackoverflow.com/questions/2591098/how-to-parse-json-in-java
https://stackoverflow.com/questions/12134687/how-to-add-element-into-arraylist-in-hashmap
https://www.w3schools.com/java/java_hashmap.asp
file:///C:/Users/icko5/AppData/Local/Packages/microsoft.windowscommunicationsapps_8wekyb3d8bbwe/LocalState/Files/S0/4/Attachments/Engineering_Take_Home_Task_2022[974].pdf
http://postcodes.io/docs
https://medium.com/codingthesmartway-com-blog/create-a-rest-api-with-json-server-36da8680136d


 */

public class Main {
    public static String GET_request(String parameter, String postcode) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        String url="";
        if(parameter !="") {
            url = "https://api.postcodes.io/postcodes/" + postcode + "/" + parameter;
        }else{
            url = "https://api.postcodes.io/postcodes/" + postcode;
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public static String Location_info(String postcode) throws IOException, InterruptedException {
        JSONObject obj = new JSONObject(GET_request("",postcode));
        String Location="Postcode:"+postcode+"\n"
                +"Country:"+obj.getJSONObject("result").getString("country")+"\n"
                +"Region:"+obj.getJSONObject("result").getString("region");
        return Location;
    }

    public static boolean validate_postcode(String postcode) throws IOException, InterruptedException {
        JSONObject obj = new JSONObject(GET_request("validate",postcode));
        return obj.getBoolean("result");
    }

    public static String Find_nearest_info(String postcode) throws IOException, InterruptedException {
        Map<String, PCode_Obj> postcode_info = new HashMap<>();
        String Nearest = "";
        JSONObject obj = new JSONObject(GET_request("nearest",postcode));
        JSONArray arr=new JSONArray(obj.getJSONArray("result"));
        for(int i=0;i<arr.length();i++){
            PCode_Obj loc=new PCode_Obj(arr.getJSONObject(i).getString("country"),arr.getJSONObject(i).getString("region"));
            postcode_info.put(arr.getJSONObject(i).getString("postcode"),loc);
        }
        for (Map.Entry<String, PCode_Obj> entry : postcode_info.entrySet()) {
            String key = entry.getKey();
            PCode_Obj Obj= postcode_info.get(key);
          Nearest+=("Post code:"+key+"Country:"+Obj.getCountry()+"Region:"+Obj.getRegion()+"\n");
        }

        return Nearest;
    }

    public static void Find_postcode_info(String postcode) throws IOException, InterruptedException {
        if ((validate_postcode(postcode))==true){
            System.out.println(Location_info(postcode));
            System.out.println(Find_nearest_info(postcode));
        }else{
            System.out.println("Invalid postcode");
        }


    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Find_postcode_info("GU50BD");

    }
}
