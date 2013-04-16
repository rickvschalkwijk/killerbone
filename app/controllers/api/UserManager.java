package controllers.api;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import helpers.Common;
import helpers.Cryptography;
import helpers.Passwords;
import models.User;
import org.joda.time.DateTime;
import org.w3c.dom.Document;

import com.avaje.ebean.Ebean;

import core.ApiController;

import play.Logger;
import play.libs.XPath;
import play.mvc.*;
import helpers.Validator;
import utils.Mailer;
import utils.Mailer.MailType;
import views.html.api.userActivation;
import views.html.email.welcome;
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
				String hashedPassword = Passwords.createHash(password);

				User newUser = new User(name, email, hashedPassword);
				newUser.creationDate = DateTime.now();
				
				Ebean.save(newUser);
				operationSucceeded = (newUser.userId != 0);

				if (operationSucceeded)
				{
					sendAccountActivationMail(newUser.userId, email);
				}
			}
		}
		catch(RuntimeException e) {
			Logger.error("An error occured while creating user: " + e.getMessage());
		} catch(UnsupportedEncodingException e) {
			Logger.error("An error occured while creating user: " + e.getMessage());
		}
		return (operationSucceeded ? operationSuccess() : operationFailed());
	}
	
	private static void sendAccountActivationMail(long userId, String email) throws UnsupportedEncodingException
	{
		String activationCode = Cryptography.encrypt("activate:" + String.valueOf(userId));
		String subject = "AmsterGuide - Account Activation";
		String[] recipients = { email };
		String body = welcome.render(URLEncoder.encode(activationCode, "UTF-8")).body();
		
		Mailer mailer = new Mailer();
		mailer.sendMail(subject, recipients, body, MailType.HTML);
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
				if (!Common.isNullOrEmpty(password)) { user.hashedPassword = Passwords.createHash(password.trim()); }
				user.lastActivityDate = DateTime.now();
				
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
	
	//-----------------------------------------------------------------------//
	
	public static Result activateUser(String code) throws UnsupportedEncodingException
	{
		code = URLDecoder.decode(code, "UTF-8");
		String decryptedCode = Cryptography.decrypt(code);
		if (!Common.isNullOrEmpty(decryptedCode) && Validator.validateActivationCode(decryptedCode))
		{
			String userIdPartOfCode = decryptedCode.substring(9);
			long userId = Long.parseLong(userIdPartOfCode);
			
			User user = User.find.byId(userId);
			if (user != null && !user.isActivated)
			{
				user.isActivated = true;
				user.lastActivityDate = DateTime.now();
				Ebean.save(user);
				
				flash("user.activated", "");
			}
		}
		return ok(userActivation.render());
	}	
}
