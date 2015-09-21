package tw.org.iii.ideas;

import tw.org.iii.ideas.common.Messages;
import tw.org.iii.ideas.layout.LoginHandler;
import tw.org.iii.ideas.layout.MainHandler;
import tw.org.iii.ideas.module.BarcodeScanHandler;
import tw.org.iii.ideas.module.FacebookHandler;

import com.facebook.appevents.AppEventsLogger;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class MainActivity extends Activity
{
	private MainHandler		mainHandler		= null;
	private LoginHandler	loginHandler	= null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// showLoginView();
		showMainView();
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
		FacebookHandler.callbackManager.onActivityResult(requestCode, resultCode, data);
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
		Intent openCameraIntent = new Intent(MainActivity.this, BarcodeScanHandler.class);
		// openCameraIntent.putExtra("missionindex", nMissionDataIndex);
		startActivityForResult(openCameraIntent, BarcodeScanHandler.ACTIVITY_REQUEST_CODE);
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
