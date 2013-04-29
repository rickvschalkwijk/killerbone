package utils;

import helpers.Common;

import java.util.List;

import models.EventCategory;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import play.Logger;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.evdb.javaapi.APIConfiguration;
import com.evdb.javaapi.EVDBAPIException;
import com.evdb.javaapi.EVDBRuntimeException;
import com.evdb.javaapi.data.Event;
import com.evdb.javaapi.data.SearchResult;
import com.evdb.javaapi.data.request.EventSearchRequest;
import com.evdb.javaapi.operations.EventOperations;
import com.typesafe.config.ConfigFactory;

public class EventfulApi
{
	/**
	 * Performs an eventful api event search.
	 * @param String
	 * @param String
	 * @param String
	 * @return SearchResult
	 */
	public SearchResult performEventSearch(String location, String categoryId, String dateRange)
	{	
		EventOperations eo = new EventOperations();
		EventSearchRequest esr = new EventSearchRequest();
		
		// Compose search action
		esr.setLocation(location);
		esr.setDateRange(dateRange);
		esr.setCategory(categoryId);
		esr.setPageSize(999);

		// Perform search
		SearchResult sr = null;
		try
		{
			sr = eo.search(esr);			
		}
		catch (EVDBRuntimeException var)
		{
			Logger.error(var.getMessage());
		}
		catch (EVDBAPIException var)
		{
			Logger.error(var.getMessage());
		}
		
		return sr;
	}
	
	/**
	 * Repopulates the event database with up-to-date events
	 * @param String
	 * @return boolean
	 */
	public boolean repopulateEvents(String previousRepopulationTimestamp)
	{
		long checkpointTimestamp = DateTime.now().minusHours(12).getMillis();
		if (previousRepopulationTimestamp != null && DateTimeFormat.forPattern("yyyyMMdd HH:mm").parseDateTime(previousRepopulationTimestamp).isAfter(checkpointTimestamp))
		{
			return false;
		}
		
		// Prepare eventful api
		String location = "Amsterdam";
		String dateRange = convertToDateRange(DateTime.now(), DateTime.now().plusHours(24*4));

		// Retrieve events in each category
		List<EventCategory> allEventCategories = EventCategory.find.all();
		for (EventCategory category : allEventCategories)
		{
			SearchResult eventSearchResult = performEventSearch(location, category.systemName, dateRange);
			TranslatorApi translator = new TranslatorApi();
			
			// Process events
			List<Event> events = eventSearchResult.getEvents();
			for (Event event : events)
			{			
				try
				{
					models.Event newEvent = new models.Event(event, category);
					newEvent.description = translator.toEnglish(newEvent.description);
					newEvent.creationTimestamp = Common.getTimestamp();
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
		
		return true;
	}
	
	//-----------------------------------------------------------------------//
	
	/**
	 * Converts dates to an eventful api compatible daterange string.
	 * @param DateTime
	 * @param DateTime
	 * @return String
	 */
	public String convertToDateRange(DateTime from, DateTime until)
	{
		return String.format("%s-%s", from.toString("yyyyMMdd00"), until.toString("yyyyMMdd00"));
	}
	
	/**
	 * Initialize the Eventful API with keys and start data.
	 * @param boolean
	 */
	public static void setupEventfulApi()
	{
		APIConfiguration.setApiKey(ConfigFactory.load().getString("eventful.token"));
		APIConfiguration.setEvdbUser(ConfigFactory.load().getString("eventful.user"));
		APIConfiguration.setEvdbPassword(ConfigFactory.load().getString("eventful.password"));	
	}	
	
	//-----------------------------------------------------------------------//
	
	/**
	 * Get the amount of events currently stored in the database.
	 * @return int
	 */
	public static int getNumberOfEvents()
	{
		return models.Event.find.findRowCount();
	}
	
	/**
	 * Get the amount of events that where added today.
	 * @return long
	 */
	public static long getNumberOfTodayNewEvents()
	{
		long beginOfDayInMillis = new DateTime().withMillisOfDay(0).getMillis();
		return models.Event.find.where(Expr.ge("creationTimestamp", beginOfDayInMillis)).findRowCount();
	}
}
