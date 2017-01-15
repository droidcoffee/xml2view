package droid.frame.utils.xml.parser;

import java.lang.reflect.Field;

import droid.frame.utils.TUtils;
import droid.frame.utils.annotation.Bean;
import droid.frame.utils.annotation.Column;

/**
 * 该方法是基于注解的
 */
public class TXmlUtils extends TUtils {
	/**
	 * 判断节点是否是Root元素; 对应的是class级别的
	 * 
	 * @param clazz
	 * @param elemName
	 * @return
	 */
	public static boolean isRootElement(Class<?> clazz, String elemName) {
		Bean root = clazz.getAnnotation(Bean.class);
		if (root != null) {
			if (elemName.equals(root.name()) || elemName.equals(root.xml())) {
				return true;
			} else {
				return false;
			}
		}
		// 如果没有Bean注解，则继续。。
		if (clazz.getSimpleName().equalsIgnoreCase(elemName)) {
			return true;
		}
		return false;
	}

	/**
	 * 判断接点名是否是普通元素; 对应的是field级别的
	 * 
	 * @param clazz
	 * @param elemName
	 * @return
	 */
	public static boolean isElement(Class<?> clazz, String elemName) {
		try {
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				Column xmlElem = field.getAnnotation(Column.class);
				if (xmlElem != null) {
					if (!"".equals(xmlElem.xml()) && elemName.equals(xmlElem.xml())) {
						return true;
					} else if(!"".equals(xmlElem.name()) && elemName.equals(xmlElem.name())){
						return true;
					}
				}// 如果没有注解， 则直接返回elem
				else if (field.getName().equals(elemName)) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 获取某一个元素所对应的Field名字
	 */
	public static String getFieldName(Class<?> clazz, String elem) {
		if (elem == null) {
			return null;
		}
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			Column xmlElem = field.getAnnotation(Column.class);
			if (xmlElem != null) {
				if (elem.equals(xmlElem.name()) || elem.equals(xmlElem.xml())) {
					return field.getName();
				}
			}// 如果没有注解， 则直接返回elem
		}
		return elem;
	}

	/**
	 * 判断相关的字段是否是List
	 */
	public static boolean isListElement(Class<?> clazz, String fieldName) {
		if (fieldName != null) {
			try {
				if (clazz.getDeclaredField(fieldName).getType().getSimpleName().contains("List")) {
					return true;
				}
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * 判断相关的字段是否是Map
	 */
	public static boolean isMapElement(Class<?> clazz, String fieldName) {
		if (fieldName != null) {
			try {
				if (clazz.getDeclaredField(fieldName).getType().getSimpleName().contains("Map")) {
					return true;
				}
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}
		return false;
	}

}
