package controllers.admin;

import models.User;
import helpers.Pagination;
import helpers.Pagination.Page;
import play.mvc.Result;
import utils.Security.Authorized;
import views.html.admin.usersOverview;
import core.AdminController;

public class UserManagement extends AdminController
{
	@Authorized
	public static Result index(int page)
	{
		// Show users
		Page<User> users = Pagination.page(User.class, page, 25, "", "", "");
		return ok(usersOverview.render(users));
	}
	
	@Authorized
	public static Result displayUser(long userId)
	{
		return ok();
	}
}