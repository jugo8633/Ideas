package tw.org.iii.ideas.layout;

import tw.org.iii.ideas.R;
import android.app.Activity;
import android.os.Handler;

public class MainHandler extends BaseHandler
{

	public MainHandler(Activity activity, Handler handler)
	{
		super(activity, handler);
	}

	@Override
	public void init()
	{
		setLayoutResource(R.layout.activity_main);
	}

	@Override
	protected void onShow()
	{
		// TODO Auto-generated method stub
		
	}
}
