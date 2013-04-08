package controllers;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import com.avaje.ebean.Ebean;

import models.Event;
import models.EventCategory;
import models.Friendship;
import models.Friendship.FriendshipStatus;
import models.FriendshipLocation;
import models.User;
import play.mvc.*;
import views.xml.api.*;

public class Development extends Controller
{
	public static Result getAllUsers()
	{
		List<User> allUsers = User.find.all();
		return ok(userList.render(allUsers).body().trim()).as("text/xml");
	}
	
	public static Result getAllFriendships()
	{
		List<Friendship> allFriendships = Friendship.find.all();

		return ok(friendshipList.render(allFriendships).body().trim()).as("text/xml");
	}	
	
	public static Result getAllEvents()
	{
		List<Event> allEvents = Event.find.all();
		return ok(eventList.render(allEvents).body().trim()).as("text/xml");
	}
	
	public static Result getAllCategories()
	{
		List<EventCategory> allEventCategories = EventCategory.find.all();
		return ok(categoryList.render(allEventCategories).body().trim()).as("text/xml");
	}
	
	//-----------------------------------------------------------------------//
	
	public static Result generateDummyData()
	{
		List<User> users = new ArrayList<User>();
		User onno = new User("Onno Valkering", "onno@valkering.nl", "valkering", DateTime.now());
		onno.isAdmin = true;
		users.add(onno);
		User fons = new User("Fons Eppink", "fons@eppink.nl", "eppink", DateTime.now());
		users.add(fons);		
		User rick = new User("Rick van Schalkwijk", "rick@vschalkwijk.nl", "vschalkwijk", DateTime.now());
		users.add(rick);
		User vincent = new User("Vincent Karsten", "vincent@karsten.nl", "karsten", DateTime.now());
		users.add(vincent);
		
		List<Friendship> friendships = new ArrayList<Friendship>();
		Friendship onnoAndfons = new Friendship(onno, fons);
		onnoAndfons.status = FriendshipStatus.APPROVED;
		friendships.add(onnoAndfons);
		Friendship rickAndvincent = new Friendship(rick, vincent);
		friendships.add(rickAndvincent);
		
		List<FriendshipLocation> locations = new ArrayList<FriendshipLocation>();
		FriendshipLocation onnoLocation = new FriendshipLocation(onno, onnoAndfons, 53.6875, 4, DateTime.now());
		locations.add(onnoLocation);
		
		// Save 
		Ebean.save(users);
		Ebean.save(friendships);
		Ebean.save(locations);
		
		return ok("Dummy data generated!");
	}
}
