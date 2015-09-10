package tw.org.iii.ideas.layout;

import tw.org.iii.ideas.common.Type;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;

public abstract class BaseHandler
{
	protected Activity	theActivity		= null;
	protected Handler	theHandler		= null;
	protected View		theView			= null;
	private int			mnResourceId	= Type.INVALID;

	public BaseHandler(Activity activity, Handler handler)
	{
		theActivity = activity;
		theHandler = handler;
	}

	abstract public void init();

	abstract protected void onShow();

	public void show()
	{
		if (null != theActivity && Type.INVALID != mnResourceId)
		{
			theActivity.setContentView(createView(mnResourceId));
			onShow();
		}
	}

	protected void setLayoutResource(final int nResourceId)
	{
		mnResourceId = nResourceId;
	}

	protected View createView(final int nResourceId)
	{
		theView = null;
		if (null != theActivity)
		{
			LayoutInflater inflater = (LayoutInflater) theActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (null != inflater)
			{
				theView = inflater.inflate(nResourceId, null);
			}
		}
		return theView;
	}

	protected View getViewById(final int nResourceId)
	{
		if (null == theView)
			return null;
		return theView.findViewById(nResourceId);
	}

	protected void sendMsg(int nWhat, int nArg1, int nArg2, Object obj)
	{
		if (null != theHandler)
		{
			Message msg = new Message();
			msg.what = nWhat;
			msg.arg1 = nArg1;
			msg.arg2 = nArg2;
			msg.obj = obj;
			theHandler.sendMessage(msg);
		}
	}

	protected void postMsg(int nWhat, int nArg1, int nArg2, Object obj)
	{
		if (null != theHandler)
		{
			Thread t = new Thread(new postMsgRunnable(nWhat, nArg1, nArg2, obj));
			t.start();
		}
	}

	class postMsgRunnable implements Runnable
	{
		private Message	message	= null;

		@Override
		public void run()
		{
			if (null == message)
				return;
			theHandler.sendMessage(message);
		}

		public postMsgRunnable(int nWhat, int nArg1, int nArg2, Object obj)
		{
			message = new Message();
			message.what = nWhat;
			message.arg1 = nArg1;
			message.arg2 = nArg2;
			message.obj = obj;
		}
	}
}
