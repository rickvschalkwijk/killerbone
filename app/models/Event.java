package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.apache.commons.lang3.StringEscapeUtils;
import org.joda.time.DateTime;

import com.avaje.ebean.validation.Length;

import play.data.format.Formats;
import play.db.ebean.Model;

@Entity
public class Event extends Model
{
	private static final long serialVersionUID = 2388689047511553876L;
	
	@Id
	@GeneratedValue
	public long eventId;
	
	@Length(max = 255)
	public String title;

	@Column(columnDefinition = "TEXT")
	public String description;
	
	@Formats.DateTime(pattern = "dd-MM-yyyy HH:mm")
	public DateTime startDate;
	
	@Formats.DateTime(pattern = "dd-MM-yyyy HH:mm")
	public DateTime endDate;
	
	@ManyToOne(fetch = FetchType.LAZY)
	public EventCategory category;
	
	public double latitude;
	public double longitude;	
	
	public boolean isFree;
	public double price;
	
	public long creationTimestamp;
	
	public Event()
	{
		// Default values
	}
	
	public Event(String title, String description, double latitude, double longitude, DateTime startDate, DateTime endDate, double price)
	{
		this();
		
		this.title = title;
		this.description = description;
		this.latitude = latitude;
		this.longitude = longitude;
		this.startDate = startDate;
		this.endDate = endDate;
		this.price = price;
		isFree = (price == 0);
	}
	
	public Event(com.evdb.javaapi.data.Event event, EventCategory category)
	{
		this();
		
		title = StringEscapeUtils.unescapeHtml4(event.getTitle());
		description = StringEscapeUtils.unescapeHtml4((event.getDescription()));
		latitude = event.getVenueLatitude();
		longitude = event.getVenueLongitude();
		startDate = new DateTime(event.getStartTime());	
		endDate = (event.getStopTime() != null ? new DateTime(event.getStopTime()) : null);
		price = 0;
		isFree = (price == 0);
		
		this.category = category;
	}
	
	//-----------------------------------------------------------------------//
	
	public static Finder<Long, Event> find = new Finder<Long, Event>(
			Long.class, Event.class
	);	
}