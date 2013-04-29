package controllers;

import helpers.*;

import play.mvc.*;

public class Application extends Controller
{
	public static Result index()
	{
		return ok("Application is online!");
	}
	
	public static Result initialize()
	{
		if (Settings.get("application.initialized") == null) 
		{
			Populator.populateAdminUsers();
			Populator.populateLocationCategories();
			Populator.populateLocations();
			Populator.populateEventCategories();
			
			Settings.set("application.initialized", "true");
		}
		return ok("Application is initialized!");
	}	
}