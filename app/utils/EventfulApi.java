package utils;

import org.joda.time.DateTime;

import play.Logger;

import com.evdb.javaapi.EVDBAPIException;
import com.evdb.javaapi.EVDBRuntimeException;
import com.evdb.javaapi.data.SearchResult;
import com.evdb.javaapi.data.request.EventSearchRequest;
import com.evdb.javaapi.operations.EventOperations;

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
}
