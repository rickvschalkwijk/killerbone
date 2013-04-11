package helpers;

import org.joda.time.DateTime;

public class Common
{
	/**
	 * Checks if string is null or empty.
	 * @param String
	 * @return boolean
	 */
	public static boolean isNullOrEmpty(String string)
	{
		if (string == null || string.trim().isEmpty())
		{
			return true;
		}
		return false;
	}
	
	/**
	 * Converts a null string to an empty string.
	 * @param String
	 * @return String
	 */
	public static String ensureNotNull(String string)
	{
		if (string == null)
		{
			return "";
		}
		return string;
	}
	
	/**
	 * Creates a system timestamp
	 * @return long
	 */
	public static long getTimestamp()
	{
		return System.currentTimeMillis();
	}
	
	public static String timestampToDateTimeString(long timestamp)
	{
		DateTime dateTime = new DateTime(timestamp);
		return dateTime.toString("hh:mm a, dd MMMM yyy");
	}
}
