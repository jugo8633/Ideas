package tw.org.iii.ideas.layout;

import tw.org.iii.ideas.FacebookActivity;
import tw.org.iii.ideas.R;
import tw.org.iii.ideas.common.Logs;
import tw.org.iii.ideas.module.FacebookHandler;
import android.app.Activity;
import android.content.Intent;
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
		else
		{
			Logs.showError("###########################");
		}
	}

	private void showFacebookLogin()
	{
		if (null == theActivity)
			return;
		Logs.showTrace("Facebook Login Start");
		facebook = new FacebookHandler(theActivity);
		facebook.init();
		facebook.show();

		// Intent intent = new Intent();
		// intent.setClass(theActivity, FacebookActivity.class);
		// theActivity.startActivity(intent);
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
