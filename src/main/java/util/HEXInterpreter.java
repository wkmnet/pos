/**
 * 
 */
package util;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.Interpreter;

/**
 * @author wkm
 *
 */
public class HEXInterpreter implements Interpreter {

	/** This HEXInterpreter sometimes adds a 0-nibble to the left. */
	public static final HEXInterpreter LEFT_PADDED = new HEXInterpreter(true, false);
	/** This HEXInterpreter sometimes adds a 0-nibble to the right. */
	public static final HEXInterpreter RIGHT_PADDED = new HEXInterpreter(false, false);
	/** This HEXInterpreter sometimes adds a F-nibble to the right. */
	public static final HEXInterpreter RIGHT_PADDED_F = new HEXInterpreter(false, true);
	/** This HEXInterpreter sometimes adds a F-nibble to the left. */
	public static final HEXInterpreter LEFT_PADDED_F = new HEXInterpreter(true, true);

	private boolean leftPadded;
	private boolean fPadded;

	private HEXInterpreter(boolean leftPadded, boolean fPadded) {
		this.leftPadded = leftPadded;
		this.fPadded = fPadded;
	}

	/**
	 * Converts the string data into a different interpretation. Standard
	 * interpretations are ASCII, EBCDIC, BCD and LITERAL.
	 *
	 * @param data The data to be interpreted.
	 * @return The interpreted data.
	 * @throws org.jpos.iso.ISOException on error
	 */
	public void interpret(String data, byte[] b, int offset) throws ISOException {
		if (data.length() % 2 != 0) {
			if (leftPadded) {
				if (fPadded) {
					data = "F" + data;
				} else {
					data = "0" + data;
				}
			} else {
				if (fPadded) {
					data += "F";
				} else {
					data += "0";
				}
			}
		}
		byte[] d = ISOUtil.hex2byte(data);
		System.arraycopy(d, 0, b, offset, d.length);
	}

	/**
	 * Converts the byte array into a String. This reverses the interpret
	 * method.
	 *
	 * @param rawData The interpreted data.
	 * @param offset  The index in rawData to start interpreting at.
	 * @param length  The number of data units to interpret.
	 * @return The uninterpreted data.
	 * @throws org.jpos.iso.ISOException on error
	 */
	public String uninterpret(byte[] rawData, int offset, int length) throws ISOException {
		int len = getPackedLength(length);
		byte[] ret = new byte[len];
		System.arraycopy(rawData, offset, ret, 0, len);
		return ISOUtil.hexString(ret).substring(0, length);
	}

	/**
	 * Returns the number of bytes required to interpret a String of length
	 * nDataUnits.
	 */
	public int getPackedLength(int nDataUnits) {
		return (nDataUnits + 1) / 2;
	}

}
