/**
 * 
 */
package org.spring.pos;

import org.apache.log4j.Logger;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author wkm
 *
 */

public class CheckIn extends CommonConfig{
	
	private static Logger logger = Logger.getLogger(CheckIn.class);
	
	private ISOMsg message = new ISOMsg();

	private void initSendMessage(){
		try{
			message.setPackager(packager);
			message.setDirection(ISOMsg.OUTGOING);
			message.setHeader(channel.getHeader());
			message.setMTI("0800");
			message.set(11,"001033");
			message.set(25,"00");
			message.set(41,"30012378");
			message.set(42,"Z21000000015872");
			message.set(49,"156");
			message.set(60,"00000000001003");
			message.set(63,"001");
		} catch (ISOException e){
			logger.error("init message error:" + e.getMessage(),e);
		}
	}
	
	
	@Test
	public void testCheckIn(){
		initSendMessage();
		ISOMsg msg = sendAndReceiveMessage(message);
		try {
			logger.info("receive:" + System.getProperty("line.separator") + ISOUtil.hexdump(msg.pack()));
			String respCode = msg.getString(39);
			Assert.assertEquals("00", respCode);
			
			String field62 = msg.getString(62);
			logger.info("62 Data:" + field62);
		} catch (ISOException e) {
			// TODO Auto-generated catch block
			logger.error("返回信息异常:" + e.getMessage(),e);
		}
	}
}
