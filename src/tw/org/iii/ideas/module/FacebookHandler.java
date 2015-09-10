/** 
 * @author Louis Ju
 * @date 2015/9/10
 * @note
 *         將FacebookHandler.callbackManager.onActivityResult(requestCode,
 *         resultCode, data); 加到 Activity的onActivityResult
 */

package tw.org.iii.ideas.module;

import java.util.Arrays;
import org.json.JSONObject;
import tw.org.iii.ideas.common.Logs;
import android.app.Activity;
import android.os.Bundle;
import android.util.SparseArray;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

public class FacebookHandler
{
	private Activity							theActivity					= null;
	public static CallbackManager				callbackManager				= CallbackManager.Factory.create();
	private AccessToken							accessToken;
	private LoginManager						loginManager;
	private SparseArray<OnFacebookLoginResult>	listOnFacebookLoginResult	= null;

	public FacebookHandler(Activity activity)
	{
		theActivity = activity;
		listOnFacebookLoginResult = new SparseArray<OnFacebookLoginResult>();
	}

	@Override
	protected void finalize() throws Throwable
	{
		listOnFacebookLoginResult.clear();
		listOnFacebookLoginResult = null;
		super.finalize();
	}

	public void init()
	{
		FacebookSdk.sdkInitialize(theActivity.getApplicationContext());
		loginManager = LoginManager.getInstance();
		loginManager.registerCallback(callbackManager, facebookCallback);
	}

	/**
	 * Facebook result callback.
	 */
	public static interface OnFacebookLoginResult
	{
		void onLoginResult(final String strFBID, final String strName, final String strError);
	}

	public void setOnFacebookLoginResultListener(FacebookHandler.OnFacebookLoginResult listener)
	{
		if (null != listener)
		{
			listOnFacebookLoginResult.put(listOnFacebookLoginResult.size(), listener);
		}
	}

	private void callbackFacebookResult(final String strFBID, final String strName, final String strError)
	{
		for (int i = 0; i < listOnFacebookLoginResult.size(); ++i)
		{
			listOnFacebookLoginResult.get(i).onLoginResult(strFBID, strName, strError);
		}
	}

	/**
	 * Show Facebook Login Activity.
	 */
	public void login()
	{
		if (null == theActivity || null == callbackManager || null == loginManager)
			return;
		loginManager.logInWithReadPermissions(theActivity,
				Arrays.asList("email", "public_profile", "user_birthday", "user_likes", "user_location"));
	}

	private void callGraph(final AccessToken strToken)
	{
		if (null != strToken && !strToken.isExpired())
		{
			Logs.showTrace("Call Facebook Graph API");
			GraphRequest request = GraphRequest.newMeRequest(strToken, new GraphRequest.GraphJSONObjectCallback()
			{
				@Override
				public void onCompleted(JSONObject object, GraphResponse response)
				{
					Logs.showTrace("Facebook ID:" + object.optString("id"));
					Logs.showTrace("Facebook Name:" + object.optString("name"));
					Logs.showTrace("Facebook Link:" + object.optString("link"));
					callbackFacebookResult(object.optString("id"), object.optString("name"), null);
				}

			});

			Bundle parameters = new Bundle();
			parameters.putString("fields", "id,name,link");
			request.setParameters(parameters);
			request.executeAsync();
		}
	}

	private FacebookCallback<LoginResult>	facebookCallback	= new FacebookCallback<LoginResult>()
																{

																	@Override
																	public void onSuccess(LoginResult loginResult)
																	{
																		Logs.showTrace("Facebook Login Success");
																		accessToken = loginResult.getAccessToken();
																		callGraph(accessToken);
																	}

																	@Override
																	public void onCancel()
																	{
																		Logs.showTrace("Facebook Login Cancel");
																		callbackFacebookResult(null, null,
																				"Facebook Login Cancel");
																	}

																	@Override
																	public void onError(FacebookException error)
																	{
																		Logs.showTrace("Facebook Exception:"
																				+ error.toString());
																		callbackFacebookResult(null, null,
																				error.toString());
																	}

																};

}
