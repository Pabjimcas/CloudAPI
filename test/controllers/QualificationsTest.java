package controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static play.test.Helpers.DELETE;
import static play.test.Helpers.GET;
import static play.test.Helpers.PUT;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.route;
import static play.test.Helpers.running;
import models.Qualification;
import models.User;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.libs.Json;
import play.mvc.Result;
import play.mvc.Http.RequestBuilder;
import play.test.Helpers;

public class QualificationsTest extends BaseTest{

	@Test
    public void newMarkJson() {
		running(fakeApplication(inMemoryDatabase()), () -> {
			newClient();
			login("pabjimcas","1234");
			newExpedient();
			newInspector();
			login("juliogarcia","4456");
			newMark();
		});
    }
	
	@Test
    public void getMarkJson() {
		running(fakeApplication(inMemoryDatabase()), () -> {
			
			newClient();
			login("pabjimcas","1234");
			newExpedient();
			newInspector();
			login("juliogarcia","4456");
			newMark();
			
			User user = User.findByUsername("juliogarcia");
	    	RequestBuilder request = fakeRequest(GET, "/expedients/AN312/marks/1").header("Accept", "application/json").header("X-AUTH-TOKEN", user.authtoken);
			Result result = route(request);

			assertNotNull(result);
	    	assertEquals("application/json", result.contentType());
		});
    }
	
	@Test
    public void markUpdateJson() {
		running(fakeApplication(inMemoryDatabase()), () -> {
			
			newClient();
			login("pabjimcas","1234");
			newExpedient();
			newInspector();
			login("juliogarcia","4456");
			newMark();
			
			User user = User.findByUsername("juliogarcia");
			
			Qualification markBefore = Qualification.find.byId(new Long(1));
			
			ObjectNode jsonMark = play.libs.Json.newObject();
			jsonMark.put("mark", 8);
			
	    	RequestBuilder request = fakeRequest(PUT, "/expedients/AN312/marks/1").bodyJson(Json.toJson(jsonMark)).header("Accept", "application/json").header("X-AUTH-TOKEN", user.authtoken);
			Result result = route(request);
			
			Qualification markAfter = Qualification.find.byId(new Long(1));

			assertNotNull(result);
	    	assertEquals("application/json", result.contentType());
	    	assertNotSame(markBefore, markAfter);
			
		});
    }
	
	@Test
    public void markRemoveJson() {
		running(fakeApplication(inMemoryDatabase()), () -> {
			
			newClient();
			login("pabjimcas","1234");
			newExpedient();
			newInspector();
			login("juliogarcia","4456");
			newMark();
			
			User user = User.findByUsername("juliogarcia");
			
			Qualification markBefore = Qualification.find.byId(new Long(1));
			
	    	RequestBuilder request = fakeRequest(DELETE, "/expedients/AN312/marks/1").header("X-AUTH-TOKEN", user.authtoken);
			Result result = route(request);
			
			Qualification markAfter = Qualification.find.byId(new Long(1));

			assertNotNull(result);
			assertNotNull(markBefore);
			assertNull(markAfter);

		});
    }
	
	@Test
    public void getMarksJson() {
		running(fakeApplication(inMemoryDatabase()), () -> {
			
			newClient();
			login("pabjimcas","1234");
			newExpedient();
			newInspector();
			login("juliogarcia","4456");
			newMark();
			
			User user = User.findByUsername("juliogarcia");
			
	    	RequestBuilder request = fakeRequest(GET, "/expedients/AN312/marks").header("Accept", "application/json").header("X-AUTH-TOKEN", user.authtoken);
			Result result = route(request);

			JsonNode response = Json.parse(Helpers.contentAsString(result));
			
			assertNotNull(result);
			assertEquals("application/json", result.contentType());
			assertEquals("1", response.get("count").toString());

		});
    }

}
