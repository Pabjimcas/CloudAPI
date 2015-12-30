
package models;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.Valid;

import play.data.validation.Constraints.Required;
import validators.CodExp;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
public class Expedient extends Model{
	
	@Id
	public Long id;
	
	@JsonIgnore
	@ManyToOne
	public Client clientExp;
	
	@Required
	@CodExp
	public String code;
	
	@JsonIgnoreProperties({"expedientsEval","nif","name","lastname","city","address","id"})
	@ManyToMany(mappedBy = "expedientsEval")
	public Set<Inspector> inspectors;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="expedient")
	@Valid
	public List<Plot> plots;
	
	@JsonIgnore
	@OneToMany(cascade=CascadeType.ALL, mappedBy="expedient")
	public List<Qualification> rating;
	
	public Float averageMark;
	
	public String validate() {
		List<Integer> list = new ArrayList<Integer>();
		Set<String> repeats = new HashSet<String>();
		for(int i = 0; i< plots.size(); i++){
			Plot plot = plots.get(i);
			if(list.contains(plot.enclosure)){
				repeats.add("La parcela "+plot.enclosure+" ya existe");
			}else{
				list.add(plot.enclosure);
			}
		}
		if(repeats.size()>0){
			return repeats.toString();
		}
		if(rating.size() > 0){
			return "Un cliente no puede calificar";
		}
		return null;
		}
	
	public void MarkCalculate(){
		Float res = new Float(0);
		for(Qualification q: rating){
			res += q.mark; 
		}
		this.averageMark = res/rating.size();
		this.update();
	}
	
	
	public void addInspector(Inspector inspector){
		this.inspectors.add(inspector);
	}
	
	public void initialization(Client client){
		this.clientExp = client;
	}
	
	public Boolean hasPlot(Plot plot){
		return this.plots.contains(plot);
	}
	
	public Boolean hasMark(Qualification mark){
		return this.rating.contains(mark);
	}
	
	public static final Find<Long,Expedient> find = new Find<Long,Expedient>(){};
	
	public static Expedient findByCode(String code){
		return find.where().eq("code", code).findUnique();
	}
		
	public static ExpressionList<Expedient> findByClient(Long idCt){
		ExpressionList<Expedient> res = Expedient.find.where().eq("clientExp.id",idCt);
		return res;
	}
	
	public static ExpressionList<Expedient> findByInspector(Inspector inspector){
		ExpressionList<Expedient> res = Expedient.find.where().in("inspectors",inspector);
		return res;
	}
	
	public Boolean updateData(Expedient data){
		Boolean res = false;
		
		if(data.code != null && !data.code.equals(this.code)){
			this.code = data.code;
			res = true;
		}
		if(data.plots != null){
			this.plots = data.plots;
			res = true;
		}
		return res;
	}
	
	public static void page(ExpressionList<Expedient> expedients,String limit,String page){
		Integer limitNumber = new Integer(limit);
		Integer pageNumber = new Integer(page);
		if(limitNumber < 1){
			limitNumber = 10;
		}
		if(pageNumber < 1){
			pageNumber = 1;
		}
		expedients.setMaxRows(limitNumber).setFirstRow(limitNumber*(pageNumber-1));
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
