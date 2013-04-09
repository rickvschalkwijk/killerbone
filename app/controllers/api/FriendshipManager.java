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

import core.ApiController;

import play.Logger;
import play.libs.XPath;
import play.mvc.*;
import views.xml.api.*;

public class FriendshipManager extends ApiController
{
	public static Result getUserFriendships(long userId)
	{
		if (!isAuthorized(userId)) { return unAuthorized(); }
		
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
		return operationFailed();
	}

	//-----------------------------------------------------------------------//

	public static Result createFriendship()
	{
		// Parse required information
		boolean operationSucceeded = false;
		Document xmlDocument = request().body().asXml();
		
		try
		{
			String initiatorId = XPath.selectText("/friendshipRequest/initiatorId", xmlDocument).trim();
			String participantEmail = XPath.selectText("/friendshipRequest/participantEmail", xmlDocument).trim();

			// Validate required information
			if (!Validator.validateNumeric(initiatorId) || !Validator.validateEmail(participantEmail))
			{
				return operationFailed();
			}
			if (!isAuthorized(Long.valueOf(initiatorId))) 
			{ 
				return unAuthorized(); 
			}
			
			// Get involved users
			User initiator = User.find.byId(Long.valueOf(initiatorId));
			User participant = User.find.where().eq("email", participantEmail).findUnique();

			// Create new friendship
			if (initiator != null && participant != null && initiator.userId != participant.userId)
			{			
				Friendship newFriendship = new Friendship(initiator, participant);
				newFriendship.requestDate = DateTime.now();
				
				Ebean.save(newFriendship);
				operationSucceeded = (newFriendship.friendshipId != 0);
			}			
		}
		catch (RuntimeException e)
		{
			Logger.error("An error occured while creating friendship: " + e.getMessage());
		}
		return (operationSucceeded ? operationSuccess() : operationFailed());
	}

	//-----------------------------------------------------------------------//

	public static Result acceptFriendship(long friendshipId, long userId)
	{
		if (!isAuthorized(userId)) { return unAuthorized(); }
		
		Friendship friendship = Friendship.find.byId(friendshipId);
		if (friendship != null && friendship.participant.userId == userId && friendship.status == FriendshipStatus.SENT)
		{
			friendship.status = FriendshipStatus.APPROVED;
			friendship.approvalDate = DateTime.now();
			Ebean.save(friendship);
			
			return operationSuccess();
		}
		return operationFailed();
	}

	//-----------------------------------------------------------------------//

	public static Result declineFriendship(long friendshipId, long userId)
	{
		if (!isAuthorized(userId)) { return unAuthorized(); }
		
		Friendship friendship = Friendship.find.byId(friendshipId);
		if (friendship != null && friendship.participant.userId == userId && friendship.status == FriendshipStatus.SENT)
		{
			Ebean.delete(friendship);
			
			return operationSuccess();
		}
		return operationFailed();
	}

	//-----------------------------------------------------------------------//
	
	public static Result endFriendship(long friendshipId, long userId)
	{
		if (!isAuthorized(userId)) { return unAuthorized(); }
		
		Friendship friendship = Friendship.find.byId(friendshipId);
		if (friendship != null && (friendship.initiator.userId == userId ||friendship.participant.userId == userId))
		{
			Ebean.delete(friendship);
			
			return operationSuccess();
		}	
		return operationFailed();
	}
	
	//-----------------------------------------------------------------------//
	
	public static Result shareLocationWithFriendship(long friendshipId, long userId)
	{
		if (!isAuthorized(userId)) { return unAuthorized(); }
		
		Friendship friendship = Friendship.find.byId(friendshipId);
		if (friendship != null && friendship.isMember(userId))
		{
			// Parse required information
			boolean operationSucceeded = false;
			Document requestBodyXml = request().body().asXml();
			
			try
			{
				String latitude = XPath.selectText("/location/latitude", requestBodyXml).trim();
				String longitude = XPath.selectText("/location/longitude", requestBodyXml).trim();			
				
				FriendshipLocation location = friendship.getMemberLocation(userId);
				if (location == null)
				{
					location = new FriendshipLocation(friendship.getMember(userId), friendship, 0, 0, DateTime.now());
				}
				
				// Set location
				location.latitude = Double.parseDouble(latitude);
				location.longitude = Double.parseDouble(longitude);
				
				Ebean.save(location);
				operationSucceeded = true;
			}
			catch(RuntimeException e)
			{
				Logger.error("An error occured while updating location: " + e.getMessage());
			}
			return (operationSucceeded ? operationSuccess() : operationFailed());
		}
		return operationFailed();
	}
}
