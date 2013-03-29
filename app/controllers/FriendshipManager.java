package controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import helpers.Server;
import helpers.Validator;
import models.Friendship;
import models.User;
import models.Friendship.FriendshipStatus;

import org.joda.time.DateTime;
import org.w3c.dom.Document;

import play.api.Play;
import play.libs.XPath;
import play.mvc.*;
import utils.Authenticator;
import utils.XmlProcessor;
import views.xml.*;

public class FriendshipManager extends Controller
{
	public static Result getUserFriendships(long userId)
	{
		Authenticator authenticator = new Authenticator();

		// Validate user access with authtoken
		String authToken = Server.getHeaderValue("AuthToken");
		if (authenticator.validateAuthToken(userId, authToken))
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
						friendship.save();
					}
				}				
				
				friendships.addAll(user.initiatedFriendships);
				friendships.addAll(user.participatedFriendships);
				
				return ok(friendshipList.render(friendships));
			}
		}

		return ok(message.render("GET_FRIENDSHIPS_FAILED", ""));
	}

	// -----------------------------------------------------------------------//

	public static Result createFriendship()
	{
		XmlProcessor xml = new XmlProcessor();
		Validator validator = new Validator();
		Document xmlDocument = request().body().asXml();

		// Validate xml document
		File xsdFile = Play.current().getFile("/public/xsd/friendshipRequest.xsd");
		boolean isValidXml = xml.validateXmlAgainstXsd(xmlDocument, xsdFile.getAbsolutePath());

		if (!isValidXml)
		{
			return ok(message.render("INVALID_XML_FORMAT", ""));
		}

		// Gather required friendship request information
		String fromId = XPath.selectText("/friendshipRequest/fromId", xmlDocument).trim();
		String toId = XPath.selectText("/friendshipRequest/toId", xmlDocument).trim();

		// Validate friendship request information
		if (!validator.validateNumeric(fromId) || !validator.validateNumeric(toId) || fromId.equals(toId))
		{
			return badRequest(message.render("INVALID_FRIENDSHIP_CREATE_INFO", ""));
		}

		// Get involved users
		User initiator = User.find.byId(Long.valueOf(fromId));
		User participant = User.find.byId(Long.valueOf(toId));

		boolean operationSucceeded = false;
		if (initiator != null && participant != null)
		{
			Friendship newFriendship = new Friendship(initiator, participant);
			newFriendship.requestDate = DateTime.now();
			newFriendship.save();

			operationSucceeded = (newFriendship.friendshipId != 0);
		}

		String messageCode = (operationSucceeded ? "FRIENDSHIP_CREATE_SUCCESS" : "FRIENDSHIP_CREATE_FAILED");
		return ok(message.render(messageCode, ""));
	}

	// -----------------------------------------------------------------------//

	public static Result acceptFriendship(long friendshipId, long userId)
	{
		Authenticator authenticator = new Authenticator();
		
		// Validate user access with authtoken
		String authToken = Server.getHeaderValue("AuthToken");
		if (authenticator.validateAuthToken(userId, authToken))
		{
			Friendship friendship = Friendship.find.byId(friendshipId);
			
			if (friendship != null && friendship.participant.userId == userId && friendship.status == FriendshipStatus.SENT)
			{
				friendship.status = FriendshipStatus.APPROVED;
				friendship.approvalDate = DateTime.now();
				friendship.save();
				
				return ok(message.render("ACCEPT_FRIENDSHIP_SUCCESS", ""));
			}
		}
		
		return ok(message.render("ACCEPT_FRIENDSHIP_FAILED", ""));
	}

	// -----------------------------------------------------------------------//

	public static Result declineFriendship(long friendshipId, long userId)
	{
		Authenticator authenticator = new Authenticator();
		
		// Validate user access with authtoken
		String authToken = Server.getHeaderValue("AuthToken");
		if (authenticator.validateAuthToken(userId, authToken))
		{
			Friendship friendship = Friendship.find.byId(friendshipId);
			
			if (friendship != null && friendship.participant.userId == userId && friendship.status == FriendshipStatus.SENT)
			{
				friendship.status = FriendshipStatus.DECLINED;
				friendship.endDate = DateTime.now();
				friendship.save();
				
				return ok(message.render("DECLINE_FRIENDSHIP_SUCCESS", ""));
			}
		}
		
		return ok(message.render("DECLINE_FRIENDSHIP_FAILED", ""));
	}

	// -----------------------------------------------------------------------//
	
	public static Result endFriendship(long friendshipId, long userId)
	{
		Authenticator authenticator = new Authenticator();
		
		// Validate user access with authtoken
		String authToken = Server.getHeaderValue("AuthToken");
		if (authenticator.validateAuthToken(userId, authToken))
		{
			Friendship friendship = Friendship.find.byId(friendshipId);
			
			if (friendship != null && (friendship.initiator.userId == userId ||friendship.participant.userId == userId) && friendship.status == FriendshipStatus.APPROVED)
			{
				friendship.status = FriendshipStatus.ENDED;
				friendship.endDate = DateTime.now();
				friendship.save();
				
				return ok(message.render("END_FRIENDSHIP_SUCCESS", ""));
			}
			else
			{
				return ok(message.render("FRIENDSHIP NULL", String.valueOf(friendship.friendshipId)));
			}			
		}	
		
		return ok(message.render("END_FRIENDSHIP_FAILED", ""));
	}
}
