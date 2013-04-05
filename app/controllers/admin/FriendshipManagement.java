package controllers.admin;

import play.mvc.Result;
import utils.Security.Authorized;
import views.html.admin.friendshipsOverview;
import core.AdminController;

public class FriendshipManagement extends AdminController
{
	@Authorized
	public static Result index()
	{
		return ok(friendshipsOverview.render());
	}
}
