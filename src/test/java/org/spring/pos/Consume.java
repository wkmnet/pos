/**
 * 
 */
package org.spring.pos;

import org.apache.log4j.Logger;
import org.jpos.core.CardHolder;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;
import org.jpos.security.EncryptedPIN;
import org.jpos.security.SecureDESKey;
import org.junit.Assert;
import org.junit.Test;
import org.spring.pos.util.Constant;

/**
 * @author wkm
 *
 */
public class Consume extends CommonConfig {
	
	private static Logger logger = Logger.getLogger(Consume.class);
	
	private ISOMsg message = new ISOMsg();

	private void initSendMessage()throws Exception{
		try{
			message.setPackager(packager);
			message.setDirection(ISOMsg.OUTGOING);
			message.setHeader(channel.getHeader());
			/**
			def transType = TRANS_TYPE_LIST.find { it.transType == 'sale' }
			def req = newMsg(Constant.Card.PASS ? '021' : '012')
//			def req = newMsg('011')
			req.set 14, '1506'
			req.setMTI('0200')
			req.set 3 , '000000'
			req.set 4, '1'.padLeft(12, '0')
			req.set 11, nextTrace()
	        req.set 35, '84C251023CFD72104B178078DEB6007682B18DC5318701DF'
	        req.set 36, ''
			req.set 60, "22${currentBatch()}00000"

			assert checkFields(req, transType), '格式错误'

			def resp = sendAndReceive(req)
			assert resp.getString(39) == '00'
			printPaper(resp, 'sale', '消费')
			**/
			message.setMTI("0200");
			message.set(2,Constant.CARDNO);
			message.set(3,"000000");
			message.set(4,String.format("0:D12", 1));
			message.set(11,"001033");
			message.set(22,"021");
			message.set(35,Constant.TRACK2);
			message.set(36,Constant.TRACK3);
			message.set(41,"30012378");
			message.set(42,"Z21000000015872");
			message.set(49,"156");
			message.set(52,getCardBin(Constant.CARDPIN));
			message.set(60,"22000000001003");
//			message.set(63,"001");
		} catch (ISOException e){
			logger.error("init message error:" + e.getMessage(),e);
		}
	}
	
	private byte[] getCardBin(String pin)throws Exception{
		CardHolder cardHolder = new CardHolder(message);
		if (message.hasField(2)) {
			cardHolder.setPAN(message.getString(2));
		}
		EncryptedPIN ePin = ssm.encryptPIN(pin, cardHolder.getPAN());
		SecureDESKey zpk = (SecureDESKey)skf.getKey("internal.zpk");
		ePin = ssm.exportPIN(ePin, zpk, ePin.getPINBlockFormat());
		return ePin.getPINBlock();
	}
	
	@Test
	public void testCheckIn(){
		try {
			initSendMessage();
			ISOMsg msg = sendAndReceiveMessage(message);
			logger.info("receive:" + System.getProperty("line.separator") + ISOUtil.hexdump(msg.pack()));
			String respCode = msg.getString(39);
			Assert.assertEquals("00", respCode);
			
			logger.info("62 Data:" + ISOUtil.hexdump(msg.pack()));
		} catch (ISOException e) {
			// TODO Auto-generated catch block
			logger.error("返回信息异常:" + e.getMessage(),e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("返回信息异常:" + e.getMessage(),e);
		}
	}
}
