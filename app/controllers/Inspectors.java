package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import helpers.ControllerHelper;
import helpers.ExpedientJson;
import helpers.InspectorJson;
import models.Expedient;
import models.Inspector;
import models.User;
import actions.Authenticator;

import com.avaje.ebean.ExpressionList;

import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

public class Inspectors extends Controller{
	
	public Result newInspector() {
		Form<Inspector> form = Form.form(Inspector.class).bindFromRequest();
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
		Inspector inspector = form.get();
		inspector.initialization("inspector");
		inspector.save();
		if(request().accepts("application/xml")){
    		return ok(views.xml.inspectorView.render(inspector,false));
    	}else if(request().accepts("application/json")){
    		return ok(Json.toJson(inspector));
    	}
    	return badRequest("Unsupported format");
    }
	@Security.Authenticated(Authenticator.class)
	public Result getInspector() {
		User user = (User) Http.Context.current().args.get("userLogged");
		if (user.type.equals("inspector")){
			Inspector inspector = Inspector.findByUser(user);
			if(inspector == null){
				return badRequest("El inspector no existe");
			}
			if(request().accepts("application/xml")){
	    		return ok(views.xml.inspectorView.render(inspector,false));
	    	}else if(request().accepts("application/json")){
	    		return ok(Json.toJson(inspector));
	    	}
	    	return badRequest("Unsupported format");
		}
        return unauthorized("Acceso denegado");
    }
	@Security.Authenticated(Authenticator.class)
	public Result inspectorUpdate() {
		User user = (User) Http.Context.current().args.get("userLogged");
		if (user.type.equals("inspector")){
			Inspector inspector = Inspector.findByUser(user);
		if(inspector == null){
			return badRequest("El cliente no existe");
		}else{
			Form<Inspector> form = Form.form(Inspector.class).bindFromRequest();
			if (form.hasErrors()) {
				return badRequest(ControllerHelper.errorJson(2, "Datos incorrectos", form.errorsAsJson()));
			}
			if(inspector.inspectorCode.equals(form.get().inspectorCode)){
			if (inspector.updateData(form.get())) {
				inspector.update();
				if(request().accepts("application/xml")){
		    		return ok(views.xml.inspectorView.render(inspector,false));
		    	}else if(request().accepts("application/json")){
		    		return ok(Json.toJson(inspector));
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
	public Result removeInspector() {
		User user = (User) Http.Context.current().args.get("userLogged");
		if (user.type.equals("inspector")){
			Inspector inspector = Inspector.findByUser(user);
			if(inspector == null){
				return badRequest("El inspector no existe");
			}
			inspector.delete();
			return ok("Inspector eliminado :(");
		}
		return unauthorized("Acceso denegado");
    }
	
	public List<String> createListFilter(){
		List<String> filterList = new ArrayList<String>();
		filterList.add("name");
		filterList.add("lastname");
		filterList.add("city");
		filterList.add("numexpedients");
		return filterList;
	}
	
	@Security.Authenticated(Authenticator.class)
	public Result getInspectors() {
		User user = (User) Http.Context.current().args.get("userLogged");
		if (user.type.equals("inspector")){
		
		//Filter by
		Map<String,String> appliedFilters = new HashMap<String,String>();
		List<String> filterList = createListFilter();
		for(String filter: filterList){
			String value = request().getQueryString(filter);
			if(value != null){
				appliedFilters.put(filter,value);
			}
		}
		ExpressionList<Inspector> inspectors = Inspector.findBy(appliedFilters);
		
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
		Inspector.orderBy(inspectors,orderMap);
		
		Integer count = inspectors.findRowCount();
		
		//Pagination
		String limit = request().getQueryString("limit");
		String page =  request().getQueryString("page");
		
		if(limit == null){
			limit = "10";
		}
		if(page == null){
			page = "1";
		}

		Inspector.page(inspectors,limit,page);
		
		List<Inspector> listInspectors = inspectors.findList();
		if(listInspectors.size() == 0){
			return badRequest("No se han encontrado resultados en la búsqueda");
		}else{
			if(request().accepts("application/xml")){
				return ok(views.xml.inspectorsView.render(listInspectors,count));
	    	}else if(request().accepts("application/json")){
	    		List<InspectorJson> list= InspectorJson.getList(listInspectors);
	    		Map<Object,Object> result = new HashMap<Object,Object>();
				result.put("count",count);
				result.put("inspectors", list);
	    		return ok(Json.toJson(result));
	    	}
	    	return badRequest("Unsupported format");
		}
		}
		return unauthorized("Acceso denegado");
    }
	
	@Security.Authenticated(Authenticator.class)
	public Result getInspectorExpedients() {
		User user = (User) Http.Context.current().args.get("userLogged");
		if (user.type.equals("inspector")){
			
		Inspector inspector = Inspector.findByUser(user);
		ExpressionList<Expedient> expedients = Expedient.findByInspector(inspector);
		/*String usernameClient = request().getQueryString("client");
		if(usernameClient != null){
			User userClient = User.findByUsername(usernameClient);
			Client client = Client.findByUser(userClient);
			expedients = Inspector.findExpedientClient(client.id);
		}else{
			expedients = Expedient.find.where();
		}*/
		

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
