package models;

import java.sql.Timestamp;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.CreatedTimestamp;
import com.avaje.ebean.annotation.UpdatedTimestamp;

@MappedSuperclass
public class BaseModel extends Model{

	@Id
	public Long id;
	
	@Version
	Long version;
	
	@CreatedTimestamp
	Timestamp whenCreated;
	
	@UpdatedTimestamp
	Timestamp whenUpdated;
}
