package controllers.admin;

import helpers.Settings;

import org.joda.time.DateTime;

import annotations.Security.Authorized;

import controllers.admin.routes;
import play.mvc.*;
import utils.EventfulApi;
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
	
	//-----------------------------------------------------------------------//
	
	@Authorized
	public static Result repopulateEventDatabase()
	{
		String lastRepopulationTimestamp = Settings.get("eventdatabase.repopulationdate");
		
		EventfulApi api = new EventfulApi();
		if (api.repopulateEvents(lastRepopulationTimestamp))
		{
			Settings.set("eventdatabase.repopulationdate", DateTime.now().toString("yyyyMMdd HH:mm"));
			flash("events.updated", "");
		}
		else
		{
			flash("events.notupdated", "");
		}
		return redirect(routes.Dashboard.index());
	}
}