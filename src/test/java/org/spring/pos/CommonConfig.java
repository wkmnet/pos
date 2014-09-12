/**
 * 
 */
package org.spring.pos;

import java.io.IOException;
import java.net.SocketException;

import org.apache.log4j.Logger;
import org.jpos.core.ConfigurationException;
import org.jpos.core.SimpleConfiguration;
import org.jpos.ext.channel.HEXChannel;
import org.jpos.ext.security.MyJCEHandler;
import org.jpos.ext.security.SoftSecurityModule;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.packager.GenericPackager;
import org.jpos.security.SimpleKeyFile;
import org.jpos.security.jceadapter.JCEHandlerException;
import org.jpos.util.DailyLogListener;
import org.junit.After;
import org.junit.Before;

/**
 * @author wkm
 *
 */
public abstract class CommonConfig {
	
	private static Logger logger = Logger.getLogger(CommonConfig.class);
	
	private org.jpos.util.Logger log = new org.jpos.util.Logger();
	
	protected ISOPackager packager = null;
	
	protected HEXChannel channel = null;
	
	protected SoftSecurityModule ssm = null;
	
	protected MyJCEHandler handler = null;
	
	protected SimpleKeyFile skf = null;
	
	private String host = "192.168.1.52";
//	private String host = "106.37.206.154";
	
//	private int port = 13107;
	private int port = 5002;
	
	@Before
	public void beforeInit(){
		try {
			logger.info("初始化:iso8583.xml");
			packager = new GenericPackager("config/posptu/posp-v1.xml");
		} catch (ISOException e){
			logger.error("初始化[iso8583.xml]错误:" + e.getMessage(),e);
		}
		
		try {
			logger.info("初始化HEXChannel...");
			channel = new HEXChannel(host,port,packager);
//			channel.setHeader(ISOUtil.str2bcd("6000060001603000000000",false));
			channel.setHeader(ISOUtil.hex2byte("6000060001603000000000"));
			channel.setOverrideHeader(true);
			DailyLogListener dailyLog = new DailyLogListener();
			dailyLog.setPrefix("log/test");
			log.addListener(dailyLog);
			
			channel.setLogger(log, "channel");
			channel.setTimeout(60*5*1000);
			logger.info("初始化Channel完成.");
			
			logger.info("config provider...");
			SimpleConfiguration config = new SimpleConfiguration();
			config.put("provider", "com.sun.crypto.provider.SunJCE");
			config.put("lmk", "D:/.lmk");
			config.put("key-file", "D:/.key");
			logger.info("config provider over!");
			
			ssm = new SoftSecurityModule();
			ssm.setLogger(log, "ssm");
			ssm.setConfiguration(config);
			handler = new MyJCEHandler("com.sun.crypto.provider.SunJCE");
			
			skf = new SimpleKeyFile();
			skf.setLogger(log, "skf");
			skf.setConfiguration(config);
			
			
		} catch (SocketException e){
			logger.error("连接服务器异常:" + e.getMessage(),e);
		} catch (ConfigurationException e){
			logger.error("配置信息异常:" + e.getMessage(),e);
		} catch (JCEHandlerException e){
			logger.error("创建JCEHandler异常:" + e.getMessage(),e);
		}
		
	}
	
	protected ISOMsg sendAndReceiveMessage(ISOMsg message){
		try {
			logger.info("send:" + System.getProperty("line.separator") + ISOUtil.hexdump(message.pack()));
			channel.connect();
			channel.send(message);
			return channel.receive();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("连接异常:" + e.getMessage(),e);
		} catch (ISOException e) {
			// TODO Auto-generated catch block
			logger.error("发送消息异常:" + e.getMessage(),e);
		}
		return null;
	}
	
	//通过TMK解密钥
	protected String decodeByTMK(){
		
		
		
		return  "";
	}
	
	@After
	public void after(){
		try {
			channel.disconnect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("关闭异常:" + e.getMessage(),e);
		}
	}
	
}
