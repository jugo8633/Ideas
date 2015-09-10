package tw.org.iii.ideas.layout;

import tw.org.iii.ideas.R;
import tw.org.iii.ideas.common.Logs;
import tw.org.iii.ideas.module.FacebookHandler;
import tw.org.iii.ideas.module.TencentHandler;
import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class LoginHandler extends BaseHandler
{
	private FacebookHandler	facebook	= null;
	private TencentHandler	tencent		= null;
	private TextView		btnFacebook	= null;
	private TextView		btnTencent	= null;

	public LoginHandler(Activity activity, Handler handler)
	{
		super(activity, handler);
	}

	@Override
	public void init()
	{
		setLayoutResource(R.layout.login);
	}

	@Override
	protected void onShow()
	{
		btnFacebook = (TextView) getViewById(R.id.textViewLoginFacebook);
		if (null != btnFacebook)
		{
			btnFacebook.setOnClickListener(itemClickListener);
		}

		btnTencent = (TextView) getViewById(R.id.textViewLoginTencent);
		if (null != btnTencent)
		{
			btnTencent.setOnClickListener(itemClickListener);
		}
	}

	private void showFacebookLogin()
	{
		if (null == theActivity)
			return;
		Logs.showTrace("Facebook Login Start");
		facebook = new FacebookHandler(theActivity);
		facebook.init();
		facebook.setOnFacebookLoginResultListener(new FacebookHandler.OnFacebookLoginResult()
		{
			@Override
			public void onLoginResult(String strFBID, String strName, String strError)
			{
				Logs.showTrace("Login Facebook: " + strFBID + " " + strName + " " + strError);
			}
		});
		facebook.login();
	}

	private void showTencentLogin()
	{
		if (null == theActivity)
			return;
		Logs.showTrace("Tencent Login Start");
		tencent = new TencentHandler(theActivity);
		tencent.init();
		tencent.setOnTencentLoginResultListener(new TencentHandler.OnTencentLoginResult()
		{
			@Override
			public void onLoginResult(String strOpenID, String strToken, String strError)
			{
				Logs.showTrace("Login Tencent: " + strOpenID + " " + strToken + " " + strError);
			}
		});
		tencent.setOnTencentUserInfoListener(new TencentHandler.OnTencentUserInfoListener()
		{
			@Override
			public void onUserInfo(String strNickname, String strPhotoURL, String strError)
			{
				Logs.showTrace("User Tencent: " + strNickname + " " + strPhotoURL + " " + strError);
			}
		});
		tencent.login();
	}

	private OnClickListener	itemClickListener	= new OnClickListener()
												{
													@Override
													public void onClick(View v)
													{
														switch (v.getId())
														{
															case R.id.textViewLoginFacebook:
																showFacebookLogin();
																break;
															case R.id.textViewLoginTencent:
																showTencentLogin();
																break;
														}
													}
												};
}
