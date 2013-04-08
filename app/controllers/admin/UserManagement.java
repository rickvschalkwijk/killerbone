package controllers.admin;

import org.joda.time.DateTime;

import com.avaje.ebean.Ebean;

import models.User;
import helpers.Common;
import helpers.Pagination;
import helpers.Pagination.Page;
import helpers.Validator;
import play.data.DynamicForm;
import play.mvc.Result;
import utils.Security.Authorized;
import views.html.admin.userOverview;
import views.html.admin.usersOverview;
import core.AdminController;

public class UserManagement extends AdminController
{
	@Authorized
	public static Result index(int page, String orderBy, String filter)
	{
		// Display all users
		Page<User> users = Pagination.getUserPage(page, 25, orderBy, filter);
		
		
		return ok(usersOverview.render(users, orderBy, filter));
	}
	
	@Authorized
	public static Result displayUser(long userId)
	{
		User user = User.find.byId(userId);
		if (user != null)
		{
			return ok(userOverview.render(user));
		}
		return redirect(controllers.admin.routes.UserManagement.index(1, "", ""));
	}
	
	//-----------------------------------------------------------------------//
	
	@Authorized(redirectToLogin = false)
	public static Result createUser()
	{
		DynamicForm requestData = DynamicForm.form().bindFromRequest();
		
		// Gather required information
		String name = requestData.get("name");
		String email = requestData.get("email");
		String password = requestData.get("password");
		String isAdmin = requestData.get("isAdmin");
		String isActivated = requestData.get("isActivated");
		
		if (Validator.validateName(name) && Validator.validateEmail(email) && Validator.validatePassword(password))
		{
			User newUser = new User(name, email, password, DateTime.now());
			
			// Set options
			if (!Common.isNullOrEmpty(isActivated) && isActivated.equals("on")) 
			{
				newUser.isActivated = true;
			}
			if (!Common.isNullOrEmpty(isAdmin) && isAdmin.equals("on"))
			{
				newUser.isAdmin = true;
			}
			
			// Create user
			Ebean.save(newUser);			
			flash("user.create.success", "");
			flash("user.create.name", newUser.name);
			flash("user.create.id", String.valueOf(newUser.userId));
		}
		else
		{
			flash("user.create.failed", "");
		}
		return redirect(controllers.admin.routes.UserManagement.index(1, "", ""));
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
			user.isAdmin = (isAdmin != null && isAdmin.equals("on") ? true : false);
			user.isActivated  = (isActivated != null && isActivated.equals("on") ? true : false);
			user.save();
			
			flash("user.updated", "");
		}
		return redirect(controllers.admin.routes.UserManagement.displayUser(userId));
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
		return redirect(controllers.admin.routes.UserManagement.index(1, "", ""));	
	}
	
	@Authorized(redirectToLogin = false)
	public static Result resetPassword(long userId)
	{
		User user = User.find.byId(userId);
		if (user != null)
		{
			// TODO: reset password logic
			
			flash("user.password-reset", "");
		}
		return redirect(controllers.admin.routes.UserManagement.displayUser(userId));
	}
}