package org.ltd3000.jasperprinter.utils;

import java.nio.charset.Charset;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * @author xushanshan 
 * @email 1539601747@qq.com
 *
 */
public class DesUtil {
	private static final String password = "12345688";//长度固定
	private static final Charset CHARSET = Charset.forName("utf-8");

	/**
	 * 加密
	 */
	public static byte[] encrypt(byte[] data, String password) {
		try {
			SecureRandom random = new SecureRandom();
			DESKeySpec desKey = new DESKeySpec(password.getBytes(CHARSET));
			// 创建一个密匙工厂，然后用它把DESKeySpec转换成
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey securekey = keyFactory.generateSecret(desKey);
			// Cipher对象实际完成加密操作
			Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
			// 用密匙初始化Cipher对象,ENCRYPT_MODE用于将 Cipher 初始化为加密模式的常量
			cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
			// 按单部分操作加密或解密数据，或者结束一个多部分操作
			return cipher.doFinal(data); 
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * byte[]转Hex字符串
	 */
	public static String parseByte2HexStr(byte buf[]) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buf.length; i++) {
			String hex = Integer.toHexString(buf[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append(hex.toUpperCase());
		}
		return sb.toString();
	}

	/**
	 * 解密
	 */
	public static byte[] decrypt(byte[] data, String password) throws Exception {
		// DES算法要求有一个可信任的随机数源
		SecureRandom random = new SecureRandom();
		// 创建一个DESKeySpec对象
		DESKeySpec desKey = new DESKeySpec(password.getBytes(CHARSET));
		// 创建一个密匙工厂
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");// 返回实现指定转换的 Cipher 对象
		// 将DESKeySpec对象转换成SecretKey对象
		SecretKey securekey = keyFactory.generateSecret(desKey);
		// Cipher对象实际完成解密操作
		Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
		// 用密匙初始化Cipher对象
		cipher.init(Cipher.DECRYPT_MODE, securekey, random);
		// 真正开始解密操作
		return cipher.doFinal(data);
	}

	/**
	 * 加密字符串
	 */
	public static String encrypt(String srcStr, String sKey) {
		byte[] src = srcStr.getBytes(CHARSET);
		byte[] buf = encrypt(src, sKey);

		return parseByte2HexStr(buf);
	}

	/**
	 * 解密Hex字符串
	 */
	public static String decrypt(String hexStr, String sKey) throws Exception {
		byte[] src = parseHexStr2Byte(hexStr);
		byte[] buf = decrypt(src, sKey);
		return new String(buf, CHARSET);
	}

	/**
	 * 16进制字符串转byte数组
	 */
	public static byte[] parseHexStr2Byte(String hexStr) {
		if (hexStr.length() < 1)
			return null;
		byte[] result = new byte[hexStr.length() / 2];
		for (int i = 0; i < hexStr.length() / 2; i++) {
			int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
			int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
			result[i] = (byte) (high * 16 + low);
		}
		return result;
	}

}
