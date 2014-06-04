package com.plugin.phonegap;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.pm.ActivityInfo;
import android.content.Intent;
import android.content.Context;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;

public class StartCustomVideoActivity extends CordovaPlugin {

	public StartCustomVideoActivity() {
	}

	@Override
	public boolean execute(String action, JSONArray arguments, CallbackContext callbackContext) {
		if (action.equals("video"))  {
		    Context context=this.cordova.getActivity().getApplicationContext();
		    Intent intent=new Intent(context,FullscreenActivity.class);
		    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		    
		    context.startActivity(intent);
		    return true;
		} else{
		    return false;
		}
	}
}
