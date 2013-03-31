package helpers;

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
}
