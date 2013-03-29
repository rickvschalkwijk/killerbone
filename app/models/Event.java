package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

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
	public long EventId;
	
	@Length(max = 255)
	public String title;
	
	public double latitude;
	public double longitude;
	
	@Formats.DateTime(pattern = "dd-MM-yyyy HH:mm")
	public DateTime startDate;
	
	@Formats.DateTime(pattern = "dd-MM-yyyy HH:mm")
	public DateTime endDate;
	
	public boolean isFree;
	public double price;
}