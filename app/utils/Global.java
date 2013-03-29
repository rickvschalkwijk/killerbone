package utils;

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
	}

	@Override
	public void onStop(Application app)
	{
		
	}

	@Override
	public Result onBadRequest(RequestHeader request, String error)
	{
		XmlProcessor xml = new XmlProcessor();

		String xmlMessage = xml.composeXmlMessage("INVALID_REQUEST", error, null);
		return Results.badRequest(xmlMessage).as("text/xml");
	}
}
