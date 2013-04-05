package controllers.admin;

import play.mvc.Result;
import utils.Security.Authorized;
import views.html.admin.eventsOverview;
import core.AdminController;

public class EventManagement extends AdminController
{
	@Authorized
	public static Result index()
	{
		return ok(eventsOverview.render());
	}
}
