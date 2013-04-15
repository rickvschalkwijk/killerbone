package controllers;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import helpers.PasswordHash;
import models.User;
import org.w3c.dom.Document;

import play.data.DynamicForm;
import play.libs.XPath;
import play.mvc.*;
import views.xml.api.*;
import utils.Authenticator;
import controllers.admin.routes;

public class Authentication extends Controller
{
	public static Result performApiAuthentication() throws NoSuchAlgorithmException, InvalidKeySpecException
	{
		Authenticator authenticator = new Authenticator();
		Document xmlDocument = request().body().asXml();
		
		// Gather required information
		String email = XPath.selectText("/user/email", xmlDocument).trim();
		String password = XPath.selectText("/user/password", xmlDocument).trim();
		
		// Validate user credentials
		User user = User.find.where().eq("email", email).findUnique();
		if (user != null && PasswordHash.validatePassword(password, user.password) && user.isActivated)
		{
			User.updateLastActivity(user.userId);
			
			String authToken = authenticator.generateAuthToken(user.userId, false);
			return ok(authtoken.render(authToken, user.userId).body().trim()).as("text/xml");
		}
		else
		{
			return ok(message.render("AUTHTOKEN_CREATE_FAILED", "").body().trim()).as("text/xml");
		}		
	}
	
	public static Result performAdminAuthentication() throws NoSuchAlgorithmException, InvalidKeySpecException
	{
		Authenticator authenticator = new Authenticator();
		DynamicForm requestData = DynamicForm.form().bindFromRequest();
		
		// Gather required information
		String email = requestData.get("email");
		String password = requestData.get("password");
		
		// Validate user credentials
		User user = User.find.where().eq("email", email).findUnique();
		if (user != null && PasswordHash.validatePassword(password, user.password) && user.isAdmin)
		{
			String authToken = authenticator.generateAuthToken(user.userId, true);
			session("UserId", String.valueOf(user.userId));
			session("Name", user.name);
			session("AuthToken", authToken);
			return redirect(routes.Dashboard.index());
		}
		else
		{ 
			// Redirect back to login page
			flash("login.failed", "");
			flash("login.email", email);
			flash("login.password", password);
			return redirect(routes.Dashboard.login());
		}				
	}
}
