package models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import play.data.format.Formats;
import play.data.validation.Constraints.Min;
import play.data.validation.Constraints.Required;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Qualification extends Model{
	
	@Id
	public Long id;
	
	@JsonIgnore
	@ManyToOne
	public Expedient expedient;
	
	@Required
	@Min(0)
	public Float mark;
	
	/*@Formats.DateTime(pattern="yyyy-MM-dd")
	public Date markDate;*/
	
	public String inspector;
	
	public static final Find<Long,Qualification> find = new Find<Long,Qualification>(){};
	
	public void initialization(Expedient expedient,Inspector inspector){
		expedient.addInspector(inspector);
		expedient.update();
		//this.markDate = new Date();
		this.inspector = inspector.inspectorCode;
		this.expedient = expedient;
		
	}
	
	public Boolean updateData(Qualification data){
		Boolean res = false;
		if(!data.mark.equals(this.mark)){
			this.mark = data.mark;
			//this.markDate = new Date();
			res = true;
		}
		return res;
	}
	
public static ExpressionList<Qualification> findBy(Long idEx,Map<String,String> filters){
		
		ExpressionList<Qualification> res = Qualification.find.where().eq("expedient.id",idEx);
		/*SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");

		for (String key : filters.keySet()){
			Date date = new Date();
			switch(key){
			case "from_date":
				try {
					date = f.parse(filters.get(key));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				res.ge("markDate",date);
				break;
			case "until_date":
				try {
					date = f.parse(filters.get(key));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				res.le("markDate",date);
				break;
			default:
				res = res.eq(key, filters.get(key));
			}
		}*/
		return res;
	}

public static void page(ExpressionList<Qualification> marks,String limit,String page){
	Integer limitNumber = new Integer(limit);
	Integer pageNumber = new Integer(page);
	if(limitNumber < 1){
		limitNumber = 10;
	}
	if(pageNumber < 1){
		pageNumber = 1;
	}
	marks.setMaxRows(limitNumber).setFirstRow(limitNumber*(pageNumber-1));
}

public static void orderBy(ExpressionList<Qualification> marks,Map<String,String> orders){
	
	String orderString = "";
	int cont = 0;
	for (String key : orders.keySet()){
		orderString += key+" "+orders.get(key);
		if(cont != orders.keySet().size() -1){
			orderString += ", ";
		}
		cont++;
	}
	marks.orderBy(orderString).findList();
}
}
