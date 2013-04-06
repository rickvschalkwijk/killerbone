package utils;

import java.util.ArrayList;
import java.util.List;

import models.EventCategory;

import com.avaje.ebean.Ebean;
import com.evdb.javaapi.APIConfiguration;
import com.typesafe.config.ConfigFactory;

import play.*;
import play.mvc.Http.RequestHeader;
import play.mvc.*;

public class Global extends GlobalSettings
{
	@Override
	public void onStart(Application app)
	{
		// Setup Eventful API
		APIConfiguration.setApiKey(ConfigFactory.load().getString("eventful.token"));
		APIConfiguration.setEvdbUser(ConfigFactory.load().getString("eventful.user"));
		APIConfiguration.setEvdbPassword(ConfigFactory.load().getString("eventful.password"));
		
		// Setup Eventful Categories
		if (EventCategory.find.all().size() == 0)
		{
			List<EventCategory> categories = new ArrayList<EventCategory>();
			categories.add(new EventCategory("Music", "music"));
			categories.add(new EventCategory("Art", "art"));
			categories.add(new EventCategory("Nightlife", "singles_social"));
			Ebean.save(categories);
		}
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
