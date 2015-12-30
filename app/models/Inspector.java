package models;

import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.validation.Valid;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;

import play.data.validation.Constraints.Pattern;
import play.data.validation.Constraints.Required;
import play.data.validation.Constraints.ValidateWith;
import validators.InspectorCodeValidator;
import validators.NifValidator;
import validators.Noum;


@Entity
public class Inspector extends Model{
	
	@Id
	public Long id;
	
	@JsonIgnore
	@Valid
	@OneToOne(cascade=CascadeType.ALL)
	public User user;
	
	@Required(message="El nif es obligatorio")
	@ValidateWith(NifValidator.class)
	public String nif;
	
	@Required
	@Noum
	public String name;
	
	@Noum
	public String lastname;
	
	@Required
	@Pattern(value = "[679]\\d{8}",message="Teléfono incorrecto")
	public String phone;
	
	@Required
	@Noum
	public String city;
	
	public String address;
	
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
	
	public void initialization(String type){
		this.user.setType(type);
	}
	
	public Boolean updateData(Inspector data){
		Boolean res = false;
		
		if(!data.name.equals(this.name)){
			this.name = data.name;
			res = true;
		}
		if(data.lastname != null && !data.lastname.equals(this.lastname)){
			this.lastname = data.lastname;
			res = true;
		}
		if(!data.nif.equals(this.nif)){
			this.nif = data.nif;
			res = true;
		}
		
		if(!data.phone.equals(this.phone)){
			this.phone = data.phone;
			res = true;
		}
		if(!data.city.equals(this.city)){
			this.city = data.city;
			res = true;
		}

		if(data.address != null && !address.equals(this.address)){
			this.address = data.address;
			res = true;
		}
		return res;
	}
}
