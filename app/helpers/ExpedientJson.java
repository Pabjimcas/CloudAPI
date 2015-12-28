package helpers;

import java.util.ArrayList;
import java.util.List;

import models.Expedient;

public class ExpedientJson {

	public String code;

	public ExpedientJson(Expedient expedient){
		this.code = expedient.code;
	}
	
	public static List<ExpedientJson> getList(List<Expedient> expedients){
		List<ExpedientJson> newList = new ArrayList<ExpedientJson>(expedients.size()) ;
		for (Expedient ex : expedients) { 
		  newList.add(new ExpedientJson(ex)); 
		}
		return newList;
	}
}
