package net.iptux.xposed.callrecording;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;

import java.io.File;

class GenerateFilenameHook extends MethodHook {
	@Override
	protected void afterHookedMethod(MethodHookParam param) {
		String result = (String) param.getResult();
		String trim = result.trim();
		Settings settings = getSettings();
		if (settings.isPrependContactName()) {
			Context context = (Context) param.thisObject;
			String number = (String) param.args[0];
			String name = getContactName(context, number);
			if (!TextUtils.isEmpty(name)) {
				if (settings.isPrependContactName()) {
					trim = name + '_' + trim;
				}
				number = name;
			}
		}
		param.setResult(trim);
	}

	String getContactName(Context context, String number) {
		if (null == context || TextUtils.isEmpty(number)) {
			return null;
		}
		Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
		try (Cursor cursor = context.getContentResolver().query(lookupUri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null)) {
			if (null == cursor || cursor.getCount() == 0) {
				return null;
			}
			cursor.moveToNext();
			@SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
			return name;
		}
	}
}
