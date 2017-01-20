package droid.frame.utils.xml2view;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Stack;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.util.Xml;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;

public class XmlParser {

	/**
	 * 每次parser只有一个全局的对象
	 */
	private XmlPullParser parser = Xml.newPullParser();

	private Context context;

	private String xmlText;

	/**
	 * 记录当前的跟节点
	 */
	private Stack<ViewGroup> viewParents = new Stack<>();

	/**
	 * 设置需要pull的xml文本内容
	 */
	public XmlParser(String xmlText) {
		this(xmlText, null);
	}

	public XmlParser(String xmlText, Context context) {
		try {
			this.xmlText = xmlText;
			this.context = context;
			parser.setInput(new StringReader(xmlText));
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
	}

	public XmlParser(InputStream in, Context context) {
		this.context = context;
		StringBuilder xmlText = new StringBuilder();
		BufferedInputStream bin = new BufferedInputStream(in);
		byte[] data = new byte[1024 * 10];
		int len = 0;
		try {
			while ((len = bin.read(data)) != -1) {
				xmlText.append(new String(data, 0, len));
			}
			bin.close();
			this.xmlText = xmlText.toString();
			parser.setInput(new StringReader(xmlText.toString()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * pull流文件
	 */
	public XmlParser(InputStream in) {
		this(in, null);
	}

	/**
	 * 利用android内置的pull方式解析xml文件
	 * 
	 * @param clazz
	 * @param listTag
	 *            : 只有在递归的时候才用到，当用的时候 <sale> <saleId></saleId> <saleTitle></saleTitle> <goodsList> <goods></goods> <goods></goods> <goods></goods> </goodsList> </sale> 那么在调用的时候就会掺入 Goods.class,
	 *            goodsList : 当解析遇到goodsList结束的时候便会结束递归 因为 parser对象是全局的
	 */
	public ViewParent puller2View() {
		if (xmlText == null || "".equals(xmlText.toString().trim())) {
			return null;
		}
		ViewParent rootLayout = null;
		try {
			String tagName = null;
			int event = parser.getEventType();
			while (event != XmlPullParser.END_DOCUMENT) {
				tagName = parser.getName();
				// 配置参数为空，则按照class的name以及field的name进行匹配
				switch (event) {
				case XmlPullParser.START_TAG:// 2
					View currentView = null;
					// Class级别
					if (isViewGroup(tagName)) {
						ViewGroup parent = XmlFactory.createViewGroup(tagName, context);
						this.viewParents.add(parent);
						currentView = parent;// set current
					} else { // Field级别
						View child = XmlFactory.createView(tagName, context);
						ViewGroup parent = viewParents.peek();
						if (parent == null) {
							continue;
						} else {
							parent.addView(child);
						}
						currentView = child;// set current
					}
					HashMap<String, String> attrs = new HashMap<String, String>();
					// 取属性:
					for (int i = 0; i < parser.getAttributeCount(); i++) {
						String attrName = parser.getAttributeName(i);
						String attrValue = parser.getAttributeValue(i);
						attrs.put(attrName, attrValue);
					}
					// 设置属性
					XmlFactory.applyProperties(currentView, attrs);
					break;
				case XmlPullParser.END_TAG:// 3
					if (isViewGroup(tagName)) {
						rootLayout = this.viewParents.peek();
						this.viewParents.pop();
					}
					break;
				}// ++....
				event = parser.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rootLayout;
	}

	/**
	 * 判断节点是否是Root元素; 对应的是class级别的
	 * 
	 * @param clazz
	 * @param elemName
	 * @return
	 */
	private static boolean isViewGroup(String elemName) {
		if (LinearLayout.class.getSimpleName().equals(elemName) || //
				RelativeLayout.class.getSimpleName().equals(elemName) || //
				FrameLayout.class.getSimpleName().equals(elemName) || //
				TableLayout.class.getSimpleName().equals(elemName)) {
			return true;
		}
		return false;
	}
}
