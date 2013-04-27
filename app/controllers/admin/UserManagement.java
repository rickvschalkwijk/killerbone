package controllers.admin;

import javax.persistence.PersistenceException;

import org.joda.time.DateTime;

import annotations.Security.Authorized;

import com.avaje.ebean.Ebean;
import controllers.admin.routes;
import models.User;
import helpers.Common;
import helpers.Pagination;
import helpers.Pagination.Page;
import helpers.Passwords;
import helpers.Validator;
import play.Logger;
import play.data.DynamicForm;
import play.mvc.Result;
import utils.Mailer;
import utils.Mailer.MailType;
import views.html.admin.userOverview;
import views.html.admin.usersOverview;
import views.html.email.password;
import core.AdminController;

public class UserManagement extends AdminController
{
	@Authorized
	public static Result index(int page, String orderBy, String filter)
	{
		Page<User> users = Pagination.getUserPage(page, 15, orderBy, filter);
		return ok(usersOverview.render(users, orderBy, filter));
	}
	
	@Authorized
	public static Result displayUser(long userId)
	{
		User user = User.find.byId(userId);
		if (user != null)
		{
			//user.initiatedFriendships = null;
			//user.participatedFriendships = null;
			
			return ok(userOverview.render(user));
		}
		return redirect(routes.UserManagement.index(1, "", ""));
	}
	
	//-----------------------------------------------------------------------//
	
	@Authorized(redirectToLogin = false)
	public static Result createUser()
	{
		boolean operationSucceeded = false;
		DynamicForm requestData = DynamicForm.form().bindFromRequest();
		
		// Gather required information
		String name = requestData.get("name");
		String email = requestData.get("email");
		String password = requestData.get("password");
		String isAdmin = requestData.get("isAdmin");
		String isActivated = requestData.get("isActivated");
		
		if (Validator.validateName(name) && Validator.validateEmail(email) && Validator.validatePassword(password))
		{
			try
			{
				String hashedPassword = Passwords.createHash(password);
				
				User newUser = new User(name, email, hashedPassword);
				newUser.creationDate = DateTime.now();
				newUser.isAdmin = !Common.isNullOrEmpty(isAdmin);
				newUser.isActivated = !Common.isNullOrEmpty(isActivated);
				
				Ebean.save(newUser);	
				
				operationSucceeded = true;
				flash("user.create.name", newUser.name);				
			}
			catch(PersistenceException e)
			{
				Logger.error("An error occured while creating user: " + e.getMessage());
			}
		}

		flash(operationSucceeded ? "user.create.success" : "user.create.failed", "");
		return redirect(routes.UserManagement.index(1, "", ""));
	}
	
	@Authorized(redirectToLogin = false)
	public static Result updateUser(long userId)
	{
		DynamicForm requestData = DynamicForm.form().bindFromRequest();
		User user = User.find.byId(userId);
		
		// Gather required information
		String isAdmin = requestData.get("isAdmin");
		String isActivated = requestData.get("isActivated");
		
		// Update user
		if (user != null) {
			user.isAdmin = !Common.isNullOrEmpty(isAdmin);
			user.isActivated  = !Common.isNullOrEmpty(isActivated);
			user.save();
			
			flash("user.updated", "");
		}
		return redirect(routes.UserManagement.displayUser(userId));
	}
	
	@Authorized(redirectToLogin = false)
	public static Result deleteUser(long userId)
	{
		User user = User.find.byId(userId);
		if (user != null)
		{
			Ebean.delete(user);
			flash("user.deleted", user.name);
		}
		return redirect(routes.UserManagement.index(1, "", ""));	
	}
	
	@Authorized(redirectToLogin = false)
	public static Result resetPassword(long userId)
	{
		User user = User.find.byId(userId);
		if (user != null)
		{
			String newPassword = String.valueOf(Passwords.generatePassword(6, 10, 3, 2, 1));
			user.hashedPassword = Passwords.createHash(newPassword);
			Ebean.save(user);
			
			Mailer mailer = new Mailer();
			String subject = "AmsterGuide - Password reset";
			String[] recipients = { user.email };
			String body = password.render(newPassword).body();
			mailer.sendMail(subject, recipients, body, MailType.HTML);
			
			flash("user.password-reset", "");
		}
		return redirect(routes.UserManagement.displayUser(userId));
	}
}