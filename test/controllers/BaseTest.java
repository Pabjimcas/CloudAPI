package controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static play.test.Helpers.POST;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.route;

import java.util.ArrayList;
import java.util.List;

import models.User;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Http.RequestBuilder;
import play.test.Helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class BaseTest {
	
	public static JsonNode clientData(){
		
		ObjectNode jsonUser = play.libs.Json.newObject();
		jsonUser.put("username", "pabjimcas");
		jsonUser.put("password", "1234");
		
		ObjectNode jsonClient = play.libs.Json.newObject();
		jsonClient.put("name", "Pablo");
		jsonClient.put("nif", "77802588M");
		jsonClient.put("phone", "651234432");
		jsonClient.put("city", "Sevilla");
		jsonClient.putPOJO("user", jsonUser);
		
		JsonNode json = Json.toJson(jsonClient);
		return json;
	}
	
	public static JsonNode inspectorData(){
		
		ObjectNode jsonUser = play.libs.Json.newObject();
		jsonUser.put("username", "juliogarcia");
		jsonUser.put("password", "4456");
		
		ObjectNode jsonInspector = play.libs.Json.newObject();
		jsonInspector.put("name", "Julio");
		jsonInspector.put("nif", "98368015M");
		jsonInspector.put("phone", "666888777");
		jsonInspector.put("city", "Salamanca");
		jsonInspector.put("inspectorCode","12NRETQ");
		jsonInspector.putPOJO("user", jsonUser);
		
		JsonNode json = Json.toJson(jsonInspector);
		return json;
	}
	
	public static JsonNode expedientData(){
		
		ObjectNode jsonExpedient = play.libs.Json.newObject();
		jsonExpedient.put("code", "AN312");
		
		ObjectNode jsonPlot1 = play.libs.Json.newObject();
		jsonPlot1.put("enclosure", 12);
		jsonPlot1.put("surface", 135.24);
		jsonPlot1.put("product", "Orange");
		
		ObjectNode jsonPlot2 = play.libs.Json.newObject();
		jsonPlot2.put("enclosure", 10);
		jsonPlot2.put("surface", 200);
		jsonPlot2.put("product", "Banana");
		
		List<JsonNode> plotsList = new ArrayList<JsonNode>();
		plotsList.add(jsonPlot1);
		plotsList.add(jsonPlot2);
		
		jsonExpedient.putPOJO("plots", plotsList);
		
		JsonNode json = Json.toJson(jsonExpedient);
		return json;
	}
	
	public static JsonNode plotData(){
		
		ObjectNode jsonPlot = play.libs.Json.newObject();
		jsonPlot.put("enclosure", 17);
		jsonPlot.put("surface", 135.24);
		jsonPlot.put("product", "Orange");
		
		JsonNode json = Json.toJson(jsonPlot);
		
		return json;
	}
	
	public static JsonNode markData(){
		
		ObjectNode jsonMark = play.libs.Json.newObject();
		jsonMark.put("mark", 7.8);
		
		JsonNode json = Json.toJson(jsonMark);
		return json;
	}
	
	public void newClient(){
		JsonNode jsonClient = clientData();
		RequestBuilder request = fakeRequest(POST, "/newClient").bodyJson(jsonClient).header("Accept", "application/json");
    	Result result = route(request);
    	assertNotNull(result);
    	assertEquals("application/json", result.contentType());
	}
	public void newInspector(){
		JsonNode jsonInspector = inspectorData();
		RequestBuilder request = fakeRequest(POST, "/newInspector").bodyJson(jsonInspector).header("Accept", "application/json");
    	Result result = route(request);
    	
    	assertNotNull(result);
    	assertEquals("application/json", result.contentType());
	}
	
	public void login(String username, String password){
		ObjectNode jsonUser = play.libs.Json.newObject();
		jsonUser.put("username", username);
		jsonUser.put("password", password);
		JsonNode json = Json.toJson(jsonUser);
		
		RequestBuilder request = fakeRequest(POST, "/login").bodyJson(json).header("Accept", "application/json");
		Result result = route(request);
		assertNotNull(result);
    	assertEquals("application/json", result.contentType());
	}
	
	public void newExpedient(){
		JsonNode jsonExpedient = expedientData();
		User user = User.findByUsername("pabjimcas");
		
		RequestBuilder request = fakeRequest(POST, "/newExpedient").bodyJson(jsonExpedient).header("Accept", "application/json").header("X-AUTH-TOKEN", user.authtoken);
    	Result result = route(request);
    	
    	assertNotNull(result);
    	assertEquals("application/json", result.contentType());
	}
	
	public void newPlot(){
		JsonNode jsonPlot = plotData();
		User user = User.findByUsername("pabjimcas");
		
		RequestBuilder request = fakeRequest(POST, "/expedients/AN312/newPlot").bodyJson(jsonPlot).header("Accept", "application/json").header("X-AUTH-TOKEN", user.authtoken);
    	Result result = route(request);
    	
    	assertNotNull(result);
    	assertEquals("application/json", result.contentType());
	}
	
	public void newMark(){
		JsonNode jsonMark = markData();
		User user = User.findByUsername("juliogarcia");
		
		RequestBuilder request = fakeRequest(POST, "/expedients/AN312/newMark").bodyJson(jsonMark).header("Accept", "application/json").header("X-AUTH-TOKEN", user.authtoken);
    	Result result = route(request);
    	
    	assertNotNull(result);
    	assertEquals("application/json", result.contentType());
	}
}
