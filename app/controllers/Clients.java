package controllers;
import helpers.ClientJson;
import helpers.ControllerHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Client;
import models.User;
import play.mvc.Http;
import actions.Authenticator;

import com.avaje.ebean.ExpressionList;

import play.data.Form;
import play.libs.Json;
import play.mvc.*;


public class Clients extends Controller{
	
	public Result newClient() {
		Form<Client> form = Form.form(Client.class).bindFromRequest();
		if (form.hasErrors()) {
			return badRequest(ControllerHelper.errorJson(2, "Datos incorrectos", form.errorsAsJson()));
		}
		User user = form.get().user; 
		if(user.username == null){
			return badRequest("El email es obligatorio");
		}else{
			if(User.findByUsername(user.username) != null){
				return badRequest("El usuario ya existe");
			}
		}
		if(user.password == null){
			return badRequest("La contraseña es obligatoria");
		}
		Client client = form.get();
		client.initialization("client");
		client.save();
		if(request().accepts("application/xml")){
    		return ok(views.xml.clientView.render(client));
    	}else if(request().accepts("application/json")){
    		return ok(Json.toJson(client));
    	}
    	return badRequest("Unsupported format");
    }
	
	@Security.Authenticated(Authenticator.class)
	public Result getClient() {
		User user = (User) Http.Context.current().args.get("userLogged");
		if (user.type.equals("client")){
			Client client = Client.findByUser(user);
			if(client == null){
				return badRequest("El cliente no existe");
			}
			if(request().accepts("application/xml")){
	    		return ok(views.xml.clientView.render(client));
	    	}else if(request().accepts("application/json")){
	    		return ok(Json.toJson(client));
	    	}
	    	return badRequest("Unsupported format");
		}
        return unauthorized("Acceso denegado");
    }
	
	@Security.Authenticated(Authenticator.class)
	public Result clientUpdate() {
		User user = (User) Http.Context.current().args.get("userLogged");
		if (user.type.equals("client")){
			Client client = Client.findByUser(user);
		if(client == null){
			return badRequest("El cliente no existe");
		}else{
			Form<Client> form = Form.form(Client.class).bindFromRequest();
			if (form.hasErrors()) {
				return badRequest(ControllerHelper.errorJson(2, "Datos incorrectos", form.errorsAsJson()));
			}
			if (client.updateData(form.get())) {
				client.update();
				if(request().accepts("application/xml")){
		    		return ok(views.xml.clientView.render(client));
		    	}else if(request().accepts("application/json")){
		    		return ok(Json.toJson(client));
		    	}else{
		    		return badRequest("Unsupported format");
		    	}
			}
			else {
				return status(NOT_MODIFIED);
			}
		}
		}
		
		return unauthorized("Acceso denegado");
    }
	
	@Security.Authenticated(Authenticator.class)
	public Result removeClient () {
		User user = (User) Http.Context.current().args.get("userLogged");
		if (user.type.equals("client")){
			Client client = Client.findByUser(user);
			if(client == null){
				return badRequest("El cliente no existe");
			}
		client.delete();
		return ok("Cliente eliminado :(");
		}
		return unauthorized("Acceso denegado");
    }
	
	public List<String> createListFilter(){
		List<String> filterList = new ArrayList<String>();
		filterList.add("name");
		filterList.add("lastname");
		filterList.add("city");
		return filterList;
	}
		
	@Security.Authenticated(Authenticator.class)
	public Result getClients(){
		
		//Filter by
		Map<String,String> appliedFilters = new HashMap<String,String>();
		List<String> filterList = createListFilter();
		for(String filter: filterList){
			String value = request().getQueryString(filter);
			if(value != null){
				appliedFilters.put(filter,value);
			}
		}
		ExpressionList<Client> clients = Client.findBy(appliedFilters);
		
		//Order by
		Map<String,String> orderMap = new HashMap<String,String>();
		String order = request().getQueryString("order");
		if(order != null){
			String[] orderArray = order.split(",");
			for(String element: orderArray){
				String direction = "asc";
				if (element.charAt(0) == '-'){
					direction = "desc";
					element = element.substring(1);
				}
				if(!filterList.contains(element)){
					return badRequest("El elemento '"+element+"' no existe");
				}else{
					orderMap.put(element,direction);
				}
			}
		}
		Client.orderBy(clients,orderMap);
		
		Integer count = clients.findRowCount();
		
		//Pagination
		String limit = request().getQueryString("limit");
		String page =  request().getQueryString("page");
		
		if(limit == null){
			limit = "10";
		}
		if(page == null){
			page = "1";
		}

		List<Client> listClients = Client.page(clients,limit,page);
		if(listClients.size() == 0){
			return badRequest("No se han encontrado resultados en la búsqueda");
		}else{
			if(request().accepts("application/xml")){
				return ok(views.xml.clientsView.render(listClients,count));
	    	}else if(request().accepts("application/json")){
	    		List<ClientJson> list= ClientJson.getList(listClients);
	    		Map<Object,Object> result = new HashMap<Object,Object>();
				result.put("count",count);
				result.put("clients", list);
	    		return ok(Json.toJson(result));
	    	}
	    	return badRequest("Unsupported format");
		}
    }
}


