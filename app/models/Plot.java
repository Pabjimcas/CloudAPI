package models;

import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;






import com.avaje.ebean.ExpressionList;
import com.fasterxml.jackson.annotation.JsonIgnore;

import play.data.validation.Constraints.Min;
import play.data.validation.Constraints.Required;
import validators.Noum;

@Entity
public class Plot extends BaseModel{

	@JsonIgnore
	@ManyToOne
	public Expedient expedient;
	
	@Required
	@Min(0)
	public Integer enclosure;
	
	@Required
	@Min(0)
	public Float surface;
	
	@Required
	@Noum
	public String product;
	
	@Min(0)
	public Float harvest;
	
	public static final Find<Long,Plot> find = new Find<Long,Plot>(){};
	
	public Boolean updateData(Plot data){
		Boolean res = false;
		
		if(data.enclosure!= this.enclosure){
			this.enclosure = data.enclosure;
			res = true;
		}
		
		if(data.surface != this.surface){
			this.surface = data.surface;
			res = true;
		}
		if(!data.product.equals(this.product)){
			this.product = data.product;
			res = true;
		}
		if(data.harvest != null && !data.harvest.equals(this.harvest)){
			this.harvest = data.harvest;
			res = true;
		}
		return res;
	}
	
public static ExpressionList<Plot> findBy(Long idEx,Map<String,String> filters){
		
		ExpressionList<Plot> res = Plot.find.where().eq("expedient.id",idEx);
		for (String key : filters.keySet()){
			switch(key){
			case "max_harvest":
				res.le("harvest",new Float(filters.get(key)));
				break;
			case "min_harvest":
				res.ge("harvest",new Float(filters.get(key)));
				break;
			case "max_surface":
				res.le("surface",new Float(filters.get(key)));
				break;
			case "min_surface":
				res.ge("surface",new Float(filters.get(key)));
				break;
			default:
				res = res.eq(key, filters.get(key));
			}
		}
		return res;
	}
	public void initialization(Expedient expedient){
		this.expedient = expedient;
	}
	
	public static Plot findByEnclosure(Expedient expedient,Integer enclosure){
		return find.where().eq("expedient", expedient).eq("enclosure", enclosure).findUnique();
	}
	
	public static void page(ExpressionList<Plot> plots,String limit,String page){
		Integer limitNumber = new Integer(limit);
		Integer pageNumber = new Integer(page);
		if(limitNumber < 1){
			limitNumber = 10;
		}
		if(pageNumber < 1){
			pageNumber = 1;
		}
		plots.setMaxRows(limitNumber).setFirstRow(limitNumber*(pageNumber-1));
	}
	
	public static void orderBy(ExpressionList<Plot> plots,Map<String,String> orders){
		
		String orderString = "";
		int cont = 0;
		for (String key : orders.keySet()){
			orderString += key+" "+orders.get(key);
			if(cont != orders.keySet().size() -1){
				orderString += ", ";
			}
			cont++;
		}
		plots.orderBy(orderString).findList();
	}
	
}
