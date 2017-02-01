
package com.linecorp.example.linebot;

import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.*;

import org.apache.http.HttpResponse;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.client.methods.HttpPost;

import org.json.JSONObject;
import org.json.JSONArray;
import com.google.gson.Gson;

import com.linecorp.bot.client.LineSignatureValidator;

@RestController
@RequestMapping(value="/linebot")
public class LineBotController
{
    @Autowired
    @Qualifier("com.linecorp.channel_secret")
    String lChannelSecret;
    
    @Autowired
    @Qualifier("com.linecorp.channel_access_token")
    String lChannelAccessToken;
    
    @RequestMapping(value="/callback", method=RequestMethod.POST)
    public ResponseEntity<String> callback(
        @RequestHeader("X-Line-Signature") String aXLineSignature,
        @RequestBody String aPayload)
    {
        // compose body
        final String text=String.format("The Signature is: %s",
            (aXLineSignature!=null && aXLineSignature.length() > 0) ? aXLineSignature : "N/A");
        
        System.out.println(text);
        
        final boolean valid=new LineSignatureValidator(lChannelSecret.getBytes()).validateSignature(aPayload.getBytes(), aXLineSignature);
        
        System.out.println("The signature is: " + (valid ? "valid" : "tidak valid"));
        
        //Get events from source
        if(aPayload!=null && aPayload.length() > 0)
        {
            System.out.println("Payload: " + aPayload);
        }
        
        Gson gson = new Gson();
        Payload payload = gson.fromJson(aPayload, Payload.class);
        String idTarget = payload.events[0].source.userId;
        System.out.println("ID Target: " + idTarget);
        String messageText = payload.events[0].message.text;
        System.out.println("Text Message: " + messageText);
        
        pushManual(idTarget, messageText);
         
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    private void pushManual(String id_target, String message_text){
        String url = "https://api.line.me/v2/bot/message/push";
        
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);
        
        try{
            // add header
            post.setHeader("Content-Type", "application/json");
            post.setHeader("Authorization", "Bearer " + lChannelAccessToken);

            String jsonData = "{\"to\":\""+id_target+"\",\"messages\":[{\"type\":\"text\",\"text\":\""+message_text+"\"}]}";
            System.out.println(jsonData);
            
            StringEntity params =new StringEntity(jsonData);
            
            post.setEntity(params);
            
            HttpResponse response = client.execute(post);
            System.out.println("Response Code : "
                               + response.getStatusLine().getStatusCode());
            
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            
            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
        } catch (IOException e){
            System.out.println("Exception is raised ");
            e.printStackTrace();
        }
    }
}
