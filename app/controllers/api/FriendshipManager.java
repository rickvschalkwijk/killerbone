package controllers.api;

import java.util.ArrayList;
import java.util.List;

import helpers.Validator;
import models.Friendship;
import models.FriendshipLocation;
import models.User;
import models.Friendship.FriendshipStatus;

import org.joda.time.DateTime;
import org.w3c.dom.Document;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;

import core.ApiController;

import play.libs.XPath;
import play.mvc.*;
import views.xml.api.*;
import views.xml.api.messages.*;

public class FriendshipManager extends ApiController
{
	public static Result getUserFriendships(long userId)
	{
		if (!isAuthorized(userId)) { return badRequest(unauthorized.render()); }
		
		User user = User.find.byId(userId);
		if (user != null)
		{
			List<Friendship> friendships = new ArrayList<Friendship>();

			// Change pending friendships to sent
			for (Friendship friendship : user.participatedFriendships) 
			{
				if (friendship.status == FriendshipStatus.PENDING)
				{
					friendship.status = FriendshipStatus.SENT;						
				}
			}
			Ebean.save(user.participatedFriendships);

			friendships.addAll(user.initiatedFriendships);
			friendships.addAll(user.participatedFriendships);
			
			return ok(friendshipList.render(friendships).body().trim()).as("text/xml");
		}
		return ok(operationFailed.render());
	}

	//-----------------------------------------------------------------------//

	public static Result createFriendship()
	{
		Document xmlDocument = request().body().asXml();

		// Gather required friendship request information
		String initiatorId = XPath.selectText("/friendshipRequest/initiatorId", xmlDocument).trim();
		String participantEmail = XPath.selectText("/friendshipRequest/participantEmail", xmlDocument).trim();

		// Validate friendship request information
		if (!Validator.validateNumeric(initiatorId) || !Validator.validateEmail(participantEmail))
		{
			return badRequest(operationFailed.render());
		}

		// Validate initiator
		if (!isAuthorized(Long.valueOf(initiatorId))) { return badRequest(unauthorized.render()); }
		
		// Get involved users
		User initiator = User.find.byId(Long.valueOf(initiatorId));
		User participant = User.find.where().eq("email", participantEmail).findUnique();

		// Create friendship
		boolean operationSucceeded = false;
		if (initiator != null && participant != null && initiator.userId != participant.userId)
		{
			// Delete any ended friendships
			Friendship endedFriendship = Friendship.find.where(
					Expr.and(Expr.eq("status", "E"),
					Expr.or(
					    Expr.and(Expr.eq("initiatorId", initiator.userId), Expr.eq("participantId", participant.userId)),
						Expr.and(Expr.eq("initiatorId", participant.userId), Expr.eq("participantId", initiator.userId)))
					)).findUnique();
			if (endedFriendship != null)
			{
				endedFriendship.delete();
			}
			
			// Create new friendship
			Friendship newFriendship = new Friendship(initiator, participant);
			newFriendship.requestDate = DateTime.now();
			newFriendship.save();

			operationSucceeded = (newFriendship.friendshipId != 0);
		}
		return ok(operationSucceeded ? operationSuccess.render() : operationFailed.render());
	}

	//-----------------------------------------------------------------------//

	public static Result acceptFriendship(long friendshipId, long userId)
	{
		if (!isAuthorized(userId)) { return badRequest(unauthorized.render()); }
		
		Friendship friendship = Friendship.find.byId(friendshipId);
		if (friendship != null && friendship.participant.userId == userId && friendship.status == FriendshipStatus.SENT)
		{
			friendship.status = FriendshipStatus.APPROVED;
			friendship.approvalDate = DateTime.now();
			friendship.save();
			
			return ok(operationSuccess.render());
		}
		return ok(operationFailed.render());
	}

	//-----------------------------------------------------------------------//

	public static Result declineFriendship(long friendshipId, long userId)
	{
		if (!isAuthorized(userId)) { return badRequest(unauthorized.render()); }
		
		Friendship friendship = Friendship.find.byId(friendshipId);
		if (friendship != null && friendship.participant.userId == userId && friendship.status == FriendshipStatus.SENT)
		{
			friendship.delete();
			return ok(operationSuccess.render());
		}
		return ok(operationFailed.render());
	}

	//-----------------------------------------------------------------------//
	
	public static Result endFriendship(long friendshipId, long userId)
	{
		if (!isAuthorized(userId)) { return badRequest(unauthorized.render()); }
		
		Friendship friendship = Friendship.find.byId(friendshipId);
		if (friendship != null && (friendship.initiator.userId == userId ||friendship.participant.userId == userId))
		{
			friendship.delete();
			return ok(operationSuccess.render());
		}	
		return ok(operationFailed.render());
	}
	
	//-----------------------------------------------------------------------//
	
	public static Result shareLocationWithFriendship(long friendshipId, long userId)
	{
		if (!isAuthorized(userId)) { return badRequest(unauthorized.render()); }
		
		Friendship friendship = Friendship.find.byId(friendshipId);
		if (friendship != null && friendship.isMember(userId))
		{
			Document requestBodyXml = request().body().asXml();
			
			// Parse required information
			String latitude = XPath.selectText("/location/latitude", requestBodyXml).trim();
			String longitude = XPath.selectText("/location/longitude", requestBodyXml).trim();			
			
			// Get or create a friendship location
			FriendshipLocation location = friendship.getMemberLocation(userId);
			if (location == null)
			{
				location = new FriendshipLocation(friendship.getMember(userId), friendship, 0, 0, DateTime.now());
			}
			
			// Set location
			location.latitude = Double.parseDouble(latitude);
			location.longitude = Double.parseDouble(longitude);
			
			location.save();
		}
		return ok(message.render("FRIENDSHIP_LOCATION_UPDATED", "").body().trim()).as("text/xml");
	}
}
