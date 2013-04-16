package models;

import java.util.*;

import javax.persistence.*;
import org.joda.time.DateTime;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
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
	@Column(unique = true)
	public String email;

	@Length(max = 255)
	public String hashedPassword;

	public boolean isAdmin;
	public boolean isActivated;

	@Formats.DateTime(pattern = "dd-MM-yyyy HH:mm")
	public DateTime lastActivityDate;	
	
	@Formats.DateTime(pattern = "dd-MM-yyyy HH:mm")
	public DateTime creationDate;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "initiator")
	public List<Friendship> initiatedFriendships;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "participant")
	public List<Friendship> participatedFriendships;

	public User()
	{
		initiatedFriendships = new ArrayList<Friendship>();
		participatedFriendships = new ArrayList<Friendship>();
	}

	public User(String name, String email, String hashedPassword)
	{
		this();

		this.name = name;
		this.email = email;
		this.hashedPassword = hashedPassword;
	}

	// -----------------------------------------------------------------------//

	public static Finder<Long, User> find = new Finder<Long, User>(Long.class, User.class);
	
	
	public static void updateLastActivity(long userId)
	{
		User user = User.find.byId(userId);
		if (user != null)
		{
			user.lastActivityDate = DateTime.now();
			Ebean.save(user);
		}
	}
	
	public static long getNumberOfUsers()
	{
		return User.find.findRowCount();
	}
	
	public static long getNumberOfWeekNewUsers()
	{
		DateTime beginOfWeek = new DateTime().withMillisOfDay(0).minusDays(Math.max(0,DateTime.now().getDayOfWeek() - 1));
		return User.find.where(Expr.ge("creationDate", beginOfWeek)).findRowCount();
	}
	
	public static long getNumberOfWeekActiveUsers()
	{
		DateTime beginOfWeek = new DateTime().withMillisOfDay(0).minusDays(Math.max(0,DateTime.now().getDayOfWeek() - 1));
		return User.find.where(Expr.ge("lastActivityDate", beginOfWeek)).findRowCount();
	}
}
