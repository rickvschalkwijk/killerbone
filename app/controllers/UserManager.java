package controllers;

import java.io.File;
import helpers.Common;
import helpers.Server;
import models.User;
import org.joda.time.DateTime;
import org.w3c.dom.Document;

import play.api.Play;
import play.libs.XPath;
import play.mvc.*;
import utils.Authenticator;
import helpers.Validator;
import utils.XmlProcessor;
import views.xml.*;

public class UserManager extends Controller
{
	public static Result getUser(long userId)
	{
		Authenticator authenticator = new Authenticator();
		
		// Users may only get information about their own account
		String authToken = Server.getHeaderValue("AuthToken");
		if (authenticator.validateAuthToken(userId, authToken))
		{
			// Find user by id
			User user = User.find.byId(userId);
			if (user != null)
			{
				return ok(userSingle.render(user).body().trim()).as("text/xml");
			}
		}
		
		return ok(message.render("USER_GET_FAILED", "").body().trim()).as("text/xml");
	}
	
	//-----------------------------------------------------------------------//
	
	public static Result createUser()
	{
		XmlProcessor xml = new XmlProcessor();
		Validator validator = new Validator();
		Document xmlDocument = request().body().asXml();
		
		// Validate xml document
		File xsdFile = Play.current().getFile("/public/xsd/userCreate.xsd");
		boolean isValidXml = xml.validateXmlAgainstXsd(xmlDocument, xsdFile.getAbsolutePath());	
		
		if (!isValidXml)
		{
			return badRequest(message.render("XML_INVALID", "").body().trim()).as("text/xml");
		}
		
		// Gather required user information
		String name = XPath.selectText("/user/name", xmlDocument).trim();
		String email = XPath.selectText("/user/email", xmlDocument).trim();
		String password = XPath.selectText("/user/password", xmlDocument).trim();
		
		boolean operationSucceeded = false;
		
		// Validate user information
		if (validator.validateName(name) || validator.validateEmail(email) || validator.validatePassword(password))
		{
			// Create new user
			User newUser = new User(name, email, password, DateTime.now());
			newUser.save();
			
			operationSucceeded = (newUser.userId > 0);
		}
				
		// Respond with xml message
		String messageCode = (operationSucceeded ? "USER_CREATE_SUCCESS" : "USER_CREATE_FAILED");
		return ok(message.render(messageCode, "").body().trim()).as("text/xml");
	}
	
	//-----------------------------------------------------------------------//
	
	public static Result updateUser(long userId)
	{
		XmlProcessor xml = new XmlProcessor();
		Authenticator authenticator = new Authenticator();
		Document xmlDocument = request().body().asXml();
		
		// Validate xml document
		File xsdFile = Play.current().getFile("/public/xsd/userUpdate.xsd");
		boolean isValidXml = xml.validateXmlAgainstXsd(xmlDocument, xsdFile.getAbsolutePath());	
		
		if (!isValidXml)
		{
			return badRequest(message.render("XML_INVALID", "").body().trim()).as("text/xml");
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
		
		String messageCode = (operationSucceeded ? "USER_UPDATE_SUCCESS" : "USER_UPDATE_FAILED");
		return ok(message.render(messageCode, "").body().trim()).as("text/xml");
	}

	//-----------------------------------------------------------------------//
	
	public static Result deleteUser(long userId)
	{
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
		
		String messageCode = (operationSucceeded ? "USER_DELETE_SUCCESS" : "USER_DELETE_FAILED");
		return ok(message.render(messageCode, "").body().trim()).as("text/xml");
	}
}
