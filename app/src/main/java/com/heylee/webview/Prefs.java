package com.heylee.webview;

import android.content.Context;
import android.content.SharedPreferences;


public final class Prefs {

	public class keys{
		public static final String URL = "URL";
		public static final String HISTORY = "HISTORY";
		public static final String CUSTOM_URL = "CUSTOM_URL";
	}

	public static SharedPreferences get(Context context) {
		return context.getSharedPreferences("_PREFS", Context.MODE_PRIVATE);
	}

	public static String getString(Context context, String key, String defValue) {
		String getString = get(context).getString(key, defValue);
		return getString;
	}

	public static void putString(Context context, String key, String value) {
		SharedPreferences.Editor editor = Prefs.get(context).edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static void remove(Context context, String key) {
		SharedPreferences.Editor editor = Prefs.get(context).edit();
		editor.remove(key);
		editor.commit();
	}

	public static void clear(Context context) {
		SharedPreferences.Editor editor = Prefs.get(context).edit();
		editor.clear();
		editor.commit();
	}

}
