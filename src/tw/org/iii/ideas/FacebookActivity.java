package tw.org.iii.ideas;

import tw.org.iii.ideas.layout.LoginHandler;

import com.facebook.FacebookSdk;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

public class FacebookActivity extends Activity
{

	private LoginHandler	loginHandler	= null;

	public FacebookActivity()
	{

	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		FacebookSdk.sdkInitialize(getApplicationContext());

//		showLoginView();
	}

	private void showLoginView()
	{
		loginHandler = new LoginHandler(this, theHandler);
		loginHandler.init();
		loginHandler.show();
	}

	private Handler	theHandler	= new Handler()
								{
								};
								
	
}
