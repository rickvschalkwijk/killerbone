package models;

import java.util.*;

import javax.persistence.*;
import org.joda.time.DateTime;

import com.avaje.ebean.validation.Length;

import play.data.format.*;
import play.db.ebean.*;

@Entity
public class User extends Model
{
	private static final long serialVersionUID = 5854422586239724109L;

	@Id
	@GeneratedValue
	public long userId;
	
	@Length(max = 50)
	public String name;
	
	@Length(max = 50)
	public String email;
	
	@Length(max = 255)
	public String password;
	
	@Length(max = 255)
	public String lastKnownLocation;
	
	@Formats.DateTime(pattern = "dd-MM-yyyy")
	public DateTime creationDate;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "initiator")
	public Set<Friendship> initiatedFriendships;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "participant")
	public Set<Friendship> participatedFriendships;
	
	public User()
	{
		// Default values
		initiatedFriendships = new HashSet<Friendship>();
		participatedFriendships = new HashSet<Friendship>();
	}
	
	public User(String name, String email, String password, DateTime creationDate)
	{
		this();

		this.name = name;
		this.email = email;
		this.password = password;
		this.creationDate = creationDate;
	}
	
	//-----------------------------------------------------------------------//
	
	public static Finder<Long, User> find = new Finder<Long, User>(
			Long.class, User.class
	);
}
