package tw.org.iii.ideas.module;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import tw.org.iii.ideas.R;
import tw.org.iii.ideas.common.Logs;

import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQAuth;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class TencentHandler
{
	public static QQAuth	mQQAuth;
	private UserInfo		mInfo;
	private Tencent			mTencent;
	private Activity		theActivity	= null;

	public TencentHandler(Activity activity)
	{
		theActivity = activity;
	}

	public void init()
	{
		mQQAuth = QQAuth.createInstance(theActivity.getString(R.string.tencent_app_id),
				theActivity.getApplicationContext());
		mTencent = Tencent.createInstance(theActivity.getString(R.string.tencent_app_id), theActivity);
	}

	public void login()
	{
		if (!mQQAuth.isSessionValid())
		{
			IUiListener listener = new BaseUiListener()
			{
				@Override
				protected void doComplete(JSONObject values)
				{
					Logs.showTrace("QQ Login Complete: " + values.toString());
					updateUserInfo();
					if (null != values)
					{
						try
						{
							String qq_openid = values.getString("openid");
							Logs.showTrace("QQ Open Id: " + qq_openid);
							return;
						}
						catch (JSONException e)
						{
							e.printStackTrace();
						}
					}
				}
			};
			mQQAuth.login(theActivity, "all", listener);
			mTencent.login(theActivity, "all", listener);
			Logs.showTrace("QQ Login");
		}
		else
		{
			logout();
		}

	}

	public void logout()
	{
		mQQAuth.logout(theActivity);
		updateUserInfo();
		Logs.showTrace("QQ Logout");
	}

	private class BaseUiListener implements IUiListener
	{

		@Override
		public void onComplete(Object response)
		{
			doComplete((JSONObject) response);
		}

		protected void doComplete(JSONObject values)
		{

		}

		@Override
		public void onError(UiError e)
		{
			Logs.showTrace("QQ Error: " + e.errorDetail);
		}

		@Override
		public void onCancel()
		{
			Logs.showTrace("QQ Cancel");
		}
	}

	private void updateUserInfo()
	{
		if (mQQAuth != null && mQQAuth.isSessionValid())
		{
			IUiListener listener = new IUiListener()
			{
				@Override
				public void onError(UiError e)
				{

				}

				@Override
				public void onComplete(final Object response)
				{
					Logs.showTrace("updateUserInfo QQ Response: " + response.toString());
					new Thread()
					{
						@Override
						public void run()
						{
							JSONObject json = (JSONObject) response;
							if (json.has("nickname"))
							{
								try
								{
									String qq_nickname = json.getString("nickname");
								}
								catch (JSONException e)
								{
									e.printStackTrace();
								}
							}

							if (json.has("figureurl"))
							{
								try
								{
									Bitmap qq_picture = getbitmap(json.getString("figureurl_qq_2"));
								}
								catch (JSONException e)
								{
									e.printStackTrace();
								}
							}
						}

					}.start();
				}

				@Override
				public void onCancel()
				{
				}
			};
			mInfo = new UserInfo(theActivity, mQQAuth.getQQToken());
			mInfo.getUserInfo(listener);
		}
		else
		{

		}
	}

	private Bitmap getbitmap(String imageUri)
	{
		Bitmap bitmap = null;
		try
		{
			URL myFileUrl = new URL(imageUri);
			HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}
}
