package helpers;

import java.io.File;

import play.Play;

public class Common
{
	/**
	 * Checks if string is null or empty
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
	 * Converts a null string to an empty string
	 * @param string
	 * @return
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
	 * Converts a relative path to an absolute path
	 * @param String
	 * @return String
	 */
	public static String resolvePath(String absolutePath)
	{
		if (!isNullOrEmpty(absolutePath) && absolutePath.startsWith("~"))
		{
			String rootPath = Play.application().path().getPath();
			String resolvedPath = absolutePath.replace("~", rootPath);
			
			// Replace seperator with operating specific seperator
			resolvedPath = resolvedPath.replace('/', File.separatorChar);
			resolvedPath = resolvedPath.replace('\\', File.separatorChar);
			
			return resolvedPath;
		}
		else
		{
			return absolutePath;
		}		
	}
}
