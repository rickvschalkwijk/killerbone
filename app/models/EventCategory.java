package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.avaje.ebean.validation.Length;

import play.db.ebean.Model;

@Entity
public class EventCategory extends Model
{
	private static final long serialVersionUID = -6205191221810280698L;

	@Id
	@GeneratedValue
	public long eventCategoryId;
	
	@Length(max = 255)
	public String title;
	
	@Length(max = 255)
	public String systemName;

	@OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
	public List<Event> events;

	public EventCategory()
	{
		// Default values
		events = new ArrayList<Event>();
	}

	public EventCategory(String title, String systemName)
	{
		this();		
		
		this.title = title;
		this.systemName = systemName;
	}
	
	//-----------------------------------------------------------------------//

	public static Finder<Long, EventCategory> find = new Finder<Long, EventCategory>(
			Long.class, EventCategory.class
	);
}
