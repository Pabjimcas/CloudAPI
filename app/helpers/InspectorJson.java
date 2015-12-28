package helpers;

import java.util.ArrayList;
import java.util.List;

import models.Inspector;

public class InspectorJson {
	
	public String name;
	public String lastname;
	public String nif_cif;
	public String phone;
	public String city;
	
	public InspectorJson(Inspector inspector){
		this.name = inspector.name;
		this.lastname = inspector.lastname;
		this.nif_cif = inspector.nif;
		this.phone = inspector.phone;
		this.city = inspector.city;
	}
	public static List<InspectorJson> getList(List<Inspector> inspectors){
		List<InspectorJson> newList = new ArrayList<InspectorJson>(inspectors.size()) ;
		for (Inspector ip : inspectors) { 
		  newList.add(new InspectorJson(ip)); 
		}
		return newList;
	}
}