package tw.org.iii.ideas.layout;

import tw.org.iii.ideas.R;
import tw.org.iii.ideas.common.Logs;
import tw.org.iii.ideas.module.FacebookHandler;
import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class LoginHandler extends BaseHandler
{
	private FacebookHandler	facebook	= null;
	private TextView		btnFacebook	= null;

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
		facebook.show();
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
														}
													}
												};
}
