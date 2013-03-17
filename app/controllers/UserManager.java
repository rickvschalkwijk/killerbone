package controllers;

import models.User;

import org.joda.time.DateTime;
import org.w3c.dom.Document;
import play.libs.XPath;
import play.mvc.*;
import utils.Authenticator;
import utils.XmlProcessor;

public class UserManager extends Controller
{
	public static Result createUser() throws Exception
	{
		XmlProcessor xml = new XmlProcessor();
		Document xmlDocument = request().body().asXml();
		
		// TODO: validate xml
		
		// Gather required user information
		String name = XPath.selectText("/user/name", xmlDocument);
		String email = XPath.selectText("/user/email", xmlDocument);
		String password = XPath.selectText("/user/password", xmlDocument);
		DateTime creationDate = DateTime.now();
		
		// Create new user
		User newUser = new User(name, email, password, creationDate);
		newUser.save();

		// Verify if create operation succeeded
		boolean operationSucceeded = (newUser.userId > 0);
		
		// Respond with xml message
		String messageCode = (operationSucceeded ? "USER_CREATE_SUCCESS" : "USER_CREATE_FAILED");
		String xmlMessage = xml.composeXmlMessage(messageCode, String.valueOf(newUser.userId));
		
		return ok(xmlMessage).as("text/xml");
	}

	public static Result deleteUser(long userId)
	{
		XmlProcessor xml = new XmlProcessor();
		
		// TODO: validate xml
		
		// Find and delete user by id
		User user = User.find.byId(userId);
		if (user != null && user.userId == userId)
		{
			user.delete();
		}
		
		// Verify if create operation succeeded
		boolean operationSucceeded = (User.find.byId(userId) == null);
		
		// Respond with xml message
		String messageCode = (operationSucceeded ? "USER_DELETE_SUCCESS" : "USER_DELETE_FAILED");
		String xmlMessage = xml.composeXmlMessage(messageCode, "");
		
		return ok(xmlMessage).as("text/xml");
	}
	
	public static Result loginUser()
	{
		XmlProcessor xml = new XmlProcessor();
		Authenticator authenticator = new Authenticator();
		Document xmlDocument = request().body().asXml();
		
		// TODO: validate xml

		// Gather required user information
		String email = XPath.selectText("/user/email", xmlDocument);
		String password = XPath.selectText("/user/password", xmlDocument);

		// Validate user credentials
		User user = User.find.all().get(0);	
		
		if (user != null)
		{
			// Login user
			
			return ok("found!");
		}
		else
		{
			// user not found
			
			return ok("not found!");
		}
	}
}
