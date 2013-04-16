package helpers;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import play.Logger;

public class Passwords 
{
	private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA1";

	private static final int SALT_BYTES = 24;
	private static final int HASH_BYTES = 24;
	private static final int PBKDF2_ITERATIONS = 1000;

	private static final int ITERATION_INDEX = 0;
	private static final int SALT_INDEX = 1;
	private static final int PBKDF2_INDEX = 2;
	
	private static final String ALPHA_CAPS_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String ALPHA_CHARS = "abcdefghijklmnopqrstuvwxyz";
	private static final String NUMERIC_CHARS = "0123456789";
	private static final String SPECIAL_CHARS = "!@#$%^&*_=+-/";
	
	//-----------------------------------------------------------------------//
	
	/**
	 * Creates a hash from password
	 * @param String
	 * @return String
	 */
	public static String createHash(String password) 
	{
		try
		{
			SecureRandom random = new SecureRandom();
			byte[] salt = new byte[SALT_BYTES];
			random.nextBytes(salt);

			byte[] hash = pbkdf2(password.toCharArray(), salt, PBKDF2_ITERATIONS, HASH_BYTES);
			return PBKDF2_ITERATIONS + ":" + toHex(salt) + ":" + toHex(hash);
		}
		catch (NoSuchAlgorithmException e)
		{
			Logger.error("An error occured while creating hash: " + e.getMessage());
		}
		catch (InvalidKeySpecException e)
		{
			Logger.error("An error occured while creating hash: " + e.getMessage());
		}
		return null;
	}

	/**
	 * Validates a password based on the password hash
	 * @param String
	 * @param String
	 * @return boolean
	 */
	public static boolean validatePassword(String password, String goodHash)
	{
		try
		{
			String[] params = goodHash.split(":");
			int iterations = Integer.parseInt(params[ITERATION_INDEX]);
			byte[] salt = fromHex(params[SALT_INDEX]);
			byte[] hash = fromHex(params[PBKDF2_INDEX]);
			byte[] testHash = pbkdf2(password.toCharArray(), salt, iterations, hash.length);
			return slowEquals(hash, testHash);
		}
		catch (NoSuchAlgorithmException e)
		{
			Logger.error("An error occured while validating password: " + e.getMessage());
		}
		catch (InvalidKeySpecException e)
		{
			Logger.error("An error occured while validating password: " + e.getMessage());
		}
		return false;
	}

	private static boolean slowEquals(byte[] a, byte[] b) 
	{
		int diff = a.length ^ b.length;
		for (int i = 0; i < a.length && i < b.length; i++)
			diff |= a[i] ^ b[i];
		return diff == 0;
	}

	private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int bytes) throws NoSuchAlgorithmException, InvalidKeySpecException 
	{
		PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, bytes * 8);
		SecretKeyFactory skf = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
		return skf.generateSecret(spec).getEncoded();
	}

	/**
	 * Converts hex to binary.
	 * @param String
	 * @return byte[]
	 */
	private static byte[] fromHex(String hex) 
	{
		byte[] binary = new byte[hex.length() / 2];
		for (int i = 0; i < binary.length; i++) {
			binary[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
		}
		return binary;
	}

	/**
	 * Converts binary to hex.
	 * @param byte[]
	 * @return String
	 */
	private static String toHex(byte[] array) 
	{
		BigInteger bi = new BigInteger(1, array);
		String hex = bi.toString(16);
		int paddingLength = (array.length * 2) - hex.length();
		if (paddingLength > 0)
		{
			return String.format("%0" + paddingLength + "d", 0) + hex;
		}
		else
		{
			return hex;
		}
	}
	
	//-----------------------------------------------------------------------//
	
	/**
	 * Generate a random password
	 * @param int
	 * @param int
	 * @param int
	 * @param int
	 * @param int
	 * @return String
	 */
	public static String generatePassword(int minLength, int maxLength, int numOfCAPSAlpha, int numOfDigits, int numOfSplChars) 
	{
		if (minLength > maxLength)
		{
			throw new IllegalArgumentException("Min. Length > Max. Length!");
		}
		if ((numOfCAPSAlpha + numOfDigits + numOfSplChars) > minLength)
		{
			throw new IllegalArgumentException("Min. Length should be atleast sum of (CAPS, DIGITS, SPL CHARS) Length!");
		}
		
		Random rnd = new Random();
		int len = rnd.nextInt(maxLength - minLength + 1) + minLength;
		char[] pswd = new char[len];
		int index = 0;
		for (int i = 0; i < numOfCAPSAlpha; i++) {
			index = getNextIndex(rnd, len, pswd);
			pswd[index] = ALPHA_CAPS_CHARS.charAt(rnd.nextInt(ALPHA_CAPS_CHARS.length()));
		}
		for (int i = 0; i < numOfDigits; i++) {
			index = getNextIndex(rnd, len, pswd);
			pswd[index] = NUMERIC_CHARS.charAt(rnd.nextInt(NUMERIC_CHARS.length()));
		}
		for (int i = 0; i < numOfSplChars; i++) {
			index = getNextIndex(rnd, len, pswd);
			pswd[index] = SPECIAL_CHARS.charAt(rnd.nextInt(SPECIAL_CHARS.length()));
		}
		for (int i = 0; i < len; i++) {
			if (pswd[i] == 0) {
				pswd[i] = ALPHA_CHARS.charAt(rnd.nextInt(ALPHA_CHARS.length()));
			}
		}
		return String.valueOf(pswd);
	}

	private static int getNextIndex(Random rnd, int len, char[] pswd) {
		int index = rnd.nextInt(len);
		while (pswd[index = rnd.nextInt(len)] != 0)
		{
			// Do nothing, just let it work..
		}
		return index;
	}
}
