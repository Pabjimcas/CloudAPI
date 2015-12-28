package helpers;

import java.util.ArrayList;
import java.util.List;

import models.Client;

public class ClientJson {

	public String name;
	public String lastname;
	public String nif_cif;
	public String phone;
	public String city;
	
	public ClientJson(Client person){
		this.name = person.name;
		this.lastname = person.lastname;
		this.nif_cif = person.nif;
		this.phone = person.phone;
		this.city = person.city;
	}
	public static List<ClientJson> getList(List<Client> clients){
		List<ClientJson> newList = new ArrayList<ClientJson>(clients.size()) ;
		for (Client ct : clients) { 
		  newList.add(new ClientJson(ct)); 
		}
		return newList;
	}
}