package controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static play.test.Helpers.DELETE;
import static play.test.Helpers.GET;
import static play.test.Helpers.POST;
import static play.test.Helpers.PUT;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.route;
import static play.test.Helpers.running;

import java.util.ArrayList;
import java.util.List;

import models.Expedient;
import models.User;

import org.junit.Test;

import play.libs.Json;
import play.mvc.Result;
import play.mvc.Http.RequestBuilder;
import play.test.Helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ExpedientsTest extends BaseTest{
	
	@Test
    public void newExpedientJson() {
		running(fakeApplication(inMemoryDatabase()), () -> {
			newClient();
			login("pabjimcas","1234");
			newExpedient();
		});
    }
	
	@Test
    public void getExpedientJson() {
		running(fakeApplication(inMemoryDatabase()), () -> {
			
			newClient();
			login("pabjimcas","1234");
			newExpedient();
			
			User user = User.findByUsername("pabjimcas");
	    	RequestBuilder request = fakeRequest(GET, "/expedients/AN312").header("Accept", "application/json").header("X-AUTH-TOKEN", user.authtoken);
			Result result = route(request);

			assertNotNull(result);
	    	assertEquals("application/json", result.contentType());
		});
    }
	
	@Test
    public void expedientUpdateJson() {
		running(fakeApplication(inMemoryDatabase()), () -> {
			
			newClient();
			login("pabjimcas","1234");
			newExpedient();
			
			User user = User.findByUsername("pabjimcas");
			
			Expedient expedientBefore = Expedient.findByCode("AN312");
			
			ObjectNode jsonExpedient = play.libs.Json.newObject();
			jsonExpedient.put("code", "AN310");
			
			ObjectNode jsonPlot = play.libs.Json.newObject();
			jsonPlot.put("enclosure", 9);
			jsonPlot.put("surface", 300);
			jsonPlot.put("product", "Lemon");
			
			List<JsonNode> plotsList = new ArrayList<JsonNode>();
			plotsList.add(jsonPlot);
			
			jsonExpedient.putPOJO("plots", plotsList);
			
	    	RequestBuilder request = fakeRequest(PUT, "/expedients/AN312").bodyJson(Json.toJson(jsonExpedient)).header("Accept", "application/json").header("X-AUTH-TOKEN", user.authtoken);
			Result result = route(request);
			
			Expedient expedientAfter = Expedient.findByCode("AN310");

			assertNotNull(result);
	    	assertEquals("application/json", result.contentType());
	    	assertNotSame(expedientBefore, expedientAfter);
			
		});
    }
	
	@Test
    public void clientRemoveJson() {
		running(fakeApplication(inMemoryDatabase()), () -> {
			
			newClient();
			login("pabjimcas","1234");
			newExpedient();
			
			User user = User.findByUsername("pabjimcas");
			
			Expedient expedientBefore = Expedient.findByCode("AN312");
			
	    	RequestBuilder request = fakeRequest(DELETE, "/expedients/AN312").header("X-AUTH-TOKEN", user.authtoken);
			Result result = route(request);
			
			Expedient expedientAfter = Expedient.findByCode("AN312");

			assertNotNull(result);
			assertNotNull(expedientBefore);
			assertNull(expedientAfter);

		});
    }
	
	@Test
    public void getClientsJson() {
		running(fakeApplication(inMemoryDatabase()), () -> {
			
			newClient();
			login("pabjimcas","1234");
			newExpedient();
			
			User user = User.findByUsername("pabjimcas");
			
			ObjectNode jsonExpedient = play.libs.Json.newObject();
			jsonExpedient.put("code", "AN313");
			
			JsonNode json = Json.toJson(jsonExpedient);
			
			RequestBuilder requestExpedient = fakeRequest(POST, "/newExpedient").bodyJson(json).header("Accept", "application/json").header("X-AUTH-TOKEN", user.authtoken);
			Result resultExpedient = route(requestExpedient);
			
			assertNotNull(resultExpedient);
	    	assertEquals("application/json", resultExpedient.contentType());
			
	    	RequestBuilder request = fakeRequest(GET, "/expedients").header("Accept", "application/json").header("X-AUTH-TOKEN", user.authtoken);
			Result result = route(request);

			JsonNode response = Json.parse(Helpers.contentAsString(result));
			
			assertNotNull(result);
			assertEquals("application/json", result.contentType());
			assertEquals("2", response.get("count").toString());

		});
    }

	
	
}
