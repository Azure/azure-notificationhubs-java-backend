package com.windowsazure.messaging;

public class SdkGlobalSettings {
	private static int authorizationTokenExpirationInMinutes = 60;
	
	public static int getAuthorizationTokenExpirationInMinutes(){
		return SdkGlobalSettings.authorizationTokenExpirationInMinutes;
	}
	
	public static void setAuthorizationTokenExpirationInMinutes(int value){
		SdkGlobalSettings.authorizationTokenExpirationInMinutes = value;
	}
}
