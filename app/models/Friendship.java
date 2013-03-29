package models;

import javax.persistence.*;
import org.joda.time.DateTime;
import com.avaje.ebean.annotation.EnumMapping;
import play.data.format.Formats;
import play.db.ebean.*;

@Entity
public class Friendship extends Model
{
	private static final long serialVersionUID = -2596701516702084821L;

	@Id
	@GeneratedValue
	public long friendshipId;

	@ManyToOne(fetch = FetchType.EAGER)
	public User initiator;
	
	@ManyToOne(fetch = FetchType.EAGER)
	public User participant;
	
	public FriendshipStatus status;
	
	@Formats.DateTime(pattern = "dd-MM-yyyy")
	public DateTime requestDate;
	
	@Formats.DateTime(pattern = "dd-MM-yyyy")
	public DateTime approvalDate;
	
	@Formats.DateTime(pattern = "dd-MM-yyyy")
	public DateTime endDate;
	
	public Friendship()
	{
		// Default values
		status = FriendshipStatus.PENDING;
	}
	
	public Friendship(User initiator, User participant)
	{
		this();		
		
		this.initiator = initiator;
		this.participant = participant;
	}
	
	// -----------------------------------------------------------------------//

	@EnumMapping(nameValuePairs = "PENDING=P, SENT=S, APPROVED=A, DECLINED=D, ENDED=E")
	public enum FriendshipStatus
	{
		PENDING, SENT, APPROVED, DECLINED, ENDED
	}
	
	public static Finder<Long, Friendship> find = new Finder<Long, Friendship>(
			Long.class, Friendship.class
	);	
}