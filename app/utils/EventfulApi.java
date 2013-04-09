package utils;

import java.util.ArrayList;
import java.util.List;

import models.EventCategory;

import org.joda.time.DateTime;

import play.Logger;

import com.avaje.ebean.Ebean;
import com.evdb.javaapi.APIConfiguration;
import com.evdb.javaapi.EVDBAPIException;
import com.evdb.javaapi.EVDBRuntimeException;
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
	 * Initialize the Eventful API with keys and start data
	 * @param boolean
	 */
	public static void setupEventfulApi(boolean setupCategories)
	{
		// Setup Eventful API
		APIConfiguration.setApiKey(ConfigFactory.load().getString("eventful.token"));
		APIConfiguration.setEvdbUser(ConfigFactory.load().getString("eventful.user"));
		APIConfiguration.setEvdbPassword(ConfigFactory.load().getString("eventful.password"));
		
		// Setup Eventful Categories
		if (setupCategories && EventCategory.find.all().size() == 0)
		{
			List<EventCategory> categories = new ArrayList<EventCategory>();
			categories.add(new EventCategory("Music", "music"));
			categories.add(new EventCategory("Art", "art"));
			categories.add(new EventCategory("Nightlife", "singles_social"));
			Ebean.save(categories);
		}		
	}	
}
