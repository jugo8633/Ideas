package tw.org.iii.ideas.module;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.regex.Pattern;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Patterns;
import android.util.SparseArray;
import android.view.Display;
import android.view.WindowManager;
import tw.org.iii.ideas.common.Logs;

public class DeviceHandler
{
	private Activity		theActivity	= null;
	private LocationManager	manager		= null;

	public static class AccountData
	{
		public String	strAccount;
		public String	strType;
	}

	public static class TeleData
	{
		// 手機號碼
		public String	lineNumber;
		// 手機 IMEI
		public String	imei;
		// 手機 IMSI
		public String	imsi;
		// 手機漫遊狀態
		public String	roamingStatus;
		// 電信網路國別
		public String	country;
		// 電信公司代號
		public String	operator;
		// 電信公司名稱
		public String	operatorName;
		// 行動網路類型
		public String	networkType;
		// 行動通訊類型
		public String	phoneType;
	}

	public DeviceHandler(Activity activity)
	{
		theActivity = activity;
	}

	public String getIMEI()
	{
		if (null == theActivity)
			return null;
		TelephonyManager telephonyManager = (TelephonyManager) theActivity.getSystemService(Context.TELEPHONY_SERVICE);
		String strDeviceId = telephonyManager.getDeviceId();
		return strDeviceId;
	}

	public String getMacAddress()
	{
		if (null == theActivity)
			return null;
		WifiManager wifiManager = (WifiManager) theActivity.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		return wifiInfo.getMacAddress();
	}

	public void getTelecomInfo(TeleData teleData)
	{
		if (null == theActivity || null == teleData)
			return;
		TelephonyManager telManager = (TelephonyManager) theActivity.getSystemService(Context.TELEPHONY_SERVICE);

		// 手機號碼
		teleData.lineNumber = telManager.getLine1Number();

		// 手機 IMEI
		teleData.imei = telManager.getDeviceId();

		// 手機 IMSI
		teleData.imsi = telManager.getSubscriberId();

		// 手機漫遊狀態
		teleData.roamingStatus = telManager.isNetworkRoaming() ? "漫遊中" : "非漫遊";

		// 電信網路國別
		teleData.country = telManager.getNetworkCountryIso();

		// 電信公司代號
		teleData.operator = telManager.getNetworkOperator();

		// 電信公司名稱
		teleData.operatorName = telManager.getNetworkOperatorName();

		// 行動網路類型
		String[] networkTypeArray = { "UNKNOWN", "GPRS", "EDGE", "UMTS", "CDMA", "EVDO 0", "EVDO A", "1xRTT", "HSDPA",
				"HSUPA", "HSPA" };
		teleData.networkType = networkTypeArray[telManager.getNetworkType()];

		// 行動通訊類型
		String[] phoneTypeArray = { "NONE", "GSM", "CDMA" };
		teleData.phoneType = phoneTypeArray[telManager.getPhoneType()];
	}

	public String getLocalIpAddress()
	{
		try
		{
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
			{
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
				{
					InetAddress inetAddress = enumIpAddr.nextElement();

					// for getting IPV4 format
					if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address)
					{
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		}
		catch (Exception ex)
		{
			Logs.showTrace("IP Address" + ex.toString());
		}
		return null;
	}

	public int getSdkVer()
	{
		int sdkInt;
		try
		{
			sdkInt = android.os.Build.VERSION.SDK_INT;
		}
		catch (NumberFormatException nfe)
		{
			sdkInt = 10000;
		}
		Logs.showTrace("Android SDK: " + String.valueOf(sdkInt));
		return sdkInt;
	}

	public int getWidth()
	{
		int width = 0;
		width = getScreenResolution(theActivity).x;
		return width;
	}

	public int getHeight()
	{
		int height = 0;
		height = getScreenResolution(theActivity).y;
		return height;
	}

	public Point getScreenResolution(Context context)
	{
		WindowManager wm = (WindowManager) theActivity.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		return size;
	}

	public int getAccounts(SparseArray<AccountData> listAccount)
	{
		if (null == theActivity || null == listAccount)
			return 0;
		Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
		Account[] accounts = AccountManager.get(theActivity).getAccounts();
		AccountData data = null;
		for (Account account : accounts)
		{
			if (emailPattern.matcher(account.name).matches())
			{
				data = new AccountData();
				data.strAccount = account.name;
				data.strType = account.type;
				listAccount.put(listAccount.size(), data);
				data = null;
			}
		}
		return accounts.length;
	}

	public void getLocation()
	{
		manager = (LocationManager) theActivity.getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);

		// 設置不需要獲取海拔方向資料
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);

		// 設置允許產生資費
		criteria.setCostAllowed(true);

		// 要求低耗電
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		String provider = manager.getBestProvider(criteria, false);

		Logs.showTrace("Location Provider:" + provider);
		Location location = manager.getLastKnownLocation(provider);

		// 第一次獲得設備的位置
		updateLocation(location);

		// 重要函數，監聽資料測試
		manager.requestLocationUpdates(provider, 6000, 10, locationListener);
	}

	private final LocationListener locationListener = new LocationListener()
	{

		@Override
		public void onLocationChanged(Location location)
		{
			updateLocation(location);
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider)
		{
			Logs.showTrace("Provider now is enabled..");
		}

		@Override
		public void onProviderDisabled(String provider)
		{
			updateLocation(null);
			Logs.showTrace("Location Provider now is disabled..");
			final Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			theActivity.startActivity(intent);
		}

	};

	private void updateLocation(Location location)
	{
		String latLng;

		if (location != null)
		{
			double lat = location.getLatitude();
			double lng = location.getLongitude();
			latLng = "Latitude:" + lat + " Longitude:" + lng;
		}
		else
		{
			latLng = "Can't access your location";
		}

		Logs.showTrace("Location:" + latLng);
	}
}
