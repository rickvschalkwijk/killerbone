package models;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import play.db.ebean.*;

@Entity
public class Location extends Model 
{
	private static final long serialVersionUID = 4748165143428806387L;

	@Id
	@GeneratedValue
	public long locationId;
	
	@Column(unique = true, nullable = false)
	public String title;
	
	@Column(columnDefinition = "TEXT")
	public String description;

	@Column(nullable = true)
	public String imageUrl;
	
	@Column(nullable = false)
	public double latitude;
	
	@Column(nullable = false)
	public double longitude;
	
	@Column(nullable = false)
	public long creationTimestamp;
	
	@Column(nullable = true)
	public long modificationTimestamp;
	
	@Column(nullable = false)
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
	public LocationCategory category;
	
	public Location()
	{
		
	}
	
	public Location(String title, String descrition, double latitude, double longitude, LocationCategory category)
	{
		this.title = title;
		this.description = descrition;
		this.latitude = latitude;
		this.longitude = longitude;
		this.category = category;
	}
	
	//-----------------------------------------------------------------------//
	
	public static Finder<Long, Location> find = new Finder<Long, Location>(
			Long.class, Location.class
	);	
	
}
