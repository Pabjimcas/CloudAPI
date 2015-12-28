package controllers;

import helpers.ControllerHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Client;
import models.Expedient;
import models.Plot;
import models.User;
import actions.Authenticator;

import com.avaje.ebean.ExpressionList;

import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

public class Plots extends Controller{
	
	@Security.Authenticated(Authenticator.class)
	public Result newPlot(String codeExp) {
		User user = (User) Http.Context.current().args.get("userLogged");
		if (user.type.equals("client")){
			Client client = Client.findByUser(user);
			if(client == null){
				return badRequest("El cliente no existe");
			}
			Expedient expedient = Expedient.findByCode(codeExp);
			if(expedient == null){
				return badRequest("El expediente no existe");
			}
			if(client.hasExpedient(expedient)){
			Form<Plot> form = Form.form(Plot.class).bindFromRequest();
			if (form.hasErrors()) {
				return badRequest(ControllerHelper.errorJson(2, "Datos incorrectos", form.errorsAsJson()));
			}
			Plot plot = form.get();
			plot.initialization(expedient);
			plot.save();
			if(request().accepts("application/xml")){
	    		return ok(views.xml.plotView.render(plot));
	    	}else if(request().accepts("application/json")){
	    		return ok(Json.toJson(plot));
	    	}
	    	return badRequest("Unsupported format");
		}
		}
		return unauthorized("Acceso denegado");
    }
	
	@Security.Authenticated(Authenticator.class)
	public Result getPlot(String codeExp,Integer enc) {
		User user = (User) Http.Context.current().args.get("userLogged");
		if (user.type.equals("client")){
			Client client = Client.findByUser(user);
			if(client == null){
				return badRequest("El cliente no existe");
			}
			Expedient expedient = Expedient.findByCode(codeExp);
			if(expedient == null){
				return badRequest("El expediente no existe");
			}
			Plot plot = Plot.findByEnclosure(expedient,enc);
			if(plot == null){
				return badRequest("La parcela no existe");
			}
			if(client.hasExpedient(expedient) && expedient.hasPlot(plot)){
				if(request().accepts("application/xml")){
		    		return ok(views.xml.plotView.render(plot));
		    	}else if(request().accepts("application/json")){
		    		return ok(Json.toJson(plot));
		    	}
		    	return badRequest("Unsupported format");
			}
		}
		return unauthorized("Acceso denegado");
    }
	
	@Security.Authenticated(Authenticator.class)
	public Result plotUpdate(String codeExp,Integer enc) {
		User user = (User) Http.Context.current().args.get("userLogged");
		if (user.type.equals("client")){
			Client client = Client.findByUser(user);
			if(client == null){
				return badRequest("El cliente no existe");
			}
			Expedient expedient = Expedient.findByCode(codeExp);
			if(expedient == null){
				return badRequest("El expediente no existe");
			}
			Plot plot = Plot.findByEnclosure(expedient,enc);
			if(plot == null){
				return badRequest("La parcela no existe");
			}
			if(client.hasExpedient(expedient) && expedient.hasPlot(plot)){
				Form<Plot> form = Form.form(Plot.class).bindFromRequest();
				if (form.hasErrors()) {
					return badRequest(ControllerHelper.errorJson(2, "Datos incorrectos", form.errorsAsJson()));
				}
			if (plot.updateData(form.get())) {
				plot.update();
				if(request().accepts("application/xml")){
		    		return ok(views.xml.plotView.render(plot));
		    	}else if(request().accepts("application/json")){
		    		return ok(Json.toJson(plot));
		    	}
		    	return badRequest("Unsupported format");
			}
			else {
				return status(NOT_MODIFIED);
			}
			}
		}
		return unauthorized("Acceso denegado");
    }
	@Security.Authenticated(Authenticator.class)
	public Result removePlot(String codeExp,Integer enc) {
		User user = (User) Http.Context.current().args.get("userLogged");
		if (user.type.equals("client")){
			Client client = Client.findByUser(user);
			if(client == null){
				return badRequest("El cliente no existe");
			}
			Expedient expedient = Expedient.findByCode(codeExp);
			if(expedient == null){
				return badRequest("El expediente no existe");
			}
			Plot plot = Plot.findByEnclosure(expedient,enc);
			if(plot == null){
				return badRequest("La parcela no existe");
			}
			if(client.hasExpedient(expedient) && expedient.hasPlot(plot)){
				plot.delete();
				return badRequest("parcela eliminada");
			}
		}
		return unauthorized("Acceso denegado");
    }
	
	public List<String> findList(){
		List<String> list = new ArrayList<String>();
		list.add("product");
		list.add("harvest");
		list.add("surface");
		list.add("enclosure");
		return list;
	}
	
	public List<String> createListFilter(){
		List<String> filterList = new ArrayList<String>();
		filterList.add("max_harvest");
		filterList.add("min_harvest");
		filterList.add("max_suface");
		filterList.add("min_surface");
		return filterList;
	}
	
	@Security.Authenticated(Authenticator.class)
	public Result getPlots(String codeExp) {
		User user = (User) Http.Context.current().args.get("userLogged");
		if (user.type.equals("client")){
			Client client = Client.findByUser(user);
			if(client == null){
				return badRequest("El cliente no existe");
			}
			Expedient expedient = Expedient.findByCode(codeExp);
			if(expedient == null){
				return badRequest("El expediente no existe");
			}
			Map<String,String> filters = new HashMap<String,String>();
			Map<String,String> orderMap = new HashMap<String,String>();
			List<String> findList = findList();
			List<String> filterList = createListFilter();
			
			filterList.addAll(findList);
			for(String filter: filterList){
				String element = request().getQueryString(filter);
				if(element != null){
					filters.put(filter,element);
				}
			}
			ExpressionList<Plot> plots = Plot.findBy(expedient.id,filters);
			
			String order = request().getQueryString("order");
			if(order != null){
				String[] orderArray = order.split(",");
				for(String element: orderArray){
					String direction = "asc";
					if (element.charAt(0) == '-'){
						direction = "desc";
						element = element.substring(1);
					}
					if(!findList.contains(element)){
						return badRequest("El elemento '"+element+"' no existe");
					}else{
						orderMap.put(element,direction);
					}
				}
			}
			Plot.orderBy(plots,orderMap);
			Integer count = plots.findRowCount();
			
			//Pagination
			String limit = request().getQueryString("limit");
			String page =  request().getQueryString("page");
			
			if(limit == null){
				limit = "10";
			}
			if(page == null){
				page = "1";
			}

			Plot.page(plots,limit,page);
			List<Plot> listPlots = plots.findList();
			
		
		if(listPlots.size() == 0){
			return badRequest("No se han encontrado resultados en la búsqueda");
		}else{
			if(request().accepts("application/xml")){
	    		return ok(views.xml.plotsView.render(listPlots,count));
	    	}else if(request().accepts("application/json")){
	    		Map<String,Object> result = new HashMap<String,Object>();
	    		result.put("count",count);
	    		result.put("plots",listPlots);
	    		return ok(Json.toJson(result));
	    	}
	    	}
	    	return badRequest("Unsupported format");
		}
		return unauthorized("Acceso denegado"); 
    }
}
