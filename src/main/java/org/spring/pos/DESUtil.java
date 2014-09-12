/**
 * 
 */
package org.spring.pos;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.apache.log4j.Logger;
import org.jpos.core.SimpleConfiguration;
import org.jpos.ext.security.MyJCEHandler;
import org.jpos.ext.security.SoftSecurityModule;
import org.jpos.iso.ISOUtil;
import org.jpos.security.SMAdapter;
import org.jpos.security.SecureDESKey;
import org.jpos.security.SimpleKeyFile;
import org.jpos.util.DailyLogListener;

/**
 * @author wkm
 * 
 */
public class DESUtil {
	
	private static Logger logger = Logger.getLogger(DESUtil.class);
	
	private org.jpos.util.Logger log = new org.jpos.util.Logger();
	
	private static String PROVIDER = "com.sun.crypto.provider.SunJCE";
	
	protected SoftSecurityModule ssm = null;
	
	protected MyJCEHandler handler = null;
	
	protected SimpleKeyFile skf = null;
	
	public void test()throws Exception{
		logger.info("config provider...");
		SimpleConfiguration config = new SimpleConfiguration();
		config.put("provider", PROVIDER);
		config.put("lmk", "D:/.lmk");
		config.put("key-file", "D:/.key");
		logger.info("config provider over!");
		
		DailyLogListener dailyLog = new DailyLogListener();
		dailyLog.setPrefix("log/test");
		log.addListener(dailyLog);
		
		ssm = new SoftSecurityModule();
		ssm.setLogger(log, "ssm");
		ssm.setConfiguration(config);
		
		handler = new MyJCEHandler(PROVIDER);
		
		skf = new SimpleKeyFile();
		skf.setLogger(log, "skf");
		skf.setConfiguration(config);
		
		String src = "9999999999999999";
		SecureDESKey key = ssm.encryptToLMK(SMAdapter.LENGTH_DES, SMAdapter.TYPE_TMK, handler.formDESKey(SMAdapter.LENGTH_DES, ISOUtil.hex2byte(src)));
		logger.info("加密:" + ISOUtil.hexString(key.getKeyBytes()));
		logger.info("校验值:" + ISOUtil.hexString(key.getKeyCheckValue()));
	}

	// 测试
	public static void main(String args[]) throws Exception{
		// 待加密内容
		String str = "1234";
		// 密码，长度要是8的倍数
		String password = "9999999999999999";

		byte[] result = DESUtil.encrypt(str.getBytes(), password);
		System.out.println("加密后：" + ISOUtil.byte2hex(result).toUpperCase());

		// 直接将如上内容解密
		try {
			byte[] decryResult = DESUtil.decrypt(result, password);
			System.out.println("解密后：" + new String(decryResult));
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		
		new DESUtil().test();
	}

	/**
	 * 加密
	 * 
	 * @param datasource
	 *            byte[]
	 * @param password
	 *            String
	 * @return byte[]
	 */
	public static byte[] encrypt(byte[] datasource, String password) {
		try {
			SecureRandom random = new SecureRandom();
			DESKeySpec desKey = new DESKeySpec(password.getBytes());
			// 创建一个密匙工厂，然后用它把DESKeySpec转换成
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey securekey = keyFactory.generateSecret(desKey);
			// Cipher对象实际完成加密操作
			Cipher cipher = Cipher.getInstance("DES");
			// 用密匙初始化Cipher对象
			cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
			// 现在，获取数据并加密
			// 正式执行加密操作
			return cipher.doFinal(datasource);
		} catch (Exception e) {
			System.out.println("Exception:" + e.getMessage());
		}
		return null;
	}

	/**
	 * 解密
	 * 
	 * @param src
	 *            byte[]
	 * @param password
	 *            String
	 * @return byte[]
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] src, String password) throws Exception {
		// DES算法要求有一个可信任的随机数源
		SecureRandom random = new SecureRandom();
		// 创建一个DESKeySpec对象
		DESKeySpec desKey = new DESKeySpec(password.getBytes());
		// 创建一个密匙工厂
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		// 将DESKeySpec对象转换成SecretKey对象
		SecretKey securekey = keyFactory.generateSecret(desKey);
		// Cipher对象实际完成解密操作
		Cipher cipher = Cipher.getInstance("DES");
		// 用密匙初始化Cipher对象
		cipher.init(Cipher.DECRYPT_MODE, securekey, random);
		// 真正开始解密操作
		return cipher.doFinal(src);
	}
}
