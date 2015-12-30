package models;

import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.Valid;

import play.data.validation.Constraints.Pattern;
import play.data.validation.Constraints.Required;
import play.data.validation.Constraints.ValidateWith;
import validators.NifValidator;
import validators.Noum;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
public class Client extends Model{
	
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
	
	public static List<Client> page(ExpressionList<Client> clients,String limit,String page){
		Integer limitNumber = new Integer(limit);
		Integer pageNumber = new Integer(page);
		if(limitNumber < 1){
			limitNumber = 10;
		}
		if(pageNumber < 1){
			pageNumber = 1;
		}
		return clients.setMaxRows(limitNumber).setFirstRow(limitNumber*(pageNumber-1)).findList();
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
	
	public void initialization(String type){
		this.user.setType(type);
	}
	
	public Boolean updateData(Client data){
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
