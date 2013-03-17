package models;

import javax.persistence.*;
import org.joda.time.DateTime;

import com.avaje.ebean.validation.Length;

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
	public DateTime creationDate;

	public User()
	{
		// Default constructor
	}
	
	public User(String name, String email, String password, DateTime creationDate)
	{
		this.name = name;
		this.email = email;
		this.password = password;
		this.creationDate = creationDate;
	}
	
	public User(long userId, String name, String email, String password, DateTime creationDate)
	{
		this.userId = userId;
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
