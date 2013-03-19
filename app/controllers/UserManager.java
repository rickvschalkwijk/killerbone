package controllers;

import java.util.HashMap;
import java.util.Map;
import helpers.Common;
import helpers.Server;
import models.User;
import org.joda.time.DateTime;
import org.w3c.dom.Document;
import play.libs.XPath;
import play.mvc.*;
import utils.Authenticator;
import helpers.Validator;
import utils.XmlProcessor;

public class UserManager extends Controller
{
	public static Result getUser(long userId)
	{
		XmlProcessor xml = new XmlProcessor();
		Authenticator authenticator = new Authenticator();
		
		// Users may only delete their own account, having a valid authtoken
		String authToken = Server.getHeaderValue("AuthToken");
		if (authenticator.validateAuthToken(userId, authToken))
		{
			// Find user by id
			User user = User.find.byId(userId);
			if (user != null)
			{
				Map<String, String> templateValues = new HashMap<String, String>();
				templateValues.put("userid", String.valueOf(user.userId));
				templateValues.put("name", user.name);
				templateValues.put("email", user.email);
				
				String pathToUserTemplate = "~/app/assets/xml/user.xml";
				String xmlMessage = xml.composeXmlMessage(pathToUserTemplate, templateValues);
				return ok(xmlMessage).as("text/xml");
			}
		}
		
		String xmlMessage = xml.composeXmlMessage("USER_NOT_FOUND", "", null);
		return ok(xmlMessage).as("text/xml");
	}
	
	//-----------------------------------------------------------------------//
	
	public static Result createUser()
	{
		XmlProcessor xml = new XmlProcessor();
		Validator validator = new Validator();
		Document xmlDocument = request().body().asXml();
		
		// Validate xml document
		String pathToXsd = Common.resolvePath("~/app/assets/xsd/createUser.xsd");
		boolean isValidXml = xml.validateXmlAgainstXsd(xmlDocument, pathToXsd);
		
		if (!isValidXml)
		{
			String xmlResponse = xml.composeXmlMessage("INVALID_XML_FORMAT", "", null);
			return badRequest(xmlResponse).as("text/xml");
		}
		
		// Gather required user information
		String name = XPath.selectText("/user/name", xmlDocument).trim();
		String email = XPath.selectText("/user/email", xmlDocument).trim();
		String password = XPath.selectText("/user/password", xmlDocument).trim();
		
		// Validate user information
		if (!validator.validateName(name) || !validator.validateEmail(email) || !validator.validatePassword(password))
		{
			String xmlResponse = xml.composeXmlMessage("INVALID_USER_INFO", "", null);
			return badRequest(xmlResponse).as("text/xml");
		}
		
		// Create new user
		User newUser = new User(name, email, password, DateTime.now());
		newUser.save();

		// Verify if create operation succeeded
		boolean operationSucceeded = (newUser.userId > 0);
		
		// Respond with xml message
		String messageCode = (operationSucceeded ? "USER_CREATE_SUCCESS" : "USER_CREATE_FAILED");
		String xmlMessage = xml.composeXmlMessage(messageCode, String.valueOf(newUser.userId), null);
		
		return ok(xmlMessage).as("text/xml");
	}
	
	//-----------------------------------------------------------------------//
	
	public static Result updateUser(long userId)
	{
		XmlProcessor xml = new XmlProcessor();
		Authenticator authenticator = new Authenticator();
		Document xmlDocument = request().body().asXml();
		
		// Validate xml document
		String pathToXsd = Common.resolvePath("~/app/assets/xsd/updateUser.xsd");
		boolean isValidXml = xml.validateXmlAgainstXsd(xmlDocument, pathToXsd);		
		
		if (!isValidXml)
		{
			String xmlResponse = xml.composeXmlMessage("INVALID_XML_FORMAT", "", null);
			return badRequest(xmlResponse).as("text/xml");
		}
		
		boolean operationSucceeded = false;

		// Users may only update their own account, having a valid authtoken
		String authToken = Server.getHeaderValue("AuthToken");
		if (authenticator.validateAuthToken(userId, authToken))
		{
			// Gather required user information
			String name = XPath.selectText("/user/name", xmlDocument);
			String email = XPath.selectText("/user/email", xmlDocument);
			String password = XPath.selectText("/user/password", xmlDocument);
			
			// Find and update user by id
			User user = User.find.byId(userId);
			if (user != null)
			{
				if (!Common.isNullOrEmpty(name)) { user.name = name.trim(); }
				if (!Common.isNullOrEmpty(email)) { user.email = email.trim(); }
				if (!Common.isNullOrEmpty(password)) { user.password = password.trim(); }

				user.save();
				operationSucceeded = true;
			}
		}
		
		// Respond with xml message
		String messageCode = (operationSucceeded ? "USER_UPDATE_SUCCESS" : "USER_UPDATE_FAILED");
		String xmlMessage = xml.composeXmlMessage(messageCode, "", null);
		
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
			if (user != null)
			{
				user.delete();
			}
			
			operationSucceeded = (user != null && User.find.byId(userId) == null);
		}	
		
		// Respond with xml message
		String messageCode = (operationSucceeded ? "USER_DELETE_SUCCESS" : "USER_DELETE_FAILED");
		String xmlMessage = xml.composeXmlMessage(messageCode, "", null);
		
		return ok(xmlMessage).as("text/xml");
	}
}
