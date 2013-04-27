package models;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import play.db.ebean.Model;

@Entity
public class LocationReview extends Model 
{
	private static final long serialVersionUID = -1808465914790122808L;
	
	@Id
	@GeneratedValue
	public long locationReviewId;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	public Location location;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	public User user;
	
	public int rating;
	
	public LocationReview()
	{
		
	}
	
	//-----------------------------------------------------------------------//
	
	public static Finder<Long, LocationReview> find = new Finder<Long, LocationReview>(
			Long.class, LocationReview.class
	);		
}
