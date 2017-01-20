package com.example.xml2view;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import dalvik.system.DexClassLoader;
import droid.frame.utils.Log;
import droid.frame.utils.xml2view.XmlParser;

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

		File dexInternalFile = null;
		try {
			BufferedInputStream bis = new BufferedInputStream(getAssets().open("plugin-apk.apk"));
			final File optimizedDexOutputDir = getDir("dex", Context.MODE_PRIVATE);
			dexInternalFile = new File(optimizedDexOutputDir, "plugin.apk");
			BufferedOutputStream dexWriter = new BufferedOutputStream(new FileOutputStream(dexInternalFile));
			byte[] buf = new byte[1024 * 8];
			int len;
			while ((len = bis.read(buf)) > 0) {
				dexWriter.write(buf, 0, len);
			}
			dexWriter.close();
			bis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		loadJarInAccets();

		loadApkInAccsts();
	}

	private void loadJarInAccets() {
		try {
			DexClassLoader loader = loadDex("class1dex.jar");
			Class<?> classToLoad = loader.loadClass("droid.frame.class1dex.ViewAction");
			final Object myInstance = classToLoad.newInstance();
			final Method doSomething = classToLoad.getMethod("doSomethind");
			doSomething.invoke(myInstance);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将jar或者apk加载到/data/data/package/dex/目录下
	 * 
	 * @param jarFile
	 *            如果是assets里面的文件 直接输入文件名file.jar 或者 file://android_asset/file.jar
	 */
	private DexClassLoader loadDex(String dexFile) {
		// 解压后的dex的存放目录
		final File optimizedDexOutputDir = getDir("dex", Context.MODE_PRIVATE);
		//
		File dexInternalFile = null;
		BufferedInputStream bis = null;
		OutputStream dexWriter = null;
		try {
			if (dexFile.startsWith("/")) {
				bis = new BufferedInputStream(new FileInputStream(dexFile));
				String dexName = dexFile.substring(dexFile.lastIndexOf("/") + 1);
				dexInternalFile = new File(optimizedDexOutputDir, dexName);
			} else {
				dexFile = dexFile.replace("file:///android_asset/", "");
				bis = new BufferedInputStream(getAssets().open(dexFile));
				String fileName = dexFile;
				dexInternalFile = new File(optimizedDexOutputDir, dexFile);
			}
			dexWriter = new BufferedOutputStream(new FileOutputStream(dexInternalFile));
			byte[] buf = new byte[1024 * 8];
			int len;
			while ((len = bis.read(buf)) > 0) {
				dexWriter.write(buf, 0, len);
			}
			dexWriter.close();
			bis.close();
			// dexInternalFile = new File("file:///android_asset/plugin-apk.apk");
			DexClassLoader cl = new DexClassLoader(dexInternalFile.getAbsolutePath(), optimizedDexOutputDir.getAbsolutePath(), null, getClass().getClassLoader());
			return cl;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void loadApkInAccsts() {
		try {
			DexClassLoader loader = loadDex("plugin-apk.apk");
			// Internal storage where the DexClassLoader writes the optimized dex file to

			String pkgName = "droid.frame.plugin";
			Context context = createPackageContext(pkgName, Context.CONTEXT_IGNORE_SECURITY | Context.CONTEXT_INCLUDE_CODE);

			Class<?> classToLoad = loader.loadClass("droid.frame.plugin.MainActivity");
			// Object activity = classToLoad.getDeclaredConstructors()[0].newInstance();
			// Method onCreate = classToLoad.getDeclaredMethod("onCreate", new Class[] { Bundle.class });
			// onCreate.setAccessible(true);
			// onCreate.invoke(activity, new Bundle());
			//
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setClass(context, classToLoad);
			context.startActivity(intent);
		} catch (Exception e) {// android.content.pm.PackageManager$NameNotFoundException: Application package droid.frame.plugin not found
			e.printStackTrace();
		}
	}

	private String install(String apkFile) {
		String result = "";
		Process process = null;
		InputStream errIs = null;
		InputStream inIs = null;
		try {
			String[] args = { "pm", "install", "-r", apkFile };
			ProcessBuilder processBuilder = new ProcessBuilder(args);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int read = -1;
			process = processBuilder.start();
			errIs = process.getErrorStream();
			while ((read = errIs.read()) != -1) {
				baos.write(read);
			}
			baos.write("/n".getBytes());
			inIs = process.getInputStream();
			while ((read = inIs.read()) != -1) {
				baos.write(read);
			}
			byte[] data = baos.toByteArray();
			result = new String(data);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (errIs != null) {
					errIs.close();
				}
				if (inIs != null) {
					inIs.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (process != null) {
				process.destroy();
			}
		}
		return result;
	}

	public String exec(String[] args) {
		String result = "";
		ProcessBuilder processBuilder = new ProcessBuilder(args);
		Process process = null;
		InputStream errIs = null;
		InputStream inIs = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int read = -1;
			process = processBuilder.start();
			errIs = process.getErrorStream();
			while ((read = errIs.read()) != -1) {
				baos.write(read);
			}
			baos.write("/n".getBytes());
			inIs = process.getInputStream();
			while ((read = inIs.read()) != -1) {
				baos.write(read);
			}
			byte[] data = baos.toByteArray();
			result = new String(data);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (errIs != null) {
					errIs.close();
				}
				if (inIs != null) {
					inIs.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (process != null) {
				process.destroy();
			}
		}
		return result;
	}
}
