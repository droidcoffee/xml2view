package droid.from.xml2view;

import java.util.HashMap;

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

	public static void applyProperties(View view, HashMap<String, String> attrs) {
		if (attrs == null || attrs.isEmpty()) {
			return;
		}
		int width = getIntValue(attrs.get("layout_width"));
		int height = getIntValue(attrs.get("layout_height"));
		if (view.getLayoutParams() == null) {
			LayoutParams params = new LayoutParams(width, height);
			view.setLayoutParams(params);
		} else {
			LayoutParams params = view.getLayoutParams();
			params.width = width;
			params.height = height;
			view.setLayoutParams(params);
		}
		// 然后调用invalidate
		int backroundColor = getColorIntValue(attrs.get("backgroud"));
		view.setBackgroundColor(backroundColor);

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
}
