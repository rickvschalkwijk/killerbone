package helpers;

public class Validator
{
	final private static String ACTIVATION_CODE_REGEX = "activate:[1-9]+[0-9]*";
	final private static String AUTH_TOKEN_REGEX = ".*@(\\[\\w{5,6}:([0-9a-zA-Z]+)]\\+?)*";
	final private static String NUMERIC_REGEX = "[0-9]{1,10}";
	final private static String NAME_REGEX = ".{0,50}";
	final private static String EMAIL_REGEX = ".+\\@.+\\..+";
	final private static String PASSWORD_REGEX = "^[a-zA-Z]\\w{3,14}$";
	final private static String LAT_OR_LNG_REGEX = "^-?[0-9]{0,2}\\.?[0-9]{0,20}$";
	
	//-----------------------------------------------------------------------//
	
	/**
	 * Validates that a string contains a valid activation code.
	 * @param String
	 * @return boolean
	 */
	public static boolean validateActivationCode(String activationCode)
	{
		return (!Common.isNullOrEmpty(activationCode) && activationCode.matches(ACTIVATION_CODE_REGEX));
	}
	
	/**
	 * Validates that a string contains a valid authtoken.
	 * @param String
	 * @return boolean
	 */
	public static boolean validateAuthToken(String authToken)
	{
		return (!Common.isNullOrEmpty(authToken) && authToken.matches(AUTH_TOKEN_REGEX));
	}
	
	/**
	 * Validates that a string contains numeric data.
	 * @param String
	 * @return boolean
	 */
	public static boolean validateNumeric(String numeric)
	{
		return (!Common.isNullOrEmpty(numeric) && numeric.matches(NUMERIC_REGEX));
	}
	
	/**
	 * Validates that a string contains name compatible data.
	 * @param String
	 * @return boolean
	 */
	public static boolean validateName(String name)
	{
		return (!Common.isNullOrEmpty(name) && name.matches(NAME_REGEX));
	}
	
	/**
	 * Validates that a string contains data in a email format.
	 * @param String
	 * @return boolean
	 */
	public static boolean validateEmail(String email)
	{
		return (!Common.isNullOrEmpty(email) && email.matches(EMAIL_REGEX));
	}
	
	/**
	 * Validates that a string contains password compatible data.
	 * @param String
	 * @return boolean
	 */
	public static boolean validatePassword(String password)
	{
		return (!Common.isNullOrEmpty(password) && password.matches(PASSWORD_REGEX));
	}
	
	/**
	 * Validates that a string contains a valid latitude or longitude.
	 * @param String
	 * @return boolean
	 */
	public static boolean validateLatitudeOrLongitude(String latOrlng)
	{
		return (!Common.isNullOrEmpty(latOrlng) && latOrlng.matches(LAT_OR_LNG_REGEX));
	}
}
