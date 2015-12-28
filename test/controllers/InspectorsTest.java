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
import models.Inspector;
import models.User;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.libs.Json;
import play.mvc.Result;
import play.mvc.Http.RequestBuilder;
import play.test.Helpers;

public class InspectorsTest extends BaseTest{
	@Test
    public void newInspectorJson() {
		running(fakeApplication(inMemoryDatabase()), () -> {
			newInspector();
		});
    }
	
	@Test
    public void getInspectorJson() {
		running(fakeApplication(inMemoryDatabase()), () -> {
			
			newInspector();
			login("juliogarcia","4456");
			
			User user = User.findByUsername("juliogarcia");
	    	RequestBuilder request = fakeRequest(GET, "/inspector").header("Accept", "application/json").header("X-AUTH-TOKEN", user.authtoken);
			Result result = route(request);

			assertNotNull(result);
	    	assertEquals("application/json", result.contentType());
		});
    }
	
	@Test
    public void inspectorUpdateJson() {
		running(fakeApplication(inMemoryDatabase()), () -> {
			
			newInspector();
			login("juliogarcia","4456");
			
			User user = User.findByUsername("juliogarcia");
			
			Inspector inspectorBefore = Inspector.findByUser(user);
			
			ObjectNode jsonInspector = play.libs.Json.newObject();
			jsonInspector.put("name", "Fernando");
			jsonInspector.put("nif", "20794363V");
			jsonInspector.put("phone", "789554223");
			jsonInspector.put("city", "Segovia");
			jsonInspector.put("inspectorCode", "12NRETQ");
			
	    	RequestBuilder request = fakeRequest(PUT, "/inspector").bodyJson(Json.toJson(jsonInspector)).header("Accept", "application/json").header("X-AUTH-TOKEN", user.authtoken);
			Result result = route(request);
			
			Inspector inspectorAfter = Inspector.findByUser(user);

			assertNotNull(result);
	    	assertEquals("application/json", result.contentType());
	    	assertNotSame(inspectorBefore, inspectorAfter);
			
		});
    }
	
	@Test
    public void inspectorRemoveJson() {
		running(fakeApplication(inMemoryDatabase()), () -> {
			
			newInspector();
			login("juliogarcia","4456");
			
			User user = User.findByUsername("juliogarcia");
			
			Inspector inspectorBefore = Inspector.findByUser(user);
			
	    	RequestBuilder request = fakeRequest(DELETE, "/inspector").header("X-AUTH-TOKEN", user.authtoken);
			Result result = route(request);
			
			Inspector inspectorAfter = Inspector.findByUser(user);
			
			assertNotNull(result);
			assertNotNull(inspectorBefore);
			assertNull(inspectorAfter);

		});
    }
	
	@Test
    public void getInspectorsJson() {
		running(fakeApplication(inMemoryDatabase()), () -> {
			
			newInspector();
			login("juliogarcia","4456");
			
			ObjectNode jsonUser2 = play.libs.Json.newObject();
			jsonUser2.put("username", "beatrizjimenez");
			jsonUser2.put("password", "88re");
			
			ObjectNode jsonInspector2 = play.libs.Json.newObject();
			jsonInspector2.put("name", "Beatriz");
			jsonInspector2.put("nif", "51803295G");
			jsonInspector2.put("phone", "987456991");
			jsonInspector2.put("city", "Sevilla");
			jsonInspector2.put("inspectorCode","54NRETQ");
			jsonInspector2.putPOJO("user", jsonUser2);
			
			JsonNode json2 = Json.toJson(jsonInspector2);
			RequestBuilder requestInspector2 = fakeRequest(POST, "/newInspector").bodyJson(json2).header("Accept", "application/json");
			Result resultInspector = route(requestInspector2);
			
			assertNotNull(resultInspector);
	    	assertEquals("application/json", resultInspector.contentType());
			
			User user = User.findByUsername("juliogarcia");
			
	    	RequestBuilder request = fakeRequest(GET, "/inspectors").header("Accept", "application/json").header("X-AUTH-TOKEN", user.authtoken);
			Result result = route(request);

			JsonNode response = Json.parse(Helpers.contentAsString(result));
			
			assertNotNull(result);
			assertEquals("application/json", result.contentType());
			assertEquals("2", response.get("count").toString());

		});
    }

}
