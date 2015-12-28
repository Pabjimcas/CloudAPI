package controllers;

import static play.test.Helpers.GET;
import static play.test.Helpers.POST;
import static play.test.Helpers.PUT;
import static play.test.Helpers.DELETE;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.route;
import static play.test.Helpers.running;
import static org.junit.Assert.*;

import models.Client;
import models.User;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.libs.Json;
import play.mvc.Http.RequestBuilder;
import play.mvc.Result;
import play.test.Helpers;

public class ClientsTest extends BaseTest{
	
	@Test
    public void newClientJson() {
		running(fakeApplication(inMemoryDatabase()), () -> {
			newClient();
		});
    }
	
	@Test
    public void getClientJson() {
		running(fakeApplication(inMemoryDatabase()), () -> {
			
			newClient();
			login("pabjimcas","1234");
			
			User user = User.findByUsername("pabjimcas");
	    	RequestBuilder request = fakeRequest(GET, "/client").header("Accept", "application/json").header("X-AUTH-TOKEN", user.authtoken);
			Result result = route(request);

			assertNotNull(result);
	    	assertEquals("application/json", result.contentType());
		});
    }
	
	@Test
    public void clientUpdateJson() {
		running(fakeApplication(inMemoryDatabase()), () -> {
			
			newClient();
			login("pabjimcas","1234");
			
			User user = User.findByUsername("pabjimcas");
			
			Client clientBefore = Client.findByUser(user);
			
			ObjectNode jsonClient = play.libs.Json.newObject();
			jsonClient.put("name", "Pedro");
			jsonClient.put("nif", "28336028M");
			jsonClient.put("phone", "987456321");
			jsonClient.put("city", "Salamanca");
			
			
	    	RequestBuilder request = fakeRequest(PUT, "/client").bodyJson(Json.toJson(jsonClient)).header("Accept", "application/json").header("X-AUTH-TOKEN", user.authtoken);
			Result result = route(request);
			
			Client clientAfter = Client.findByUser(user);

			assertNotNull(result);
	    	assertEquals("application/json", result.contentType());
	    	assertNotSame(clientBefore, clientAfter);
			
		});
    }
	
	@Test
    public void clientRemoveJson() {
		running(fakeApplication(inMemoryDatabase()), () -> {
			
			newClient();
			login("pabjimcas","1234");
			
			User user = User.findByUsername("pabjimcas");
			
			Client clientBefore = Client.findByUser(user);
			
	    	RequestBuilder request = fakeRequest(DELETE, "/client").header("X-AUTH-TOKEN", user.authtoken);
			Result result = route(request);
			
			Client clientAfter = Client.findByUser(user);

			assertNotNull(result);
			assertNotNull(clientBefore);
			assertNull(clientAfter);

		});
    }
	
	@Test
    public void getClientsJson() {
		running(fakeApplication(inMemoryDatabase()), () -> {
			
			newClient();
			login("pabjimcas","1234");
			
			ObjectNode jsonUser2 = play.libs.Json.newObject();
			jsonUser2.put("username", "pedroximenez");
			jsonUser2.put("password", "78965");
			
			ObjectNode jsonClient2 = play.libs.Json.newObject();
			jsonClient2.put("name", "Pedro");
			jsonClient2.put("nif", "28336028M");
			jsonClient2.put("phone", "987456321");
			jsonClient2.put("city", "Salamanca");
			jsonClient2.putPOJO("user", jsonUser2);
			
			JsonNode json2 = Json.toJson(jsonClient2);
			RequestBuilder requestClient2 = fakeRequest(POST, "/newClient").bodyJson(json2).header("Accept", "application/json");
			Result resultClient = route(requestClient2);
			
			assertNotNull(resultClient);
	    	assertEquals("application/json", resultClient.contentType());
			
			User user = User.findByUsername("pabjimcas");
			
	    	RequestBuilder request = fakeRequest(GET, "/clients").header("Accept", "application/json").header("X-AUTH-TOKEN", user.authtoken);
			Result result = route(request);

			JsonNode response = Json.parse(Helpers.contentAsString(result));
			
			assertNotNull(result);
			assertEquals("application/json", result.contentType());
			assertEquals("2", response.get("count").toString());

		});
    }

}
