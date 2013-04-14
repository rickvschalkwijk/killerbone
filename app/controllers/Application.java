package controllers;

import helpers.Settings;

import org.joda.time.DateTime;

import play.mvc.*;
import utils.EventfulApi;

public class Application extends Controller
{
	public static Result index()
	{
		return ok("Application is online!");
	}
	
	//-----------------------------------------------------------------------//
	
	public static Result repopulateEventfulContent()
	{
		String lastRepopulationTimestamp = Settings.get("Eventful_Repopulated_DateTime");
		
		EventfulApi api = new EventfulApi();
		if (api.repopulateEvents(lastRepopulationTimestamp))
		{
			Settings.set("Eventful_Repopulated_DateTime", DateTime.now().toString("yyyyMMdd HH:mm"));
			return ok("Event database is successfully repopulated.");			
		}
		return ok("Event database is not repopulated.");
	}
}
