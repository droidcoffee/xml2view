package droid.from.xml2view;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;

public class LinearLayout extends android.widget.LinearLayout implements Xml2View {

	private AtomicBoolean invalidate = new AtomicBoolean(false);
	private String layout_width;
	private String layout_height;
	private String backgroud;

	private ArrayList<Xml2View> children = new ArrayList<>();

	public LinearLayout(Context context) {
		super(context);
	}

	@Override
	public void invalidate() {
		if (invalidate.compareAndSet(false, true)) {
			int width = XmlAttr.getIntValue(layout_width);
			int height = XmlAttr.getIntValue(layout_height);
			LayoutParams params = new LayoutParams(width, height);
			setLayoutParams(params);
			// 然后调用invalidate
			int backroundColor = XmlAttr.getColorIntValue(backgroud);
			setBackgroundColor(backroundColor);
			// 计算出高度以后调用invalidate
			super.invalidate();
		} else {
			super.invalidate();
		}
	}

}
