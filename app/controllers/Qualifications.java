package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.avaje.ebean.ExpressionList;

import helpers.ControllerHelper;
import models.Expedient;
import models.Inspector;
import models.Qualification;
import models.User;
import actions.Authenticator;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

public class Qualifications extends Controller {

	@Security.Authenticated(Authenticator.class)
	public Result newMark(String codeExp) {
		User user = (User) Http.Context.current().args.get("userLogged");
		if (user.type.equals("inspector")) {
			Inspector inspector = Inspector.findByUser(user);
			Expedient expedient = Expedient.findByCode(codeExp);
			if (expedient == null) {
				return badRequest("El expediente no existe");
			}
			Form<Qualification> form = Form.form(Qualification.class).bindFromRequest();
			if (form.hasErrors()) {
				return badRequest(ControllerHelper.errorJson(2,
						"Datos incorrectos", form.errorsAsJson()));
			}
			Qualification mark = form.get();
			mark.initialization(expedient, inspector);
			mark.save();
			expedient.MarkCalculate();
			if (request().accepts("application/xml")) {
				return ok(views.xml.qualificationView.render(mark));
			} else if (request().accepts("application/json")) {
				return ok(Json.toJson(mark));
			}
			return badRequest("Unsupported format");
		}
		return unauthorized("Acceso denegado");
	}

	@Security.Authenticated(Authenticator.class)
	public Result getMark(String codeExp, Long idMark) {
		Expedient expedient = Expedient.findByCode(codeExp);
		if (expedient == null) {
			return badRequest("El expediente no existe");
		}
		Qualification mark = Qualification.find.byId(idMark);
		if (mark == null) {
			return badRequest("La nota no existe");
		}
		if (request().accepts("application/xml")) {
			return ok(views.xml.qualificationView.render(mark));
		} else if (request().accepts("application/json")) {
			return ok(Json.toJson(mark));
		}
		return badRequest("Unsupported format");
	}
	
	@Security.Authenticated(Authenticator.class)
	public Result markUpdate(String codeExp,Long idMark) {
		User user = (User) Http.Context.current().args.get("userLogged");
		if (user.type.equals("inspector")){
			Inspector inspector = Inspector.findByUser(user);
			Expedient expedient = Expedient.findByCode(codeExp);
			if(expedient == null){
				return badRequest("El expediente no existe");
			}
			Qualification mark = Qualification.find.byId(idMark);
			if(mark == null){
				return badRequest("La nota no existe");
			}
			if(mark.inspector.equals(inspector.inspectorCode)){
				Form<Qualification> form = Form.form(Qualification.class).bindFromRequest();
				if (form.hasErrors()) {
					return badRequest(ControllerHelper.errorJson(2, "Datos incorrectos", form.errorsAsJson()));
				}
			if (mark.updateData(form.get())) {
				mark.update();
				expedient.MarkCalculate();
				if(request().accepts("application/xml")){
		    		return ok(views.xml.qualificationView.render(mark));
		    	}else if(request().accepts("application/json")){
		    		return ok(Json.toJson(mark));
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
	public Result removeMark(String codeExp,Long idMark) {
		User user = (User) Http.Context.current().args.get("userLogged");
		if (user.type.equals("inspector")){
			Inspector inspector = Inspector.findByUser(user);
			Expedient expedient = Expedient.findByCode(codeExp);
			if(expedient == null){
				return badRequest("El expediente no existe");
			}
			Qualification mark = Qualification.find.byId(idMark);
			if(mark == null){
				return badRequest("La nota no existe");
			}
			if(mark.inspector.equals(inspector.inspectorCode)){
				mark.delete();
				expedient.MarkCalculate();
				return badRequest("nota eliminada");
			}
		}
		return unauthorized("Acceso denegado");
    }
	
	public List<String> createListFilter(){
		List<String> filterList = new ArrayList<String>();
		filterList.add("from_date");
		filterList.add("until_date");
		return filterList;
	}
	public List<String> createListOrder(){
		List<String> orderList = new ArrayList<String>();
		orderList.add("inspectorCode");
		orderList.add("mark");
		orderList.add("markDate");
		return orderList;
	}
	
	@Security.Authenticated(Authenticator.class)
	public Result getMarks(String codeExp) {
		Expedient expedient = Expedient.findByCode(codeExp);
		if(expedient == null){
			return badRequest("El expediente no existe");
		}
		Map<String,String> filters = new HashMap<String,String>();
		Map<String,String> orderMap = new HashMap<String,String>();
		List<String> filterList = createListFilter();
		List<String> orderList = createListOrder();
		
		filterList.addAll(orderList);
		
		for(String filter: filterList){
			String element = request().getQueryString(filter);
			if(element != null){
				filters.put(filter,element);
			}
		}
		ExpressionList<Qualification> marks = Qualification.findBy(expedient.id,filters);
			
		String order = request().getQueryString("order");
		if(order != null){
			String[] orderArray = order.split(",");
			for(String element: orderArray){
				String direction = "asc";
				if (element.charAt(0) == '-'){
					direction = "desc";
					element = element.substring(1);
				}
				if(!orderList.contains(element)){
					return badRequest("El elemento '"+element+"' no existe");
				}else{
					orderMap.put(element,direction);
				}
			}
		}
		Qualification.orderBy(marks,orderMap);
			Integer count = marks.findRowCount();
			
			//Pagination
			String limit = request().getQueryString("limit");
			String page =  request().getQueryString("page");
			
			if(limit == null){
				limit = "10";
			}
			if(page == null){
				page = "1";
			}

			Qualification.page(marks,limit,page);
			List<Qualification> rating = marks.findList();
			
		
		if(rating.size() == 0){
			return badRequest("No se han encontrado resultados en la búsqueda");
		}
		if(request().accepts("application/xml")){
    		return ok(views.xml.qualificationsView.render(rating,count));
    	}else if(request().accepts("application/json")){
    		Map<String,Object> result = new HashMap<String,Object>();
    		result.put("count",count);
    		result.put("rating",rating);
    		return ok(Json.toJson(result));
    	}
		return badRequest("Unsupported format");
	}
}
