package objects;

import objects.User;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;


public class LoginHash  {
	private static final long serialVersionUID = 1L;
	
	/* From a given password and salt, uses the SHA-256 encoding system to return a hashed key
	 * This key is used to compare to the database key when users log in. 
	 */
	public static String generateHash(String password) {
		
		String encodedSalt = getSalt();
		String passwordAndSalt = encodedSalt + password;
		
		 MessageDigest messageDigest = null;
			try {
				messageDigest = MessageDigest.getInstance("SHA-256");
			} catch (NoSuchAlgorithmException e) {
				System.out.println( " Cannot find algorithm ");
				e.printStackTrace();
			}
		
		String hex = (new HexBinaryAdapter()).marshal(messageDigest.digest(passwordAndSalt.getBytes()));
		return hex;
	}
	
	/*
	 * generates a "salt"; a 16 byte array of random numbers
	 * this salt is unique for each new user
	 * lastly, the method calls generateHash to combine the salt and hash for the final encrypted string
	 */
	public static String getSalt()  {
        SecureRandom random = null;
        
		try {
			random = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			System.out.println( " Cannot find algorithm ");
		}
		
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        
        String encodedSalt = new String(java.util.Base64.getMimeEncoder().encode(salt), StandardCharsets.UTF_8);
        	
        return encodedSalt;
    }

}

