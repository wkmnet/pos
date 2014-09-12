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
public class IFB_LLTrack extends ISOStringFieldPackager{

	public IFB_LLTrack() {
		super(NullPadder.INSTANCE, HEXInterpreter.RIGHT_PADDED, BcdPrefixer.LL);
	}
	/**
	 * @param len - field len
	 * @param description symbolic descrption
	 */
	public IFB_LLTrack(int len, String description, boolean isLeftPadded) {
		super(len, description, NullPadder.INSTANCE,
				isLeftPadded ? HEXInterpreter.LEFT_PADDED : BCDInterpreter.RIGHT_PADDED,
				BcdPrefixer.LL);
		checkLength(len, 99);
	}

	public IFB_LLTrack(int len, String description, boolean isLeftPadded, boolean fPadded) {
		super(len, description, NullPadder.INSTANCE,
				isLeftPadded ? HEXInterpreter.LEFT_PADDED :
						(fPadded ? HEXInterpreter.RIGHT_PADDED_F : HEXInterpreter.RIGHT_PADDED),
				BcdPrefixer.LL);
		checkLength(len, 99);
	}

	public void setLength(int len) {
		checkLength(len, 99);
		super.setLength(len);
	}

	/** Must override ISOFieldPackager method to set the Interpreter correctly */
	public void setPad (boolean pad) {
		setInterpreter(pad ? HEXInterpreter.LEFT_PADDED : HEXInterpreter.RIGHT_PADDED);
		this.pad = pad;
	}
}
