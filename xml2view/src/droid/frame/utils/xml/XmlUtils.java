package droid.frame.utils.xml;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import droid.frame.utils.annotation.Column;

/**
 * xml工具类
 * 
 * @author coffee <br>
 *         2017-1-16 上午12:20:35
 */
public class XmlUtils {

	/**
	 * 将List-->转换为xml
	 * 
	 * @param <T>
	 * @param items
	 *            ： List结果集
	 * @param attr
	 *            : 属性
	 */
	public static <T> String toXml(List<T> items, String... attrs) {
		if (items.size() == 0) {
			return "";
		}
		Class<?> clazz = items.get(0).getClass();
		Field[] fields = clazz.getDeclaredFields();
		StringBuilder sb = new StringBuilder();
		sb.append("<result>");
		String classAndArrrs = "<" + clazz.getSimpleName();
		int tag = 0;
		List<String> attrsList = Arrays.asList(attrs);
		for (String attr : attrsList) {
			attr = getColumnName(clazz, attr);
			classAndArrrs += " " + attr + "=\"{" + tag + "}\"";
			tag++;
		}
		classAndArrrs += ">";
		for (T t : items) {
			tag = 0;// 重置为0
			sb.append(classAndArrrs);
			for (Field field : fields) {
				String fieldName = field.getName();
				Object fieldValue = getValue(t, fieldName);
				String columnName = getColumnName(t.getClass(), fieldName);
				//
				if (attrsList.contains(field.getName())) {
					if (fieldValue == null) {
						fieldValue = "";
					}
					int start = sb.lastIndexOf("{" + tag + "}");
					sb.replace(start, start + 2 + Integer.toString(tag).length(), fieldValue.toString());
					tag++;
				} else {
					// 不显示值为null的字段
					if (fieldValue == null) {
						continue;
					}
					sb.append("<").append(columnName).append(">").append(fieldValue).append("</").append(columnName).append(">");
				}
			}// inline for
			sb.append("</").append(clazz.getSimpleName()).append(">");
		}// outline for
		sb.append("</result>");
		// Outer.setPath(xmlPath, true, true);
		// Outer.p(sb.toString());
		return sb.toString();
	}

	public static <T> String toXml(T bean, String... attrs) {
		List<T> items = new ArrayList<T>();
		items.add(bean);
		String result = toXml(items, attrs);
		result = result.replaceAll("</?result>", "");// 去掉开头结尾的 <result></result>
		return result;
	}

	/**
	 * 将xml文档转换为List
	 * 
	 * @param xmlDoc
	 */
	public static void toList(String xmlDoc) {

	}

	private static <T> Object getValue(T obj, String fieldName) {
		String firstChar = fieldName.charAt(0) + "";
		try {
			String methodName = "get" + fieldName.replaceFirst(".", firstChar.toUpperCase(Locale.getDefault()));
			return obj.getClass().getMethod(methodName, new Class[] {}).invoke(obj, new Object[] {});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取列名
	 */
	public static <T> String getColumnName(Class<T> clazz, String fieldName) {
		Field field = null;
		try {
			field = clazz.getDeclaredField(fieldName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Column column = field.getAnnotation(Column.class);
		if (column != null) {
			return column.name();
		} else {
			return fieldName;
		}
	}
}
