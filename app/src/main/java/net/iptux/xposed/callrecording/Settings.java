package net.iptux.xposed.callrecording;

import android.os.Build;

import de.robv.android.xposed.XSharedPreferences;

class Settings {
	private static final String PREF_RECORD_ENABLE = "record_enabled";
	private static final String PREF_RECORD_INCOMING = "record_incoming";
	private static final String PREF_RECORD_OUTGOING = "record_outgoing";
	private static final String PREF_RECORD_DELAY2 = "record_delay2";
	private static final String PREF_PREPEND_CONTACT_NAME = "prepend_contact_name";
	static final String PREF_VERSION_NAME = "version_name";

	private static class SingletonHelper {
		private static final Settings INSTANCE = new Settings();
	}

	static Settings getInstance() {
		return SingletonHelper.INSTANCE;
	}

	XSharedPreferences prefs;
	private Settings() {
		prefs = new XSharedPreferences(BuildConfig.APPLICATION_ID);
	}

	void reload() {
		prefs.reload();
	}

	boolean isRecordEnable() {
		return prefs.getBoolean(PREF_RECORD_ENABLE, true);
	}

	boolean isRecordIncoming() {
		return prefs.getBoolean(PREF_RECORD_INCOMING, true);
	}

	boolean isRecordOutgoing() {
		return prefs.getBoolean(PREF_RECORD_OUTGOING, true);
	}

	int getRecordDelay() {
		int delay = 100;
		try {
			delay = Integer.parseInt(prefs.getString(PREF_RECORD_DELAY2, "100"));
		} catch (NumberFormatException ignored) {
		}
		return delay;
	}

	boolean isPrependContactName() {
		return prefs.getBoolean(PREF_PREPEND_CONTACT_NAME, true);
	}
}
