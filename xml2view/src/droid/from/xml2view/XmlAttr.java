package droid.from.xml2view;

import android.graphics.Color;
import android.view.ViewGroup.LayoutParams;

/**
 * xml attribute 相关的工具类
 * 
 * @author coffee <br>
 *         2017-1-15 下午10:59:30
 */
public class XmlAttr {

	public static int getIntValue(String arrtibuteValue) {
		if ("warp_content".equals(arrtibuteValue)) {
			return LayoutParams.WRAP_CONTENT;
		} else if ("match_parent".equals(arrtibuteValue)) {
			return LayoutParams.MATCH_PARENT;
		}
		return 0;
	}

	public static int getColorIntValue(String arrtibuteValue) {
		return Color.parseColor(arrtibuteValue);
	}
}
