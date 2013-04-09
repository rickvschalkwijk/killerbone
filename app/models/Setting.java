package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.avaje.ebean.validation.Length;

import play.db.ebean.Model;

@Entity
public class Setting extends Model
{
	private static final long serialVersionUID = 2489593664032287904L;

	@Id
	@GeneratedValue
	public long settingId;
	
	@Column(unique = true)
	@Length(max = 50)
	public String settingKey;
	
	@Length(max = 255)
	public String settingValue;

	public Setting()
	{
	}
	
	public Setting(String key, String value)
	{
		settingKey = key;
		settingValue = value;
	}
	
	//-----------------------------------------------------------------------//
	
	public static Finder<Long, Setting> find = new Finder<Long, Setting>(
			Long.class, Setting.class
	);
}
