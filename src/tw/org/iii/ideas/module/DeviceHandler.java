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
		// ������X
		public String	lineNumber;
		// ��� IMEI
		public String	imei;
		// ��� IMSI
		public String	imsi;
		// ������C���A
		public String	roamingStatus;
		// �q�H������O
		public String	country;
		// �q�H���q�N��
		public String	operator;
		// �q�H���q�W��
		public String	operatorName;
		// ��ʺ�������
		public String	networkType;
		// ��ʳq�T����
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

		// ������X
		teleData.lineNumber = telManager.getLine1Number();

		// ��� IMEI
		teleData.imei = telManager.getDeviceId();

		// ��� IMSI
		teleData.imsi = telManager.getSubscriberId();

		// ������C���A
		teleData.roamingStatus = telManager.isNetworkRoaming() ? "���C��" : "�D���C";

		// �q�H������O
		teleData.country = telManager.getNetworkCountryIso();

		// �q�H���q�N��
		teleData.operator = telManager.getNetworkOperator();

		// �q�H���q�W��
		teleData.operatorName = telManager.getNetworkOperatorName();

		// ��ʺ�������
		String[] networkTypeArray = { "UNKNOWN", "GPRS", "EDGE", "UMTS", "CDMA", "EVDO 0", "EVDO A", "1xRTT", "HSDPA",
				"HSUPA", "HSPA" };
		teleData.networkType = networkTypeArray[telManager.getNetworkType()];

		// ��ʳq�T����
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

		// �]�m���ݭn������ޤ�V���
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);

		// �]�m���\���͸�O
		criteria.setCostAllowed(true);

		// �n�D�C�ӹq
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		String provider = manager.getBestProvider(criteria, false);

		Logs.showTrace("Location Provider:" + provider);
		Location location = manager.getLastKnownLocation(provider);

		// �Ĥ@����o�]�ƪ���m
		updateLocation(location);

		// ���n��ơA��ť��ƴ���
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
