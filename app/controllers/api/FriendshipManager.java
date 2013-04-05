package controllers.api;

import java.util.ArrayList;
import java.util.List;

import helpers.Server;
import helpers.Validator;
import models.Friendship;
import models.FriendshipLocation;
import models.User;
import models.Friendship.FriendshipStatus;

import org.joda.time.DateTime;
import org.w3c.dom.Document;

import com.avaje.ebean.Ebean;

import core.ApiController;

import play.libs.XPath;
import play.mvc.*;
import utils.Authenticator;
import views.xml.api.*;
import views.xml.messages.*;

public class FriendshipManager extends ApiController
{
	public static Result getUserFriendships(long userId)
	{
		Authenticator authenticator = new Authenticator();

		// Validate user access with authtoken
		String authToken = Server.getHeaderValue("AuthToken");
		if (authenticator.validateAuthToken(userId, false, authToken))
		{
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
		}

		return ok(message.render("FRIENDSHIPS_GET_FAILED", "").body().trim()).as("text/xml");
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
			return badRequest(message.render("FRIENDSHIP_CREATE_FAILED", "").body().trim()).as("text/xml");
		}

		// Get involved users
		User initiator = User.find.byId(Long.valueOf(initiatorId));
		User participant = User.find.where().eq("email", participantEmail).findUnique();

		boolean operationSucceeded = false;
		if (initiator != null && participant != null && initiator.userId != participant.userId)
		{
			Friendship newFriendship = new Friendship(initiator, participant);
			newFriendship.requestDate = DateTime.now();
			newFriendship.save();

			operationSucceeded = (newFriendship.friendshipId != 0);
		}

		String messageCode = (operationSucceeded ? "FRIENDSHIP_CREATE_SUCCESS" : "FRIENDSHIP_CREATE_FAILED");
		return ok(message.render(messageCode, "").body().trim()).as("text/xml");
	}

	//-----------------------------------------------------------------------//

	public static Result acceptFriendship(long friendshipId, long userId)
	{
		Authenticator authenticator = new Authenticator();
		
		// Validate user access with authtoken
		String authToken = Server.getHeaderValue("AuthToken");
		if (authenticator.validateAuthToken(userId, false, authToken))
		{
			Friendship friendship = Friendship.find.byId(friendshipId);
			
			if (friendship != null && friendship.participant.userId == userId && friendship.status == FriendshipStatus.SENT)
			{
				friendship.status = FriendshipStatus.APPROVED;
				friendship.approvalDate = DateTime.now();
				friendship.save();
				
				return ok(message.render("FRIENDSHIP_ACCEPT_SUCCESS", "").body().trim()).as("text/xml");
			}
		}
		
		return ok(message.render("FRIENDSHIP_ACCEPT_FAILED", "").body().trim()).as("text/xml");
	}

	//-----------------------------------------------------------------------//

	public static Result declineFriendship(long friendshipId, long userId)
	{
		Authenticator authenticator = new Authenticator();
		
		// Validate user access with authtoken
		String authToken = Server.getHeaderValue("AuthToken");
		if (authenticator.validateAuthToken(userId, false, authToken))
		{
			Friendship friendship = Friendship.find.byId(friendshipId);
			
			if (friendship != null && friendship.participant.userId == userId && friendship.status == FriendshipStatus.SENT)
			{
				friendship.status = FriendshipStatus.DECLINED;
				friendship.endDate = DateTime.now();
				friendship.save();
				
				return ok(message.render("FRIENDSHIP_DECLINE_SUCCESS", "").body().trim()).as("text/xml");
			}
		}
		
		return ok(message.render("FRIENDSHIP_DECLINE_FAILED", "").body().trim()).as("text/xml");
	}

	//-----------------------------------------------------------------------//
	
	public static Result endFriendship(long friendshipId, long userId)
	{
		Authenticator authenticator = new Authenticator();
		
		// Validate user access with authtoken
		String authToken = Server.getHeaderValue("AuthToken");
		if (authenticator.validateAuthToken(userId, false, authToken))
		{
			Friendship friendship = Friendship.find.byId(friendshipId);
			
			if (friendship != null && (friendship.initiator.userId == userId ||friendship.participant.userId == userId))
			{
				friendship.status = FriendshipStatus.ENDED;
				friendship.endDate = DateTime.now();
				friendship.save();
				
				return ok(message.render("FRIENDSHIP_END_SUCCESS", "").body().trim()).as("text/xml");
			}	
		}	
		return ok(message.render("FRIENDSHIP_END_FAILED", "").body().trim()).as("text/xml");
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
				location = new FriendshipLocation(friendship.getMember(userId), friendship, 0, 0);
			}
			
			// Set location
			location.latitude = Double.parseDouble(latitude);
			location.longitude = Double.parseDouble(longitude);
			
			location.refreshDate = DateTime.now();
			location.save();
		}
		return ok(message.render("FRIENDSHIP_LOCATION_UPDATED", "").body().trim()).as("text/xml");
	}
}
