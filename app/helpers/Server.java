package helpers;

import java.util.Map;

import play.mvc.Http;
import play.mvc.Http.Request;

public class Server
{
	/**
	 * Get a HTTP header value.
	 * @param String
	 * @return String
	 */
	public static String getHeaderValue(String header)
	{
		Request currentRequest = Http.Context.current().request();
		Map<String, String[]> headers = currentRequest.headers();
		
		if (headers.containsKey(header))
		{
			return headers.get(header)[0];
		}
		return null;
	}
}
