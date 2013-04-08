package models;

import javax.persistence.*;

import org.joda.time.DateTime;

import play.data.format.Formats;
import play.db.ebean.Model;

@Entity
public class FriendshipLocation extends Model
{
	private static final long serialVersionUID = 5498218896807197842L;

	@Id
	@GeneratedValue
	public long friendshipLocationId;
	
	@Column(nullable = false)
	public double latitude;
	
	@Column(nullable = false)
	public double longitude;
	
	@ManyToOne(fetch = FetchType.LAZY)
	public User user;
	
	@ManyToOne(fetch = FetchType.LAZY)
	public Friendship friendship;
	
	@Formats.DateTime(pattern = "dd-MM-yyyy HH:mm")
	public DateTime refreshDate;
	
	public FriendshipLocation()
	{
		// Default values
	}
	
	public FriendshipLocation(User user, Friendship friendship, double latitude, double longitude, DateTime refreshDate)
	{
		this();
		
		this.user = user;
		this.friendship = friendship;
		this.latitude = latitude;
		this.longitude = longitude;
		this.refreshDate = refreshDate;
	}
	
	//-----------------------------------------------------------------------//
	
	public static Finder<Long, FriendshipLocation> find = new Finder<Long, FriendshipLocation>(
			Long.class, FriendshipLocation.class
	);	
}
