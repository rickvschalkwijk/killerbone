package utils;

import helpers.Settings;

import org.joda.time.DateTime;

import play.*;
import play.mvc.Http.RequestHeader;
import play.mvc.*;

public class Global extends GlobalSettings
{
	@Override
	public void onStart(Application app)
	{
		// Setup statistics
		Settings.set("application.startupdate", DateTime.now().toString("hh:mm a, dd MMMM yyy"));
		
		// Setup events
		EventfulApi.setupEventfulApi(true);
		
		// Setup locations
		Populator.populateLocationCategories();
	}

	@Override
	public void onStop(Application app)
	{
		
	}
	
	@Override
	public Result onBadRequest(RequestHeader request, String error)
	{
		return Results.badRequest();
	}
}
