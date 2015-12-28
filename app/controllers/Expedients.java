package controllers;

import helpers.ControllerHelper;
import helpers.ExpedientJson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Client;
import models.Expedient;
import models.User;
import actions.Authenticator;

import com.avaje.ebean.ExpressionList;

import play.data.Form;
import play.libs.Json;
import play.mvc.*;


public class Expedients extends Controller{
	@Security.Authenticated(Authenticator.class)
	public Result newExpedient() {
		User user = (User) Http.Context.current().args.get("userLogged");
		if (user.type.equals("client")){
			Client client = Client.findByUser(user);
			if(client == null){
				return badRequest("El cliente no existe");
			}
			Form<Expedient> form = Form.form(Expedient.class).bindFromRequest();
			if (form.hasErrors()) {
				return badRequest(ControllerHelper.errorJson(2, "Datos incorrectos", form.errorsAsJson()));
			}
			Expedient expedient = form.get();
			expedient.initialization(client);
			expedient.save();
			if(request().accepts("application/xml")){
	    		return ok(views.xml.expedientView.render(expedient,false));
	    	}else if(request().accepts("application/json")){
	    		return ok(Json.toJson(expedient));
	    	}
	    	return badRequest("Unsupported format");
		}
		return unauthorized("Acceso denegado");
    }
	
	@Security.Authenticated(Authenticator.class)
	public Result getExpedient(String codeExp) {
		User user = (User) Http.Context.current().args.get("userLogged");
		Boolean allowSee = false;
		if(user != null){
			Expedient expedient = Expedient.findByCode(codeExp);
			if(expedient == null){
				return badRequest("El expediente no existe");
			}
		if (user.type.equals("client")){
			Client client = Client.findByUser(user);
			if(client.hasExpedient(expedient)){
				allowSee = true;
			}
		}else{
			allowSee = true;
		}
			if(allowSee){
				if(request().accepts("application/xml")){
		    		return ok(views.xml.expedientView.render(expedient,false));
		    	}else if(request().accepts("application/json")){
		    		return ok(Json.toJson(expedient));
		    	}else{
		    		return badRequest("Unsupported format");
		    	}
			}
		}
		return unauthorized("Acceso denegado");	
    }
	@Security.Authenticated(Authenticator.class)
	public Result expedientUpdate(String codeExp) {
		User user = (User) Http.Context.current().args.get("userLogged");
		if (user.type.equals("client")){
			Client client = Client.findByUser(user);
			if(client == null){
				return badRequest("El cliente no existe");
			}
		Expedient expedient = Expedient.findByCode(codeExp);
		if(expedient == null){
			return badRequest("El expediente no existe");
		}else{
			if(client.hasExpedient(expedient)){
			Form<Expedient> form = Form.form(Expedient.class).bindFromRequest();
			if (form.hasErrors()) {
				return badRequest(ControllerHelper.errorJson(2, "Datos incorrectos", form.errorsAsJson()));
			}
			if (expedient.updateData(form.get())) {
				expedient.update();
				if(request().accepts("application/xml")){
		    		return ok(views.xml.expedientView.render(expedient,false));
		    	}else if(request().accepts("application/json")){
		    		return ok(Json.toJson(expedient));
		    	}else{
		    		return badRequest("Unsupported format");
		    	}
			}
			else {
				return status(NOT_MODIFIED);
			}
			}
		}
		}
		return unauthorized("Acceso denegado");	
    }
	
	
	
	@Security.Authenticated(Authenticator.class)
	public Result removeExpedient(String codeExp) {
		User user = (User) Http.Context.current().args.get("userLogged");
		if (user.type.equals("client")){
			Client client = Client.findByUser(user);
			Expedient expedient = Expedient.findByCode(codeExp);
			if(expedient == null){
				return badRequest("El expediente no existe");
			}
			if(client.hasExpedient(expedient)){
				expedient.delete();
		        return ok("Expediente eliminado :(");
			}
		}
		return unauthorized("Acceso denegado");	
    }
	
	public List<String> createListFilter(){
		List<String> filterList = new ArrayList<String>();
		filterList.add("min_mark");
		filterList.add("max_mark");
		filterList.add("mark");
		return filterList;
	}
	
	@Security.Authenticated(Authenticator.class)
	public Result getExpedients(){
		User user = (User) Http.Context.current().args.get("userLogged");
		if(user != null){
			
			ExpressionList<Expedient> expedients ;
			if (user.type.equals("client")){
				Client client = Client.findByUser(user);
				expedients = Expedient.findByClient(client.id);	
			}else{
				String usernameClient = request().getQueryString("client");
				if(usernameClient != null){
					User userClient = User.findByUsername(usernameClient);
					Client client = Client.findByUser(userClient);
					expedients = Expedient.findByClient(client.id);
				}else{
					expedients = Expedient.find.where();
				}
			}

		Integer count = expedients.findRowCount();
		
		//Pagination
		String limit = request().getQueryString("limit");
		String page =  request().getQueryString("page");
		if(limit == null){
			limit = "10";
		}
		if(page == null){
			page = "1";
		}
		Expedient.page(expedients,limit,page);
		
		List<Expedient> listExpedients = expedients.findList();
		
		if(listExpedients.size() == 0){
			return badRequest("No se han encontrado resultados en la búsqueda");
		}else{
			if(request().accepts("application/xml")){
	    		return ok(views.xml.expedientsView.render(listExpedients,count));
	    	}else if(request().accepts("application/json")){
	    		Map<String,Object> result = new HashMap<String,Object>();
	    		List<ExpedientJson> list = ExpedientJson.getList(listExpedients);
	    		result.put("count",count);
	    		result.put("expedients",list);
	    		return ok(Json.toJson(result));
	    	}
	    	return badRequest("Unsupported format");
		}
    }
		return unauthorized("Acceso denegado");	
	}
	
}