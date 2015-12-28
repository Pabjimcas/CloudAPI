package models;

import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.Valid;

import com.avaje.ebean.ExpressionList;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
public class Client extends Person{
	
	@JsonIgnoreProperties({"clientExp","inspectors","plots"})
	@Valid
	@OneToMany(cascade=CascadeType.ALL, mappedBy="clientExp")
	public List<Expedient> expedients;
	
	public static final Find<Long,Client> find = new Find<Long,Client>(){};
	
	public static Client findByUser (User user){
		return find.where().eq("user", user).findUnique();
	}
	
	public static Client findByNif(String nif){
		return find.where().eq("nif",nif).findUnique();
	}
	
	public Boolean hasExpedient(Expedient expedient){
		return this.expedients.contains(expedient);
	}
	
	public static ExpressionList<Client> findBy(Map<String,String> filters){
		
		ExpressionList<Client> res = find.where();
		for (String key : filters.keySet()){
			res = res.eq(key, filters.get(key));
		}
		return res;
	}
	
	public static void page(ExpressionList<Client> clients,String limit,String page){
		Integer limitNumber = new Integer(limit);
		Integer pageNumber = new Integer(page);
		if(limitNumber < 1){
			limitNumber = 10;
		}
		if(pageNumber < 1){
			pageNumber = 1;
		}
		clients.setMaxRows(limitNumber).setFirstRow(limitNumber*(pageNumber-1));
	}
	
	public static void orderBy(ExpressionList<Client> clients,Map<String,String> orders){
		
		String orderString = "";
		int cont = 0;
		for (String key : orders.keySet()){
			orderString += key+" "+orders.get(key);
			if(cont != orders.keySet().size() -1){
				orderString += ", ";
			}
			cont++;
		}
		clients.orderBy(orderString).findList();
	}
}
