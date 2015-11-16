package tw.org.iii.ideas;

import tw.org.iii.ideas.common.Logs;
import tw.org.iii.ideas.common.Messages;
import tw.org.iii.ideas.common.Device;
import tw.org.iii.ideas.layout.LoginHandler;
import tw.org.iii.ideas.layout.MainHandler;
import tw.org.iii.ideas.module.InvoiceScanHandler;
import tw.org.iii.ideas.module.DeviceHandler;
import tw.org.iii.ideas.module.FacebookHandler;

import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;

public class MainActivity extends Activity
{
	private MainHandler		mainHandler		= null;
	private LoginHandler	loginHandler	= null;
	private DeviceHandler	deviceHandler	= null;
	private Tracker			tracker			= null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		showLoginView();
		// showMainView();
		deviceHandler = new DeviceHandler(this);

		Logs.showTrace("Device IMEI:" + deviceHandler.getIMEI());
		Logs.showTrace("Device MAC Address:" + deviceHandler.getMacAddress());

		SparseArray<DeviceHandler.AccountData> listAccount = new SparseArray<DeviceHandler.AccountData>();
		deviceHandler.getAccounts(listAccount);
		for (int i = 0; i < listAccount.size(); ++i)
		{
			Logs.showTrace(listAccount.get(i).strType + " Account is: " + listAccount.get(i).strAccount);
		}

		DeviceHandler.TeleData teleData = new DeviceHandler.TeleData();
		deviceHandler.getTelecomInfo(teleData);
		Logs.showTrace("������X:" + teleData.lineNumber);
		Logs.showTrace("��� IMEI:" + teleData.imei);
		Logs.showTrace("��� IMSI:" + teleData.imsi);
		Logs.showTrace("������C���A:" + teleData.roamingStatus);
		Logs.showTrace("�q�H������O:" + teleData.country);
		Logs.showTrace("�q�H���q�N��:" + teleData.operator);
		Logs.showTrace("�q�H���q�W��:" + teleData.operatorName);
		Logs.showTrace("��ʺ�������:" + teleData.networkType);
		Logs.showTrace("��ʳq�T����:" + teleData.phoneType);

		deviceHandler.getLocation();

		String strAAID = Device.getAAID(this);
		Logs.showTrace("AAID:" + strAAID);
		Device.getGaid(this);
		
		tracker = ((IdeasApplication) getApplication()).getTracker(IdeasApplication.TrackerName.APP_TRACKER);
	}

	@Override
	protected void onStart()
	{
		GoogleAnalytics.getInstance(this).reportActivityStart(this);
		super.onStart();
	}

	@Override
	protected void onStop()
	{
		GoogleAnalytics.getInstance(this).reportActivityStop(this);
		super.onStop();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		AppEventsLogger.activateApp(this);
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		AppEventsLogger.deactivateApp(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && InvoiceScanHandler.ACTIVITY_REQUEST_CODE == requestCode)
		{
			Bundle bundle = data.getExtras();
			String scanResult = bundle.getString("result");
			String strReward = bundle.getString("reward");
			Logs.showTrace("QR Code:" + scanResult);
		}
		else
		{
			FacebookHandler.callbackManager.onActivityResult(requestCode, resultCode, data);
		}
	}

	private void showMainView()
	{
		mainHandler = new MainHandler(this, theHandler);
		mainHandler.init();
		mainHandler.show();
	}

	private void showLoginView()
	{
		loginHandler = new LoginHandler(this, theHandler);
		loginHandler.init();
		loginHandler.show();
	}

	private void showQrScanner()
	{
		Intent openCameraIntent = new Intent(MainActivity.this, InvoiceScanHandler.class);
		// openCameraIntent.putExtra("missionindex", nMissionDataIndex);
		startActivityForResult(openCameraIntent, InvoiceScanHandler.ACTIVITY_REQUEST_CODE);
	}

	private Handler theHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case Messages.MSG_SHOW_QR_SCANNER:
				showQrScanner();
				break;
			}
		}
	};
}
