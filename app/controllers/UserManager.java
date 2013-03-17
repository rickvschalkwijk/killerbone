package controllers;

import helpers.Common;
import helpers.Server;
import models.User;
import org.joda.time.DateTime;
import org.w3c.dom.Document;
import com.avaje.ebean.Ebean;
import play.libs.XPath;
import play.mvc.*;
import utils.Authenticator;
import helpers.Validator;
import utils.XmlProcessor;

public class UserManager extends Controller
{
	public static Result createUser() throws Exception
	{
		XmlProcessor xml = new XmlProcessor();
		Validator validator = new Validator();
		Document xmlDocument = request().body().asXml();
		
		// Validate xml document
		String pathToXsd = Common.resolvePath("~\\app\\assets\\xsd\\createUser.xsd");
		boolean isValidXml = xml.validateXmlAgainstXsd(xmlDocument, pathToXsd);
		
		if (!isValidXml)
		{
			String xmlResponse = xml.composeXmlMessage("INVALID_XML_FORMAT", "");
			return badRequest(xmlResponse).as("text/xml");
		}
		
		// Gather required user information
		String name = XPath.selectText("/user/name", xmlDocument).trim();
		String email = XPath.selectText("/user/email", xmlDocument).trim();
		String password = XPath.selectText("/user/password", xmlDocument).trim();
		
		// Validate user information
		if (!validator.validateName(name) || !validator.validateEmail(email) || !validator.validatePassword(password))
		{
			String xmlResponse = xml.composeXmlMessage("INVALID_USER_INFO", "");
			return badRequest(xmlResponse).as("text/xml");
		}
		
		// Create new user
		User newUser = new User(name, email, password, DateTime.now());
		newUser.save();

		// Verify if create operation succeeded
		boolean operationSucceeded = (newUser.userId > 0);
		
		// Respond with xml message
		String messageCode = (operationSucceeded ? "USER_CREATE_SUCCESS" : "USER_CREATE_FAILED");
		String xmlMessage = xml.composeXmlMessage(messageCode, String.valueOf(newUser.userId));
		
		return ok(xmlMessage).as("text/xml");
	}

	//-----------------------------------------------------------------------//
	
	public static Result deleteUser(long userId)
	{
		XmlProcessor xml = new XmlProcessor();
		Authenticator authenticator = new Authenticator();
		
		boolean operationSucceeded = false;
		
		// Users may only delete their own account, having a valid authtoken
		String authToken = Server.getHeaderValue("AuthToken");
		if (authenticator.validateAuthToken(userId, authToken))
		{
			// Find and delete user by id
			User user = User.find.byId(userId);
			if (user != null && user.userId == userId)
			{
				user.delete();
			}
			
			operationSucceeded = (User.find.byId(userId) == null);
		}	
		
		// Respond with xml message
		String messageCode = (operationSucceeded ? "USER_DELETE_SUCCESS" : "USER_DELETE_FAILED");
		String xmlMessage = xml.composeXmlMessage(messageCode, "");
		
		return ok(xmlMessage).as("text/xml");
	}
	
	//-----------------------------------------------------------------------//
	
	public static Result loginUser()
	{
		XmlProcessor xml = new XmlProcessor();
		Authenticator authenticator = new Authenticator();
		Document xmlDocument = request().body().asXml();
		
		// Validate xml document
		String pathToXsd = Common.resolvePath("~\\app\\assets\\xsd\\loginUser.xsd");
		boolean isValidXml = xml.validateXmlAgainstXsd(xmlDocument, pathToXsd);
		
		if (!isValidXml)
		{
			String xmlResponse = xml.composeXmlMessage("INVALID_XML_FORMAT", "");
			return badRequest(xmlResponse).as("text/xml");
		}

		// Gather required user information
		String email = XPath.selectText("/user/email", xmlDocument).trim();
		String password = XPath.selectText("/user/password", xmlDocument).trim();

		// Validate user credentials
		User user = Ebean.find(User.class).where().eq("email", email).eq("password", password).findUnique();

		if (user != null)
		{
			String authToken = authenticator.generateAuthToken(user.userId);
			String xmlResponse = xml.composeXmlMessage("USER_LOGIN_SUCCESS", authToken);
			return ok(xmlResponse).as("text/xml");
		}
		else
		{
			String xmlResponse = xml.composeXmlMessage("USER_LOGIN_FAILED", "");
			return ok(xmlResponse).as("text/xml");
		}
	}
}
