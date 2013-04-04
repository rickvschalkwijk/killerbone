package helpers;

import models.Setting;

public class Settings
{
	/**
	 * Returns a setting based on the key
	 * @param string
	 * @return string
	 */
	public static String get(String key)
	{
		Setting setting = Setting.find.where().eq("settingKey", key).findUnique();
		if (setting != null)
		{
			return setting.settingValue;
		}
		return null;
	}
	
	/**
	 * Creates or updates a setting based on key/value pair
	 * @param key
	 * @param value
	 */
	public static void set(String key, String value)
	{
		Setting setting = Setting.find.where().eq("settingKey", key).findUnique();
		if (setting != null)
		{
			setting.settingValue = value;
		}
		else
		{
			setting = new Setting(key, value);
		}
		setting.save();
	}
}
