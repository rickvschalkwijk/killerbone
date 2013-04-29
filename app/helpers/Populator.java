package helpers;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import com.avaje.ebean.Ebean;

import models.EventCategory;
import models.Location;
import models.LocationCategory;
import models.User;

public class Populator 
{
	public static void populateAdminUsers()
	{
		if (User.find.getMaxRows() == 0)
		{
			String hashedAdminPassword = Passwords.createHash("valkering");
			User admin = new User("Onno Valkering", "onnovalkering@gmail.com", hashedAdminPassword);
			admin.creationDate = DateTime.now();
			admin.isAdmin = true;
			admin.isActivated = true;
			Ebean.save(admin);
			
			String hashedAuditPassword = Passwords.createHash("audit");
			User audit = new User("Audit AmsterGuide", "audit@amsterguide.nl", hashedAuditPassword);
			audit.creationDate = DateTime.now();
			audit.isAdmin = true;
			audit.isActivated = true;
			Ebean.save(audit);	
		}
	}
	
	public static void populateLocationCategories()
	{
		if (LocationCategory.find.getMaxRows() == 0)
		{
			List<LocationCategory> locationCategories = new ArrayList<LocationCategory>();
			locationCategories.add(new LocationCategory("Museams", "museams"));
			locationCategories.add(new LocationCategory("Parks", "parks"));
			locationCategories.add(new LocationCategory("Transportation", "transportation"));
			locationCategories.add(new LocationCategory("Restaurants/Pubs", "restaurants_pubs"));
			locationCategories.add(new LocationCategory("Cafés", "cafes"));
			locationCategories.add(new LocationCategory("Nightclubs", "nightclubs"));
			Ebean.save(locationCategories);
		}
	}
	
	public static void populateLocations()
	{
		if (Location.find.getMaxRows() == 0)
		{
			List<Location> locations = new ArrayList<Location>();
			String description = "";
			
			LocationCategory museams = LocationCategory.find.where().eq("systemName", "museams").findUnique();
			if (museams != null)
			{
				description = "The Rembrandt House Museum is a house in the Jodenbreestraat in Amsterdam not far from the new townhall, where Rembrandt lived and painted for a number of years. A few years ago the house was thoroughly reconstructed on the inside to show how the house would have looked in Rembrandt's days. Adjoining (and linked to) the house is a modern building where work of Rembrandt is on display, mainly etchings and also a part of his collection of objects from all over the world.";
				locations.add(new Location("Rembrandt Museum", description, 52.36944, 4.9013, museams));
				
				description = "Hermitage Amsterdam is a branch museum of the Hermitage Museum of Saint Petersburg, Russia, located on the banks of the Amstel river in Amsterdam.";
				locations.add(new Location("Hermitage Amsterdam", description, 52.3653351, 4.9026104, museams));
				
				description = "The Heineken Experience, located in Amsterdam, is a historic brewery and corporate visitor center for the internationally distributed Dutch pilsner, Heineken beer.";
				locations.add(new Location("Heineken Experience", description, 52.357852, 4.891585, museams));
				
				description = "The Anne Frank House is a museum dedicated to Jewish wartime diarist Anne Frank, who hid from Nazi persecution with her family and four other people in hidden rooms at the rear of the building. As well as the preservation of the hiding place — known in Dutch as the Achterhuis — and an exhibition on the life and times of Anne Frank, the museum acts as an exhibition space to highlight all forms of persecution and discrimination.";
				locations.add(new Location("Anne Franks Huis", description, 52.3751728, 4.8839374, museams));				
			}
			LocationCategory parks = LocationCategory.find.where().eq("systemName", "parks").findUnique();
			if (parks != null)
			{
				description = "";
				locations.add(new Location("Oosterpark", description, 52.3589699, 4.9209303, parks));
				
				description = "";
				locations.add(new Location("Vondelpark", description, 52.3585283, 4.869938, parks));				
			}
			LocationCategory transportation = LocationCategory.find.where().eq("systemName", "transportation").findUnique();
			if (transportation != null)
			{
				description = "Amsterdam Centraal is the central railway station of Amsterdam. It is also one of the main railway hubs of the Netherlands and is used by 250,000 passengers a day, excluding transferring passengers. It is the starting point of Amsterdam Metro lines 51, 53 and 54.";
				locations.add(new Location("Amsterdam Central Station", description, 52.37919, 4.899431, transportation));
			}
			LocationCategory restaurantsPubs = LocationCategory.find.where().eq("systemName", "restaurants_pubs").findUnique();
			if (restaurantsPubs != null)
			{
				description = "Quote: “From 'Desa' philosophy, our chefs provide traditional and original Indonesian selected dishes with its fresh and best ingredients. We offer various menus, from rijsttafel torames, from wine to banana ice. You named it, we got it. With a perfect combination of delightful meal and comfortable atmosphere, you will surely hang smile on your face and coming back for more.”";
				locations.add(new Location("Desa", description, 52.35301635868728, 4.892102479934692, restaurantsPubs));
				
				description = "De Duvel is a nice place that fits perfectly in the ever lively Pijp, offering a real pub ambience, good service and good food at excellent prices. The kitchen staff makes sure the menu changes every month and there are always several specials. The 'coffee complete' is always a great finishing touch.";
				locations.add(new Location("De Duvel", description, 52.354823, 4.8937771, restaurantsPubs));
				
				description = "Van Kerkwijk stands for good quality food and drinks in an urban atmosphere. Characteristic, easy going, value for money. You feel at home between the regular customers who keep returning for the varied menu and the relaxed atmosphere.";
				locations.add(new Location("Van Kerkwijk", description, 52.37153813446085, 4.8936715722084045, restaurantsPubs));
			}
			LocationCategory cafes = LocationCategory.find.where().eq("systemName", "cafes").findUnique();
			if (cafes != null)
			{
				description = "The glowing umber, art deco–inspired interior with stained-glass windows and big tables is a crowd-puller.";
				locations.add(new Location("Café Thijssen", description, 52.380871424293275, 4.887328147888184, cafes));
				
				description = "The unique atmosphere of De Kroon (The crown) appeals to many. The decor, the colors, the lighting and certainly the cozy atmosphere creates many guests. In De Kroon you can enjoy lunch and dinner in the French Kitchen with sometimes a trip to Italy or some oriental. If you want to eat at De Kroon, then it is best to book";
				locations.add(new Location("De Kroon", description, 52.36626477249069, 4.897059202194214, cafes));
			}
			LocationCategory nightclubs = LocationCategory.find.where().eq("systemName", "nightclubs").findUnique();
			if (nightclubs != null)
			{
				description = "Escape Club is the largest nightspot in Amsterdam, located on the Rembrandtplein. Opened in 1986, the venue has gone through three major transformations and is now one of the most popular clubs in the Amsterdam party scene. The 2,000-person club features the newest technologies in sound, lighting, and visuals. Events at Escape Club include live DJ shows, exclusive parties, and weekly club nights, such as Reveal on Thursdays and Framebusters on Saturdays. Past concerts at Escape Club include performances by internationally renowned DJs Armin van Buuren, Tiësto, Fedde le Grand, Bob Sinclar, and Paul van Dyk.";
				locations.add(new Location("Escape", description, 52.366307354953136, 4.896324276924133, nightclubs));
				
				description = "Housed in a former church, Paradiso is a temple of contemporary music. The atmosphere and acoustics are internationally renowned. A popular venue for live music as well as special club nights.";
				locations.add(new Location("Paradiso", description, 52.3622774, 4.883945, nightclubs));				
			}
			Ebean.save(locations);
		}
	}
	
	public static void populateEventCategories()
	{
		if (EventCategory.find.getMaxRows() == 0)
		{
			List<EventCategory> eventCategories = new ArrayList<EventCategory>();
			eventCategories.add(new EventCategory("Music", "music"));
			eventCategories.add(new EventCategory("Art", "art"));
			eventCategories.add(new EventCategory("Nightlife", "singles_social"));
			Ebean.save(eventCategories);			
		}
	}
}
