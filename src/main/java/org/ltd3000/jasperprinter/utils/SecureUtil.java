package org.ltd3000.jasperprinter.utils;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Calendar;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;

/**
 * @author deski
 *
 */
public class SecureUtil {
	/**
	 * 从文件获取公钥
	 */
	private static String keyStorePath = "./jasper.public";
	private static final Logger logger = Logger.getLogger("SecureUtil");

	/**
	 * @param filename
	 * @function 获取公钥
	 * @return RSAPublicKey
	 * @throws Exception
	 */
	private static RSAPublicKey getPublicKey(String filename) throws Exception {

		File f = new File(filename);
		FileInputStream fis = new FileInputStream(f);
		DataInputStream dis = new DataInputStream(fis);
		byte[] keyBytes = new byte[(int) f.length()];
		dis.readFully(keyBytes);
		dis.close();
		X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return (RSAPublicKey) kf.generatePublic(spec);
	}

	/**
	 * @param pubKeyInByte
	 * @param data
	 * @function 公钥解密
	 * @return byte
	 */
	public static byte[] decryptByRSA1(byte[] pubKeyInByte, byte[] data) {
		try {
			KeyFactory mykeyFactory = KeyFactory.getInstance("RSA");
			X509EncodedKeySpec pub_spec = new X509EncodedKeySpec(pubKeyInByte);
			PublicKey pubKey = mykeyFactory.generatePublic(pub_spec);
			Cipher cipher = Cipher.getInstance(mykeyFactory.getAlgorithm());
			cipher.init(Cipher.DECRYPT_MODE, pubKey);
			return cipher.doFinal(data);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * @param encodeString : 为私钥加密后的byte[] Base64处理后字符串
	 * @function 解密字符串
	 * @return
	 */
	public static String decodeString(String encodeString) {
		try {
			RSAPublicKey pKey = getPublicKey(keyStorePath);
			byte[] stringByte = decryptByRSA1(pKey.getEncoded(), Base64.decodeBase64(encodeString));
			return new String(stringByte, "UTF-8");
		} catch (Exception e) {

			e.printStackTrace();
		}

		return null;
	}

	public static boolean checkAuth() {
		String authkey = ConfigUtil.getProperty("authkey");
		JSONObject keyJson = (JSONObject) JSONObject.parse(authkey);
		String encodeDesPass = keyJson.getString("p");
		String encodeDesData = keyJson.getString("d");
		String decodeDesPass = decodeString(encodeDesPass);
		try {
			String desDecodeString = DesUtil.decrypt(encodeDesData, decodeDesPass);
			JSONObject dataJson = (JSONObject) JSONObject.parse(desDecodeString);
			JSONObject lJson = (JSONObject) dataJson.get("l");
			String slaveDate = lJson.getString("s");
			Calendar cNow = Calendar.getInstance();
			Calendar cSlave = Calendar.getInstance();
			cSlave.set(Calendar.YEAR, Integer.parseInt(slaveDate.substring(0, 4)));
			cSlave.set(Calendar.MONTH, Integer.parseInt(slaveDate.substring(5, 7)) - 1);
			cSlave.set(Calendar.DATE, Integer.parseInt(slaveDate.substring(8, 10)));
			if (cNow.before(cSlave)) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean lockInstance() {
		File lockFile = new File("./jasper.lck");
		RandomAccessFile randomAccessFile;
		try {
			if (!lockFile.exists()) {
				lockFile.createNewFile();
			}
			randomAccessFile = new RandomAccessFile(lockFile, "rw");
			FileChannel channel = randomAccessFile.getChannel();
			try {
				FileLock lock = channel.tryLock();
				if (lock != null && lock.isValid()) {
					return true;
				} else {
					logger.info("已有服务在运行");
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static void main(String[] args) {
		if (checkAuth()) {
			System.out.print("验证成功");
		} else {
			System.out.print("验证失败");
		}

	}
}
