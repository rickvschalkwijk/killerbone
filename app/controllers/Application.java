package controllers;

import helpers.Settings;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import com.avaje.ebean.Ebean;
import com.evdb.javaapi.data.Event;
import com.evdb.javaapi.data.SearchResult;

import models.EventCategory;
import play.Logger;
import play.mvc.*;
import utils.EventfulApi;
import views.html.admin.login;

public class Application extends Controller
{
	public static Result index()
	{
		return ok("Application is online!");
	}
	
	public static Result login()
	{
		return ok(login.render());
	}
	
	public static Result logout()
	{
		session().clear();
		return redirect(routes.Application.login());
	}
	
	//-----------------------------------------------------------------------//
	
	public static Result repopulateEventfulContent()
	{
		// Only repopulate after 12 hours from previous repopulation
		String eventfulPreviousRepopulation = Settings.get("Eventful_Repopulated_DateTime");
		long checkpointTimestamp = DateTime.now().minusHours(12).getMillis();
		
		if (eventfulPreviousRepopulation != null && DateTimeFormat.forPattern("yyyyMMdd HH:mm").parseDateTime(eventfulPreviousRepopulation).isAfter(checkpointTimestamp))
		{
			return ok();
		}
		
		// Prepare eventful api
		EventfulApi api = new EventfulApi();
		String location = "Amsterdam";
		String dateRange = api.convertToDateRange(DateTime.now(), DateTime.now().plusHours(24*3));

		// Retrieve events in each category
		List<EventCategory> allEventCategories = EventCategory.find.all();
		for (EventCategory category : allEventCategories)
		{
			SearchResult eventSearchResult = api.performEventSearch(location, category.systemName, dateRange);
			
			// Process events
			List<Event> events = eventSearchResult.getEvents();
			for (Event event : events)
			{			
				try
				{
					models.Event newEvent = new models.Event(event, category);
					newEvent.creationTimestamp = System.currentTimeMillis();
					Ebean.save(newEvent);
				}
				catch (Exception e)
				{
					Logger.error("Failed to process event: " + event.getTitle() + " / " + event.getSeid());
				}
			}
		}
		
		// Delete old, expired events
		List<models.Event> expiredEvents = models.Event.find.where().lt("endDate", DateTime.now().minusHours(12)).findList();
		Ebean.delete(expiredEvents);
		
		Settings.set("Eventful_Repopulated_DateTime", DateTime.now().toString("yyyyMMdd HH:mm"));
		return ok();
	}
}
