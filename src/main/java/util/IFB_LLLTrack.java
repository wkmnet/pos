/**
 * 
 */
package util;

import org.jpos.iso.BCDInterpreter;
import org.jpos.iso.BcdPrefixer;
import org.jpos.iso.ISOStringFieldPackager;
import org.jpos.iso.NullPadder;

/**
 * @author wkm
 *
 */
public class IFB_LLLTrack extends ISOStringFieldPackager{

	public IFB_LLLTrack() {
		super(NullPadder.INSTANCE, HEXInterpreter.RIGHT_PADDED, BcdPrefixer.LLL);
	}
	/**
	 * @param len - field len
	 * @param description symbolic descrption
	 */
	public IFB_LLLTrack(int len, String description, boolean isLeftPadded) {
		super(len, description, NullPadder.INSTANCE,
				isLeftPadded ? HEXInterpreter.LEFT_PADDED : BCDInterpreter.RIGHT_PADDED,
				BcdPrefixer.LLL);
		checkLength(len, 999);
	}

	public IFB_LLLTrack(int len, String description, boolean isLeftPadded, boolean fPadded) {
		super(len, description, NullPadder.INSTANCE,
				isLeftPadded ? HEXInterpreter.LEFT_PADDED :
						(fPadded ? HEXInterpreter.RIGHT_PADDED_F : HEXInterpreter.RIGHT_PADDED),
				BcdPrefixer.LLL);
		checkLength(len, 999);
	}

	public void setLength(int len) {
		checkLength(len, 999);
		super.setLength(len);
	}

	/** Must override ISOFieldPackager method to set the Interpreter correctly */
	public void setPad (boolean pad) {
		setInterpreter(pad ? HEXInterpreter.LEFT_PADDED : HEXInterpreter.RIGHT_PADDED);
		this.pad = pad;
	}
}
