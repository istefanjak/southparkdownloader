package com.istef.southpark.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Crypto {
	public static String ALGORITHM = "AES";
	//public static String AES_CBC_NoPADDING = "AES/CBC/NoPadding";
	public static String AES_CBC_PADDING = "AES/CBC/PKCS5Padding";

	public static byte[] encrypt(final byte[] key, final byte[] IV, final byte[] message) throws Exception {
		return encryptDecrypt(Cipher.ENCRYPT_MODE, key, IV, message);
	}

	public static byte[] decrypt(final byte[] key, final byte[] IV, final byte[] message) throws Exception {
		return encryptDecrypt(Cipher.DECRYPT_MODE, key, IV, message);
	}

	private static byte[] encryptDecrypt(final int mode, final byte[] key, final byte[] IV, final byte[] message)
			throws Exception {
		final Cipher cipher = Cipher.getInstance(AES_CBC_PADDING);
		final SecretKeySpec keySpec = new SecretKeySpec(key, ALGORITHM);
		final IvParameterSpec ivSpec = new IvParameterSpec(IV);
		cipher.init(mode, keySpec, ivSpec);
		return cipher.doFinal(message);
	}

	public static byte[] getBytesFromHexString(String input) {
		byte[] val = new byte[input.length() / 2];
		for (int i = 0; i < val.length; i++) {
			int index = i * 2;
			int j = Integer.parseInt(input.substring(index, index + 2), 16);
			val[i] = (byte) j;
		}
		return val;
	}
}
