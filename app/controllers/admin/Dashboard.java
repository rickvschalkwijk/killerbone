package controllers.admin;

import play.mvc.*;
import utils.Security.Authorized;
import views.html.admin.dashboard;
import core.AdminController;

public class Dashboard extends AdminController
{
	@Authorized
	public static Result index()
	{
		return ok(dashboard.render());
	}
}