package droid.frame.utils.xml2view;

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

/**
 * 区分几个变量 <br>
 * xmlAttrName, xmlAttrValue, viewAttrName <br>
 * 服务器端返回的xml元素的name以及value 比如说layout_width="wrap_conntent", 也可能是lw="wc"<br>
 * viewAttrName是view控件的属性名,比如说layout_width
 * 
 * @author coffee <br>
 *         2017-1-17 下午8:46:53
 */
public class XmlFactory {

	private static boolean test = true;
	/**
	 * 目前支持的属性<br>
	 * key - view 属性名 value - xml对应的属性名(正式环境有可能是a、b、c省流量)<br>
	 * <LinearLayout layout_width="wrap_content" <br>
	 * <L a="-2"
	 */
	private static HashMap<String, String> supportAttrs = new HashMap<>();

	// *********** 以下是目前支持的属性***************
	private final static String layout_width = "layout_width";
	private final static String layout_height = "layout_height";
	private final static String backgroud = "backgroud";
	static {
		if (test) {
			supportAttrs.put(layout_width, "layout_width");
			supportAttrs.put(layout_height, "layout_height");
			supportAttrs.put(backgroud, "backgroud");
		} else {
			supportAttrs.put(layout_width, "lw");
			supportAttrs.put(layout_width, "lh");
			supportAttrs.put(backgroud, "bg");
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

	public static void applyProperties(View view, HashMap<String, String> xmlAttrsMap) {
		if (xmlAttrsMap == null || xmlAttrsMap.isEmpty()) {
			return;
		}
		// xml layout 文件里View的属性
		Set<String> names = xmlAttrsMap.keySet();
		// 先处理通用属性, 然后根据View类型处理各自不同的字段
		for (Iterator<String> it = names.iterator(); it.hasNext();) {
			String xmlAttrName = it.next();
			String viewAttrName = supportAttrs.get(xmlAttrName);
			// handle layout params
			if (isEquals(layout_width, viewAttrName)) {
				int width = getIntValue(layout_width, xmlAttrsMap);
				int height = getIntValue(layout_height, xmlAttrsMap);
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
			} else if (isEquals(backgroud, viewAttrName)) {
				// 然后调用invalidate
				int backroundColor = getColorIntValue(backgroud, xmlAttrsMap);
				view.setBackgroundColor(backroundColor);
			}

			if (view instanceof TextView) {
				((TextView) view).setText("hello xxx");
			}
		}
	}

	private static String getXmlAttrValue(String viewAttrName, HashMap<String, String> xmlAttrsMap) {
		String xmlAttrName = supportAttrs.get(viewAttrName);
		String xmlAttrValue = xmlAttrsMap.get(xmlAttrName);
		return xmlAttrValue;
	}

	private static int getIntValue(String viewAttrName, HashMap<String, String> xmlAttrsMap) {
		String xmlAttrValue = getXmlAttrValue(viewAttrName, xmlAttrsMap);
		if ("wrap_content".equals(xmlAttrValue)) {
			return LayoutParams.WRAP_CONTENT;
		} else if ("match_parent".equals(xmlAttrValue)) {
			return LayoutParams.MATCH_PARENT;
		} else if (xmlAttrValue.endsWith("dp") || xmlAttrValue.endsWith("dip") || xmlAttrValue.endsWith("sp")) {
			String tmp = xmlAttrValue.replace("dp", "").replace("dip", "").replace("sp", "");
			int value = Integer.valueOf(tmp);
			return value * 2;
		} else if (xmlAttrValue.endsWith("px")) {
			String tmp = xmlAttrValue.replace("px", "");
			int value = Integer.valueOf(tmp);
			return value;
		}
		return 0;
	}

	private static int getColorIntValue(String viewAttrName, HashMap<String, String> xmlAttrsMap) {
		String xmlAttrValue = getXmlAttrValue(viewAttrName, xmlAttrsMap);
		return Color.parseColor(xmlAttrValue);
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
		return xmlAttrName != null && xmlAttrName.equals(supportAttrs.get(viewAttrName));
	}
}
