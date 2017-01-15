package droid.from.xml2view;

import java.security.InvalidAlgorithmParameterException;

import android.content.Context;

/**
 * 
 * @author coffee <br>
 *         2017-1-15 下午10:21:57
 */
public class TextView extends android.widget.TextView implements Xml2View {

	public TextView(Context context) {
		super(context);
	}

	@Override
	public void invalidate() {
		super.invalidate();
	}
}
