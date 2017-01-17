package droid.frame.xml2view;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

public class XmlFactory {

	private static boolean test = true;
	/**
	 * 目前支持的属性<br>
	 * key - view 属性名 value - xml对应的属性名(正式环境有可能是a、b、c省流量)<br>
	 * <LinearLayout layout_width="wrap_content" <br>
	 * <L a="-2"
	 */
	private static HashMap<String, String> namesMap = new HashMap<>();

	// *********** 以下是目前支持的属性***************
	private final static String layout_width = "layout_width";
	private final static String layout_height = "layout_height";
	private final static String background = "background";
	static {
		if (test) {
			namesMap.put(layout_width, "layout_width");
			namesMap.put(layout_height, "layout_height");
		} else {
			namesMap.put(layout_width, "lw");
			namesMap.put(layout_width, "lh");
		}
	}

	public static ViewGroup createViewGroup(String viewType, Context context) {
		ViewGroup parent = null;
		if (LinearLayout.class.getSimpleName().equals(viewType)) {
			parent = new LinearLayout(context);
		} else if (RelativeLayout.class.getSimpleName().equals(viewType)) {

		} else if (FrameLayout.class.getSimpleName().equals(viewType)) {

		} else if (TableLayout.class.getSimpleName().equals(viewType)) {

		}
		return parent;
	}

	public static View createView(String viewType, Context context) {
		View child = null;
		if (TextView.class.getSimpleName().equals(viewType)) {
			child = new TextView(context);
		} else if (Button.class.getSimpleName().equals(viewType)) {
			child = new Button(context);
		} else if (EditText.class.getSimpleName().equals(viewType)) {
			child = new EditText(context);
		} else if (ImageView.class.getSimpleName().equals(viewType)) {
			child = new ImageView(context);
		}
		return child;
	}

	public static void applyProperties(View view, HashMap<String, String> attrsMap) {
		if (attrsMap == null || attrsMap.isEmpty()) {
			return;
		}
		Set<String> names = attrsMap.keySet();
		for (Iterator<String> it = names.iterator(); it.hasNext();) {
			String xmlAttrName = it.next();
			String xmlAttrValue = attrsMap.get(xmlAttrName);
			String viewAttrName = namesMap.get(xmlAttrName);
			// handle layout params
			if (isEquals(layout_width, viewAttrName)) {
				int width = getIntValue(xmlAttrValue);
				String viewLayoutHeight = attrsMap.get(namesMap.get(layout_height));
				int height = getIntValue(viewLayoutHeight);

				if (view.getLayoutParams() == null) {
					LayoutParams params = new LayoutParams(width, height);
					view.setLayoutParams(params);
				} else {
					LayoutParams params = view.getLayoutParams();
					params.width = width;
					params.height = height;
					view.setLayoutParams(params);
				}
				//
				it.remove();
			} else if (isEquals(layout_height, viewAttrName)) {
				continue;// width的时候已经处理过
			}

		}
		// 然后调用invalidate
		// int backroundColor = getColorIntValue(attrs.get("backgroud"));
		// view.setBackgroundColor(backroundColor);

		if (view instanceof TextView) {
			((TextView) view).setText("hello xxx");
		}
	}

	private static int getIntValue(String arrtibuteValue) {
		if ("wrap_content".equals(arrtibuteValue)) {
			return LayoutParams.WRAP_CONTENT;
		} else if ("match_parent".equals(arrtibuteValue)) {
			return LayoutParams.MATCH_PARENT;
		}
		return 0;
	}

	private static int getColorIntValue(String arrtibuteValue) {
		return Color.parseColor(arrtibuteValue);
	}

	/**
	 * 
	 * @param viewAttrName
	 *            传入view的属性名 layout_width
	 * @param xmlAttrName
	 *            传入xml节点元素的attribute
	 * @return
	 */
	private static boolean isEquals(String viewAttrName, String xmlAttrName) {
		return xmlAttrName.equals(namesMap.get(viewAttrName));
	}
}
