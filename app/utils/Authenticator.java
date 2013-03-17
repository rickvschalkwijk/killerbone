package utils;

import org.joda.time.DateTime;
import helpers.Common;
import helpers.Cryptography;

public class Authenticator
{
	private final String AUTH_TOKEN_PREFIX = "_authtoken";
	private final String AUTH_TOKEN_REGEX = ".*@(\\[\\w{6}:([0-9a-zA-Z]+)]\\+?)*";
	
	public String generateAuthToken(long userId)
	{
		// Calculate expiration datetime
		DateTime expirationDateTime = DateTime.now().plusHours(24);
		long expires = expirationDateTime.getMillis();
		
		// Compose authtoken
		String authToken = String.format("%s@[userid:%d]+[expire:%d]", AUTH_TOKEN_PREFIX, userId, expires);
		
		// Encrypt authtoken
		return Cryptography.encrypt(authToken);
	}
	
	public boolean validateAuthToken(long userId, String encryptedAuthToken)
	{
		// Decrypt and validate authtoken
		String authToken = Cryptography.decrypt(encryptedAuthToken);
		if (Common.isNullOrEmpty(authToken) && authToken.matches(AUTH_TOKEN_REGEX))
		{
			return false;
		}
		
		// Remove authtoken prefix and parse key-value collection
		authToken = authToken.substring(AUTH_TOKEN_PREFIX.length()+1);
		authToken = authToken.replace("[", "").replace("]", "");
		String[] keyValuePairs = authToken.split("[+]");
		
		// Validate userid
		String valueUserId = keyValuePairs[0].substring(7);
		if (userId != 0 && (Common.isNullOrEmpty(valueUserId) || Long.valueOf(valueUserId) != userId))
		{
			return false;
		}
		
		// Validate expiration date
		String valueExpires = keyValuePairs[1].substring(7);
		if (Common.isNullOrEmpty(valueExpires) || Long.valueOf(valueExpires) < DateTime.now().getMillis())
		{
			return false;
		}
		
		return true;
	}
}
