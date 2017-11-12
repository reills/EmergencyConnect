package objects;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;


public class LoginHash  {
	/* From a given password and salt, uses the SHA-256 encoding system to return a hashed key
	 * This key is used to compare to the database key when users log in. 
	 */
	public static String generateHash(String passwordAndSalt) {
		
		 MessageDigest messageDigest = null;
			try {
				messageDigest = MessageDigest.getInstance("SHA-256");
			} catch (NoSuchAlgorithmException e) {
				System.out.println( " Cannot find algorithm ");
				e.printStackTrace();
			}
		
	    byte[] hash = messageDigest.digest(passwordAndSalt.getBytes(StandardCharsets.UTF_8));
	    
		//String hex = (new HexBinaryAdapter()).marshal(messageDigest.digest(passwordAndSalt.getBytes()) );
		String encodedSalt = new String(java.util.Base64.getMimeEncoder().encode(hash) );
		//System.out.println("encodedSalt: " + encodedSalt);
		return encodedSalt;
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
	
	
	public static void main(String args[]) {
		String hash = generateHash("tree");
		System.out.println("hash1: " + hash);
		hash = generateHash("tree");
		System.out.println("hash2: " + hash);
		hash = generateHash("tree");
		System.out.println("hash3: " + hash);
	}

}

