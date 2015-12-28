package models;

import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.validation.Valid;

import com.avaje.ebean.ExpressionList;

import play.data.validation.Constraints.ValidateWith;
import validators.InspectorCodeValidator;


@Entity
public class Inspector extends Person{
	
	@ValidateWith(InspectorCodeValidator.class)
	public String inspectorCode;
	
	@ManyToMany
	@Valid
	public List<Expedient> expedientsEval;
	
	public static final Find<Long,Inspector> find = new Find<Long,Inspector>(){};
	
	public String validate() {
		if(expedientsEval.size() > 0){
			return "Operación no permitida.";
		}
		return null;
		}

	public static Inspector findByUser (User user){
		return find.where().eq("user", user).findUnique();
	}
	public static Inspector findByCode (String code){
		return find.where().eq("inspectorCode", code).findUnique();
	}
	
	public void addExpedient(Expedient expedient){
		this.expedientsEval.add(expedient);
	}
	public static ExpressionList<Inspector> findBy(Map<String,String> filters){
		
		ExpressionList<Inspector> res = find.where();
		for (String key : filters.keySet()){
			res = res.eq(key, filters.get(key));
		}
		return res;
	}
	
	public static void page(ExpressionList<Inspector> inspectors,String limit,String page){
		Integer limitNumber = new Integer(limit);
		Integer pageNumber = new Integer(page);
		if(limitNumber < 1){
			limitNumber = 10;
		}
		if(pageNumber < 1){
			pageNumber = 1;
		}
		inspectors.setMaxRows(limitNumber).setFirstRow(limitNumber*(pageNumber-1));
	}
	
	public static void orderBy(ExpressionList<Inspector> inspectors,Map<String,String> orders){
		
		String orderString = "";
		int cont = 0;
		for (String key : orders.keySet()){
			orderString += key+" "+orders.get(key);
			if(cont != orders.keySet().size() -1){
				orderString += ", ";
			}
			cont++;
		}
		inspectors.orderBy(orderString).findList();
	}
}
