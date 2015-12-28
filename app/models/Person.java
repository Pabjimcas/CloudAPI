package models;

import javax.persistence.CascadeType;

import validators.Noum;

import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import javax.validation.Valid;

import validators.NifValidator;

import com.fasterxml.jackson.annotation.JsonIgnore;

import play.data.validation.Constraints.Pattern;
import play.data.validation.Constraints.Required;
import play.data.validation.Constraints.ValidateWith;

@MappedSuperclass
public class Person extends BaseModel{
	
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
	
	public void initialization(String type){
		this.user.setType(type);
	}
	
	public Boolean updateData(Person data){
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