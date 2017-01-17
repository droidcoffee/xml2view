package com.example.xml2view;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.LinearLayout;
import dalvik.system.DexClassLoader;
import droid.frame.utils.Log;
import droid.frame.utils.xml.parser.XmlParser;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		try {
			LinearLayout layout = LinearLayout.class.getDeclaredConstructor(Context.class).newInstance(this);
			Log.d("layout", layout);
			Field[] fields = layout.getClass().getFields();
			fields = layout.getClass().getDeclaredFields();
			Log.d("layout", fields);
			InputStream in = getAssets().open("main.xml");
			layout = (LinearLayout) new XmlParser(in, this).puller2View();
			Log.d("layout", layout);
			layout.invalidate();
			setContentView(layout);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//
		loadjar();

		loadJarInAccets();
	}

	@SuppressWarnings("unchecked")
	private void loadjar() {
		try {

			// final String libPath = Environment.getExternalStorageDirectory() + "/shlublu.jar";
			final String libPath = "file:///android_asset/class1dex.jar";
			final File tmpDir = getDir("dex", Context.MODE_PRIVATE);

			final DexClassLoader classloader = new DexClassLoader(libPath, tmpDir.getAbsolutePath(), null, this.getClass().getClassLoader());
			final Class<Object> classToLoad = (Class<Object>) classloader.loadClass("droid.frame.class1dex.ViewAction");

			final Object myInstance = classToLoad.newInstance();
			final Method doSomething = classToLoad.getMethod("doSomethind");

			doSomething.invoke(myInstance);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadJarInAccets() {
		// data/data/.../package/app_dex
		// 先将assets中的jar保存到 以上目录
		File dexInternalStoragePath = new File(getDir("dex", Context.MODE_PRIVATE), "class1dex.jar");
		BufferedInputStream bis = null;
		OutputStream dexWriter = null;
		final int BUF_SIZE = 8 * 1024;
		try {
			bis = new BufferedInputStream(getAssets().open("class1dex.jar"));
			dexWriter = new BufferedOutputStream(new FileOutputStream(dexInternalStoragePath));
			byte[] buf = new byte[BUF_SIZE];
			int len;
			while ((len = bis.read(buf, 0, BUF_SIZE)) > 0) {
				dexWriter.write(buf, 0, len);
			}
			dexWriter.close();
			bis.close();
			// Internal storage where the DexClassLoader writes the optimized dex file to
			// 解压后的dex存放位置
			final File optimizedDexOutputPath = getDir("dex", Context.MODE_PRIVATE);
			DexClassLoader cl = new DexClassLoader(dexInternalStoragePath.getAbsolutePath(), optimizedDexOutputPath.getAbsolutePath(), null, getClass().getClassLoader());
			Class<?> classToLoad = cl.loadClass("droid.frame.class1dex.ViewAction");

			final Object myInstance = classToLoad.newInstance();
			final Method doSomething = classToLoad.getMethod("doSomethind");

			doSomething.invoke(myInstance);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
