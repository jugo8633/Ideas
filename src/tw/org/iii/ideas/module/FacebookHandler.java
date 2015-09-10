package tw.org.iii.ideas.module;

import java.util.Arrays;

import android.app.Activity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

public class FacebookHandler
{
	private Activity		theActivity	= null;
	private CallbackManager	callbackManager;
	private AccessToken		accessToken;

	public FacebookHandler(Activity activity)
	{
		theActivity = activity;
	}

	public void init()
	{
		FacebookSdk.sdkInitialize(theActivity.getApplicationContext());
		callbackManager = CallbackManager.Factory.create();
	}

	public void show()
	{
		if (null == theActivity || null == callbackManager)
			return;
		LoginManager.getInstance().logInWithReadPermissions(theActivity,
				Arrays.asList("public_profile", "user_friends"));
	}
}
