package com.company;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

import org.json.*;

public class Main {
    public enum Response {
        r1("Success",200),
        r2("Bad request/Wrong input",400),
        r3("Not found",404),
        r4("Server error",500);

        private final String response;
        private final int code;

        public String getResponse() {return response;}
        public int getCode() {return code;}

        Response(String response, int code) {
            this.response = response;
            this.code = code;
        }
    }

    /*
    Method to make a specific request based on parameter value
     */
    public static String GET_request(String parameter, String postcode) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        String url="";
        postcode=postcode.replace(" ","");
        String res="";
        if(parameter !="") {
            url = "https://api.postcodes.io/postcodes/" + postcode + "/" + parameter;
        }else{url = "https://api.postcodes.io/postcodes/" + postcode;}

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
            res=response.body();
        }catch (HttpConnectTimeoutException e){
            System.out.println(e);
        }
        return res;
    }

    /*
    Stringify Postcode object
     */
    public static String Location_info(String postcode) throws IOException, InterruptedException {
        JSONObject obj = new JSONObject(GET_request("",postcode));
        String Location="Postcode:"+postcode+"\n"
                +"Country:"+obj.getJSONObject("result").getString("country")+"\n"
                +"Region:"+obj.getJSONObject("result").getString("region");
        return Location;
    }

    /*
    Check the validity of a specific postcode with status code
     */
    public static ArrayList validate_postcode(String postcode) throws IOException, InterruptedException {
        ArrayList res = new ArrayList();
        if(postcode!="") {
            JSONObject obj = new JSONObject(GET_request("validate", postcode));
            for (Response r : Response.values()) {
                if (r.getCode() == obj.getInt("status") && obj.getBoolean("result")!=false ) {
                    res.add(r.getResponse());
                    res.add(r.getCode());
                }else{
                    res.add(Response.values()[1].response);
                    res.add(Response.values()[1].code);
                }
            }
            return res;
        }
        return null;
    }

    /*
    Store nearest postcode as keys to Postcode Object with country and region info
     */
    public static String Find_nearest_postcode_info(String postcode) throws IOException, InterruptedException {
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

    /*
    General function with validity check
     */
    public static void Find_postcode_info(String postcode) throws IOException, InterruptedException {
        if ((Integer)validate_postcode(postcode).get(1) ==200){
            System.out.println(Location_info(postcode));
            System.out.println(Find_nearest_postcode_info(postcode));
        }else{
            System.out.println("System error:"+validate_postcode(postcode).get(1)+" "+validate_postcode(postcode).get(0));
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Find_postcode_info("CB3 0FA");

        System.out.println("Wrong Input");
        Find_postcode_info("CB3 0F");
    }
}
