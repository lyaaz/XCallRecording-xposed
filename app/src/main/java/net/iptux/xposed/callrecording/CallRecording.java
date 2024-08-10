package net.iptux.xposed.callrecording;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Build;

import static de.robv.android.xposed.XposedBridge.hookAllMethods;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class CallRecording implements IXposedHookLoadPackage {
	private static final String PACKAGE_DIALER = "com.android.dialer";
	private static final String CALL_RECORDING_SERVICE = "com.android.services.callrecorder.CallRecorderService";
	private static final String CALL_BUTTON_PRESENTER = "com.android.incallui.CallButtonPresenter";
	private static final String CALL_BUTTON_FRAGMENT = "com.android.incallui.CallButtonFragment";
	private static final String CALL_RECORDING_LISTENER = "com.android.incallui.CallRecorder";

	private static final String CALL_RECORDING_LISTENER_LOS15 = "com.android.incallui.call.CallRecorder";
	private static final String CALL_RECORDING_SERVICE_LOS15 = "com.android.dialer.callrecord.impl.CallRecorderService";
	private static final String CALL_BUTTON_FRAGMENT_LOS15 = "com.android.incallui.incall.impl.ButtonController$CallRecordButtonController";

	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
		if (PACKAGE_DIALER.equals(lpparam.packageName)) {
			Utility.d("handleLoadPackage: packageName=%s", lpparam.packageName);
			hookDialer(lpparam);
		}
	}

	void hookDialer(LoadPackageParam lpparam) throws Throwable {
		final String callRecordingListener;
		final String callRecordingServiceName;
		final String callButtonFragment;

		final int version = Build.VERSION.SDK_INT;
		if (version >= Build.VERSION_CODES.O) {
			callRecordingListener = CALL_RECORDING_LISTENER_LOS15;
			callRecordingServiceName = CALL_RECORDING_SERVICE_LOS15;
			callButtonFragment = CALL_BUTTON_FRAGMENT_LOS15;
		} else {
			callRecordingListener = CALL_RECORDING_LISTENER;
			callRecordingServiceName = CALL_RECORDING_SERVICE;
			callButtonFragment = CALL_BUTTON_FRAGMENT;
		}

		try {
			findAndHookMethod(callRecordingServiceName, lpparam.classLoader, "isEnabled", Context.class, new IsEnabledHook());
		} catch (Throwable e) {
			// CallRecorderService.isEnabled(context) may get inlined and not present
		}

		try {
			findAndHookMethod(callRecordingListener, lpparam.classLoader, "isEnabled", new IsEnabledHook());
		} catch (Throwable e) {
			// instead, try to hook CallRecorder.isEnabled()
		}

		try {
			// This method is used in place of isEnabled in later versions
			findAndHookMethod(callRecordingListener, lpparam.classLoader, "canRecordInCurrentCountry", new IsEnabledHook());
		} catch (Throwable e) {
			// ignored
		}

		findAndHookMethod(callRecordingServiceName, lpparam.classLoader, "generateFilename", String.class, new GenerateFilenameHook());

		final Class<?> CallButtonPresenter = XposedHelpers.findClass(CALL_BUTTON_PRESENTER, lpparam.classLoader);
		hookAllMethods(CallButtonPresenter, "onStateChange", new OnStateChangeHook());

		final Class<?> CallButtonFragment = XposedHelpers.findClass(callButtonFragment, lpparam.classLoader);
		hookAllMethods(CallButtonFragment, "setEnabled", new SetEnabledHook());

	}
}
