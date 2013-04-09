package controllers.api;

import helpers.Common;
import models.User;
import org.joda.time.DateTime;
import org.w3c.dom.Document;

import com.avaje.ebean.Ebean;

import core.ApiController;

import play.Logger;
import play.libs.XPath;
import play.mvc.*;
import helpers.Validator;
import views.xml.api.*;

public class UserManager extends ApiController
{
	public static Result getUser(long userId)
	{
		if (!isAuthorized(userId)) { return unAuthorized(); }
		
		User user = User.find.byId(userId);
		if (user != null)
		{
			return ok(userSingle.render(user).body().trim()).as("text/xml");
		}
		return operationFailed();
	}
	
	//-----------------------------------------------------------------------//
	
	public static Result createUser()
	{
		// Parse required information
		boolean operationSucceeded = false;
		Document xmlDocument = request().body().asXml();
		
		try
		{
			String name = XPath.selectText("/user/name", xmlDocument).trim();
			String email = XPath.selectText("/user/email", xmlDocument).trim();
			String password = XPath.selectText("/user/password", xmlDocument).trim();
		
			// Validate user information
			if (Validator.validateName(name) || Validator.validateEmail(email) || Validator.validatePassword(password))
			{
				User newUser = new User(name, email, password, DateTime.now());
				
				Ebean.save(newUser);
				operationSucceeded = (newUser.userId != 0);
			}
		}
		catch(RuntimeException e)
		{
			Logger.error("An error occured while creating user: " + e.getMessage());
		}
		return (operationSucceeded ? operationSuccess() : operationFailed());
	}
	
	//-----------------------------------------------------------------------//
	
	public static Result updateUser(long userId)
	{
		if (!isAuthorized(userId)) { return unAuthorized(); }
		
		// Parse required information
		boolean operationSucceeded = false;
		Document xmlDocument = request().body().asXml();
		
		try
		{
			String name = XPath.selectText("/user/name", xmlDocument);
			String email = XPath.selectText("/user/email", xmlDocument);
			String password = XPath.selectText("/user/password", xmlDocument);
			
			User user = User.find.byId(userId);
			if (user != null)
			{
				if (!Common.isNullOrEmpty(name)) { user.name = name.trim(); }
				if (!Common.isNullOrEmpty(email)) { user.email = email.trim(); }
				if (!Common.isNullOrEmpty(password)) { user.password = password.trim(); }

				Ebean.save(user);
				operationSucceeded = true;
			}
		}
		catch(RuntimeException e)
		{
			Logger.error("An error occured while updating user (" + userId + "): " + e.getMessage());
		}
		return (operationSucceeded ? operationSuccess() : operationFailed());
	}

	//-----------------------------------------------------------------------//
	
	public static Result deleteUser(long userId)
	{
		if (!isAuthorized(userId)) { return unAuthorized(); }
		
		User user = User.find.byId(userId);
		if (user != null)
		{
			Ebean.delete(user);
			
			return operationSuccess();
		}
		return operationFailed();
	}
}
