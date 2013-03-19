package utils;

import play.*;
import play.mvc.Http.RequestHeader;
import play.mvc.*;

public class Global extends GlobalSettings
{
	@Override
	public void onStart(Application app)
	{
		// Application started!
	}

	@Override
	public void onStop(Application app)
	{
		// Application stoped!
	}

	@Override
	public Result onBadRequest(RequestHeader request, String error)
	{
		XmlProcessor xml = new XmlProcessor();
		
		String xmlMessage = xml.composeXmlMessage("INVALID_REQUEST", "", null);
		return Results.badRequest(xmlMessage).as("text/xml");
	}
}
