package utils;

import com.avaje.ebean.Ebean;

import models.LocationCategory;

public class Populator 
{
	public static void populateLocationCategories()
	{
		if (LocationCategory.find.getMaxRows() == 0)
		{
			LocationCategory museums = new LocationCategory("Museams", "museams");
			Ebean.save(museums);
			
			LocationCategory parks = new LocationCategory("Parks", "parks");
			Ebean.save(parks);
			
			LocationCategory transportation = new LocationCategory("Transportation", "transportation");
			Ebean.save(transportation);
			
			LocationCategory restaurantsAndPubs = new LocationCategory("Restaurants/Pubs", "restaurants_pubs");
			Ebean.save(restaurantsAndPubs);
			
			LocationCategory cafes = new LocationCategory("Cafés", "cafes");
			Ebean.save(cafes);
			
			LocationCategory nightclubs = new LocationCategory("Nightclubs", "nightclubs");
			Ebean.save(nightclubs);
		}
	}
}
