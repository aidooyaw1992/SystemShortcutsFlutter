package com.example.systemshortcuts;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;


import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * SystemShortcutsPlugin
 */
public class SystemShortcutsPlugin implements FlutterPlugin, ActivityAware, MethodCallHandler {
    private MethodChannel channel;
    private Activity activity;

    /**
     * v2 plugin embedding
     */
    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
        channel = new MethodChannel(
                binding.getBinaryMessenger(), "system_shortcuts");
        channel.setMethodCallHandler(this);
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
        channel = null;
    }

    @Override
    public void onAttachedToActivity(ActivityPluginBinding binding) {
        activity = binding.getActivity();
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //Check if the phone is running Marshmallow or above
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            //If the permission is not granted, launch an inbuilt activity to grant permission
            if (!nm.isNotificationPolicyAccessGranted()) {
                startActivity(new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS));
            }
        }
    }

    @Override
    public void onDetachedFromActivity() {
        activity = null;
    }

    @Override
    public void onReattachedToActivityForConfigChanges(ActivityPluginBinding binding) {
        activity = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        activity = null;
    }

    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {
        SystemShortcutsPlugin instance = new SystemShortcutsPlugin();
        instance.channel = new MethodChannel(registrar.messenger(), "system_shortcuts");
        instance.activity = registrar.activity();
        instance.channel.setMethodCallHandler(instance);
    }

    @Override
    public void onMethodCall(MethodCall call, @NonNull Result result) {
        switch (call.method) {
            case "home":
                home();
                break;
            case "back":
                back();
                break;
            case "volDown":
                volDown();
                break;
            case "volUp":
                volUp();
                break;
            case "orientLandscape":
                orientLandscape();
                break;
            case "orientPortrait":
                orientPortrait();
                break;
            case "wifi":
                wifi();
                break;
            case "checkWifi":
                result.success(checkWifi());
                break;
            case "bluetooth":
                bluetooth();
                break; 
            case "silentMode":
                silentMode();
                break;
            case "checkBluetooth":
                result.success(checkBluetooth());
                break;
            default:
                result.notImplemented();
                break;
        }
    }

    private void silentMode() {
        NotificationManager nm = (NotificationManager)this.activity.getApplicationContext().getSystemService(NOTIFICATION_SERVICE);

        AudioManager audioManager = (AudioManager) this.activity.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M && nm.isNotificationPolicyAccessGranted())
        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
    }

    private void home() {
        this.activity.startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME));
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private void back() {
        this.activity.onBackPressed();
    }

    private void volDown() {
        AudioManager audioManager = (AudioManager) this.activity.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
    }

    private void volUp() {
        AudioManager audioManager = (AudioManager) this.activity.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
    }

    private void orientLandscape() {
        this.activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    private void orientPortrait() {
        this.activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void wifi() {
        WifiManager wifiManager = (WifiManager) this.activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
        } else {
            wifiManager.setWifiEnabled(true);
        }
    }

    private boolean checkWifi() {
        WifiManager wifiManager = (WifiManager) this.activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private void bluetooth() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.disable();
        } else {
            mBluetoothAdapter.enable();
        }
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private boolean checkBluetooth() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return mBluetoothAdapter.isEnabled();
    }

}
