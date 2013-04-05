package controllers.api;

import java.util.ArrayList;
import java.util.List;

import models.Event;
import models.EventCategory;
import play.mvc.Controller;
import play.mvc.Result;
import views.xml.api.*;

public class EventManager extends Controller
{
	public static Result getAllEvents()
	{
		List<Event> events = Event.find.all();
		return ok(eventList.render(events).body().trim()).as("text/xml");
	}
	
	//-----------------------------------------------------------------------//
	
	public static Result getNewEvents(long timestamp)
	{
		List<Event> events = Event.find.where().gt("creationTimestamp", timestamp).findList();
		return ok(eventList.render(events).body().trim()).as("text/xml");
	}
	
	//-----------------------------------------------------------------------//
	
	public static Result getAllEventsFromCategory(String categoryName)
	{
		EventCategory category = EventCategory.find.where().eq("systemName", categoryName).findUnique();
		
		if (category != null)
		{
			return ok(eventList.render(category.events).body().trim()).as("text/xml");
		}
		return ok(message.render("CATEGORY_EVENTS_GET_FAILED", "").body().trim()).as("text/xml");
	}

	//-----------------------------------------------------------------------//
	
	public static Result getNewEventsFromCategory(String categoryName, long timestamp)
	{
		EventCategory category = EventCategory.find.where().eq("systemName", categoryName).findUnique();
		
		if (category != null)
		{
			List<Event> events = category.events;
			List<Event> newEvents = new ArrayList<Event>();
			
			// Filter new events
			for (Event event : events)
			{
				if (event.creationTimestamp > timestamp)
				{
					newEvents.add(event);
				}
			}
			return ok(eventList.render(newEvents).body().trim()).as("text/xml");
		}
		return ok(message.render("CATEGORY_NEW_EVENTS_GET_FAILED", "").body().trim()).as("text/xml");
	}	
	
	//-----------------------------------------------------------------------//
	
	public static Result getAllCategories()
	{
		List<EventCategory> categories = EventCategory.find.all();
		return ok(categoryList.render(categories).body().trim()).as("text/xml");
	}
}
