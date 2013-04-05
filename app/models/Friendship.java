package models;

import java.util.ArrayList;
import java.util.List;

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
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "friendship")
	public List<FriendshipLocation> memberLocations;
	
	public FriendshipStatus status;
	
	@Formats.DateTime(pattern = "dd-MM-yyyy")
	public DateTime requestDate;
	
	@Formats.DateTime(pattern = "dd-MM-yyyy")
	public DateTime approvalDate;
	
	@Formats.DateTime(pattern = "dd-MM-yyyy")
	public DateTime endDate;
	
	public Friendship()
	{
		// Set default values
		status = FriendshipStatus.PENDING;
		memberLocations = new ArrayList<FriendshipLocation>();
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
	
	// -----------------------------------------------------------------------//
	
	public boolean isMember(long userId)
	{
		return (initiator.userId == userId || participant.userId == userId);
	}
	
	public User getMember(long userId)
	{
		if (initiator.userId == userId)
		{
			return initiator;
		}
		if (participant.userId == userId)
		{
			return participant;
		}
		return null;
	}
	
	public FriendshipLocation getMemberLocation(long userId)
	{
		if (memberLocations.size() > 0)
		{
			for(FriendshipLocation location : memberLocations)
			{
				if (location.user.userId == userId)
				{
					return location;
				}
			}
		}
		return null;
	}
}