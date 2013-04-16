package models;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.apache.commons.lang3.StringEscapeUtils;
import org.joda.time.DateTime;

import com.avaje.ebean.validation.Length;
import com.evdb.javaapi.data.Image;

import play.data.format.Formats;
import play.db.ebean.Model;

@Entity
public class Event extends Model
{
	private static final long serialVersionUID = 2388689047511553876L;
	
	@Id
	@GeneratedValue
	public long eventId;
	
	@Column(unique = true)
	public String eventfulId;
	
	@Length(max = 255)
	public String title;

	@Column(columnDefinition = "TEXT")
	public String description;
	
	@Formats.DateTime(pattern = "dd-MM-yyyy HH:mm")
	public DateTime startDate;
	
	@Formats.DateTime(pattern = "dd-MM-yyyy HH:mm")
	public DateTime endDate;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
	public EventCategory category;
	
	public double latitude;
	public double longitude;	
	
	public boolean isFree;
	public String price;
	
	@Length(max = 255)
	public String thumbUrl;
	@Length(max = 255)
	public String imageUrl;
	
	public long creationTimestamp;
	public long modifiedTimestamp;
	
	public Event()
	{
	}
	
	public Event(String eventfulId, String title, String description, double latitude, double longitude, DateTime startDate, DateTime endDate, String price, boolean isFree)
	{
		this();
		
		this.eventfulId = eventfulId;
		this.title = title;
		this.description = description;
		this.latitude = latitude;
		this.longitude = longitude;
		this.startDate = startDate;
		this.endDate = endDate;
		this.price = price;
		this.isFree = isFree;
	}
	
	public Event(com.evdb.javaapi.data.Event event, EventCategory category)
	{
		this();
		
		eventfulId = event.getSeid();
		title = StringEscapeUtils.unescapeHtml4(event.getTitle().trim());
		description = StringEscapeUtils.unescapeHtml4(event.getDescription().trim());
		latitude = event.getVenueLatitude();
		longitude = event.getVenueLongitude();
		startDate = new DateTime(event.getStartTime());	
		endDate = (event.getStopTime() != null ? new DateTime(event.getStopTime()) : startDate);
		price = event.getPrice();
		isFree = event.isFree();
		
		Image firstEventImage = event.getImages().get(0);
		if (firstEventImage != null)
		{
			thumbUrl = (firstEventImage.getThumb() != null ? firstEventImage.getThumb().getUrl() : null);
			imageUrl = firstEventImage.getUrl();
		}
		this.category = category;
	}
	
	//-----------------------------------------------------------------------//
	
	public static Finder<Long, Event> find = new Finder<Long, Event>(
			Long.class, Event.class
	);	
}