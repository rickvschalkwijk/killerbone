package controllers.api;

import java.util.ArrayList;
import java.util.List;

import com.avaje.ebean.Expr;

import core.ApiController;

import models.Event;
import models.EventCategory;
import play.mvc.Result;
import views.xml.api.*;

public class EventManager extends ApiController
{
	public static Result getAllEvents()
	{
		List<Event> events = Event.find.all();
		return ok(eventList.render(events).body().trim()).as("text/xml");
	}
	
	//-----------------------------------------------------------------------//
	
	public static Result getNewEvents(long timestamp)
	{
		List<Event> events =  Event.find.where()
										.or(Expr.ge("creationTimestamp", timestamp), 
										    Expr.ge("modifiedTimestamp", timestamp))
										.findList();
		
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
		return operationFailed();
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
				if (event.creationTimestamp >= timestamp || event.modifiedTimestamp >= timestamp)
				{
					newEvents.add(event);
				}
			}
			return ok(eventList.render(newEvents).body().trim()).as("text/xml");
		}
		return operationFailed();
	}	
	
	//-----------------------------------------------------------------------//
	
	public static Result getAllCategories()
	{
		List<EventCategory> categories = EventCategory.find.all();
		return ok(categoryList.render(categories).body().trim()).as("text/xml");
	}
}
