package com.rabbitcomapny.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Hash {
	public String algo;
	public String hash;
	public String salt;

	public Hash(String algo, String password) {
		try {
			MessageDigest digest = MessageDigest.getInstance(algo);
			this.algo = algo;
			byte[] salt = generateSalt();
			digest.update(salt);
			this.salt = bytesToHex(salt);
			this.hash = bytesToHex(digest.digest(password.getBytes(StandardCharsets.UTF_8)));
		} catch (NoSuchAlgorithmException e) {
			this.algo = null;
			this.hash = password;
			this.salt = null;
		}
	}

	public Hash(String algo, String password, String salt, boolean hashed) {

		if (hashed) {
			this.algo = algo;
			this.hash = password;
			this.salt = salt;
			return;
		}

		try {
			MessageDigest digest = MessageDigest.getInstance(algo);
			this.algo = algo;
			digest.update(hexToBytes(salt));
			this.salt = salt;
			this.hash = bytesToHex(digest.digest(password.getBytes(StandardCharsets.UTF_8)));
		} catch (NoSuchAlgorithmException e) {
			this.algo = null;
			this.hash = password;
			this.salt = null;
		}
	}

	private static byte[] generateSalt() {
		SecureRandom random = new SecureRandom();
		byte[] salt = new byte[32];
		random.nextBytes(salt);
		return salt;
	}

	private static String bytesToHex(byte[] hash) {
		StringBuilder hexString = new StringBuilder();
		for (byte b : hash) {
			String hex = Integer.toHexString(0xff & b);
			if (hex.length() == 1) hexString.append('0');
			hexString.append(hex);
		}
		return hexString.toString();
	}

	private static byte[] hexToBytes(String hex) {
		int length = hex.length();
		byte[] bytes = new byte[length / 2];
		for (int i = 0; i < length; i += 2) {
			bytes[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4) + Character.digit(hex.charAt(i + 1), 16));
		}
		return bytes;
	}
}
