package utils;

import play.Logger;

import com.evdb.javaapi.EVDBAPIException;
import com.evdb.javaapi.EVDBRuntimeException;
import com.evdb.javaapi.data.SearchResult;
import com.evdb.javaapi.data.request.EventSearchRequest;
import com.evdb.javaapi.operations.EventOperations;

public class EventfulApi
{
	public SearchResult PerformSearch(String keywords)
	{	
		EventOperations eo = new EventOperations();
		EventSearchRequest esr = new EventSearchRequest();
		
		esr.setLocation("Amsterdam");
		esr.setDateRange("2012050200-2013052100");
		esr.setPageSize(20);
		esr.setPageNumber(1);

		SearchResult sr = null;
		try
		{
			sr = eo.search(esr);
			if (sr.getTotalItems() > 1)
			{
				return sr;
			}
		}
		catch (EVDBRuntimeException var)
		{
			Logger.info(var.getMessage());
		}
		catch (EVDBAPIException var)
		{
			Logger.info(var.getMessage());
		}
		
		return null;
	}
	
	//-----------------------------------------------------------------------//
	
	public void RepopulateEventTable()
	{
		
	}
	
	public void DeleteAllEvents()
	{
		
	}
}
