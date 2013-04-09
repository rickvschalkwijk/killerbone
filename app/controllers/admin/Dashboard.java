package controllers.admin;

import controllers.admin.routes;
import play.mvc.*;
import utils.Security.Authorized;
import views.html.admin.dashboard;
import views.html.admin.login;
import core.AdminController;

public class Dashboard extends AdminController
{
	@Authorized
	public static Result index()
	{
		return ok(dashboard.render());
	}
	
	public static Result login()
	{
		return ok(login.render());
	}
	
	public static Result logout()
	{
		session().clear();
		return redirect(routes.Dashboard.login());
	}	
}