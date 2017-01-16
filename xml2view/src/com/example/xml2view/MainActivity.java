package com.example.xml2view;

import java.io.InputStream;
import java.lang.reflect.Field;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
