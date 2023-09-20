package net.iptux.xposed.callrecording;

import android.content.pm.PackageManager;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.os.Build;
import android.os.Bundle;
import android.preference.SwitchPreference;

public class SettingsActivity extends PreferenceActivity{

	static final int REQUEST_STORAGE_PERMISSION = 0x10ae;

	SwitchPreference mPrefSkipMediaScan;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getPreferenceManager().setSharedPreferencesMode(MODE_WORLD_READABLE);
		addPreferencesFromResource(R.xml.preferences);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		switch (requestCode) {
		case REQUEST_STORAGE_PERMISSION:
			if (grantResults.length > 0 && PackageManager.PERMISSION_GRANTED == grantResults[0]) {
				Utility.setRecordingSkipMediaScan(mPrefSkipMediaScan.isChecked());
			}
			break;
		default:
			break;
		}
	}
}
