package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import com.avaje.ebean.validation.*;

import play.db.ebean.Model;

@Entity
public class LocationCategory extends Model 
{
	private static final long serialVersionUID = -1367968336647686110L;
	
	@Id
	@GeneratedValue
	public long locationCategoryId;
	
	@Length(max = 255)
	public String title;
	
	@Length(max = 255)
	public String systemName;
	
	@OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
	public List<Location> locations;
	
	public LocationCategory()
	{
		locations = new ArrayList<Location>();
	}
	
	public LocationCategory(String title, String systemName)
	{
		this();
		
		this.title = title;
		this.systemName = systemName;
	}
	
	//-----------------------------------------------------------------------//

	public static Finder<Long, LocationCategory> find = new Finder<Long, LocationCategory>(
			Long.class, LocationCategory.class
	);
}
