package models;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.data.validation.Constraints.MinLength;
import play.data.validation.Constraints.Required;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class User extends Model{
	
	@Id
	public Long id;
	
	@Required
	@MinLength(4)
	public String username;
	
	@JsonIgnore
	public String password;
	
	public String authtoken;
	
	/*@JsonIgnore
	public Date tokenDate;*/
	
	@JsonIgnore
	public String type;
	
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public static final Find<Long,User> find = new Find<Long,User>(){};
	
	public String tokenCalculate(){
		SecureRandom random = new SecureRandom();
		String token = new BigInteger(130, random).toString(32);
		return token;
	}
	
	public void tokenGenerate(){
		
		String token = tokenCalculate();
		
		while(find.where().eq("authtoken", token).findRowCount() > 0){
			token = tokenCalculate();
		}
		this.authtoken = token;
		/*this.tokenDate = new Date();*/
	}
	
	public static User findByUsername(String username){
		return find.where().eq("username", username).findUnique();
	}
	
	public void logout(){
		this.authtoken = null;
	}
	
	public static Boolean authenticate(User user){
		User userAuth = find.where().eq("username", user.username).eq("password", user.password).findUnique();
		if(userAuth != null){
			return true;
		}
		return false;
	}
}
