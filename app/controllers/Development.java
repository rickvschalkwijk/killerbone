package controllers;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import com.avaje.ebean.Ebean;

import models.Event;
import models.EventCategory;
import models.Friendship;
import models.User;
import play.mvc.*;
import utils.EventfulApi;
import views.xml.*;

public class Development extends Controller
{
	public static Result getAllUsers()
	{
		List<User> allUsers = User.find.all();
		return ok(userList.render(allUsers));
	}
	
	public static Result getAllFriendships()
	{
		List<Friendship> allFriendships = Friendship.find.all();
		return ok(friendshipList.render(allFriendships));
	}	
	
	public static Result getAllEvents()
	{
		List<Event> allEvents = Event.find.all();
		return ok(eventList.render(allEvents));
	}
	
	public static Result getAllCategories()
	{
		List<EventCategory> allEventCategories = EventCategory.find.all();
		return ok(categoryList.render(allEventCategories));
	}
	
	//-----------------------------------------------------------------------//
	
	public static Result populateCategoriesTable()
	{
		Ebean.delete(EventCategory.find.all());
		
		List<EventCategory> categories = new ArrayList<EventCategory>();
		categories.add(new EventCategory("Music", "music"));
		categories.add(new EventCategory("Art", "art"));
		categories.add(new EventCategory("Nightlife", "singles_social"));
		
		Ebean.save(categories);

		return ok("category table (re-)populated!");
	}
	
	public static Result populateEventsTable()
	{
		Ebean.delete(Event.find.all());
		
		EventfulApi api = new EventfulApi();
		String location = "Amsterdam";
		String dateRange = api.convertToDateRange(DateTime.now(), DateTime.now().plusDays(7));
		
		List<EventCategory> categories = EventCategory.find.all();
		for (EventCategory category : categories)
		{
			List<com.evdb.javaapi.data.Event> apiEvents = api.performEventSearch(location, category.systemName, dateRange).getEvents();
			for(com.evdb.javaapi.data.Event apiEvent : apiEvents)
			{
				try
				{
					Ebean.save(new Event(apiEvent, category));
				}
				catch(Exception e) { }
			}
		}
		
		return ok("event table (re-)populated!");
	}
}
