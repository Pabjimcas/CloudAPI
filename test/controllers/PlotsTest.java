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
import models.Expedient;
import models.Plot;
import models.User;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;




import play.libs.Json;
import play.mvc.Result;
import play.mvc.Http.RequestBuilder;
import play.test.Helpers;

public class PlotsTest extends BaseTest{
	
	@Test
    public void newPlotJson() {
		running(fakeApplication(inMemoryDatabase()), () -> {
			newClient();
			login("pabjimcas","1234");
			newExpedient();
			newPlot();
		});
    }
	
	@Test
    public void getPlotJson() {
		running(fakeApplication(inMemoryDatabase()), () -> {
			
			newClient();
			login("pabjimcas","1234");
			newExpedient();
			newPlot();
			
			User user = User.findByUsername("pabjimcas");
	    	RequestBuilder request = fakeRequest(GET, "/expedients/AN312/plots/17").header("Accept", "application/json").header("X-AUTH-TOKEN", user.authtoken);
			Result result = route(request);

			assertNotNull(result);
	    	assertEquals("application/json", result.contentType());
		});
    }
	
	@Test
    public void plotUpdateJson() {
		running(fakeApplication(inMemoryDatabase()), () -> {
			
			newClient();
			login("pabjimcas","1234");
			newExpedient();
			newPlot();
			
			User user = User.findByUsername("pabjimcas");
			
			Expedient expedient = Expedient.findByCode("AN312");
			
			Plot plotBefore = Plot.findByEnclosure(expedient, 12);
			
			ObjectNode jsonPlot = play.libs.Json.newObject();
			jsonPlot.put("enclosure", 2);
			jsonPlot.put("surface", 301);
			jsonPlot.put("product", "Tomato");
			
	    	RequestBuilder request = fakeRequest(PUT, "/expedients/AN312/plots/12").bodyJson(Json.toJson(jsonPlot)).header("Accept", "application/json").header("X-AUTH-TOKEN", user.authtoken);
			Result result = route(request);
			
			Plot plotAfter = Plot.findByEnclosure(expedient, 2);

			assertNotNull(result);
	    	assertEquals("application/json", result.contentType());
	    	assertNotSame(plotBefore, plotAfter);
			
		});
    }
	
	@Test
    public void plotRemoveJson() {
		running(fakeApplication(inMemoryDatabase()), () -> {
			
			newClient();
			login("pabjimcas","1234");
			newExpedient();
			newPlot();
			
			User user = User.findByUsername("pabjimcas");
			
			Expedient expedient = Expedient.findByCode("AN312");
			
			Plot plotBefore = Plot.findByEnclosure(expedient, 12);
			
	    	RequestBuilder request = fakeRequest(DELETE, "/expedients/AN312/plots/12").header("X-AUTH-TOKEN", user.authtoken);
			Result result = route(request);
			
			Plot plotAfter = Plot.findByEnclosure(expedient, 12);

			assertNotNull(result);
			assertNotNull(plotBefore);
			assertNull(plotAfter);

		});
    }
	
	@Test
    public void getPlotsJson() {
		running(fakeApplication(inMemoryDatabase()), () -> {
			
			newClient();
			login("pabjimcas","1234");
			newExpedient();
			newPlot();
			
			User user = User.findByUsername("pabjimcas");
			
	    	RequestBuilder request = fakeRequest(GET, "/expedients/AN312/plots").header("Accept", "application/json").header("X-AUTH-TOKEN", user.authtoken);
			Result result = route(request);

			JsonNode response = Json.parse(Helpers.contentAsString(result));
			
			assertNotNull(result);
			assertEquals("application/json", result.contentType());
			assertEquals("3", response.get("count").toString());

		});
    }

}
