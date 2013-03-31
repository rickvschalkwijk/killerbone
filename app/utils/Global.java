package utils;

import com.evdb.javaapi.APIConfiguration;
import com.typesafe.config.ConfigFactory;

import play.*;
import play.mvc.Http.RequestHeader;
import play.mvc.*;
import views.xml.message;

public class Global extends GlobalSettings
{
	@Override
	public void onStart(Application app)
	{
		// Setup Eventful API
		APIConfiguration.setApiKey(ConfigFactory.load().getString("eventful.token"));
		APIConfiguration.setEvdbUser(ConfigFactory.load().getString("eventful.user"));
		APIConfiguration.setEvdbPassword(ConfigFactory.load().getString("eventful.password"));
	}

	@Override
	public void onStop(Application app)
	{
		
	}

	@Override
	public Result onBadRequest(RequestHeader request, String error)
	{
		return Results.badRequest(message.render("INVALID_REQUEST", ""));
	}
}
