package controllers.admin;

import play.mvc.*;
import views.html.admin.system;
import core.AdminController;

public class System extends AdminController
{
	public static Result index()
	{
		return ok(system.render());
	}
}
