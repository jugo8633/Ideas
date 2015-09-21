package tw.org.iii.ideas.layout;

import tw.org.iii.ideas.R;
import tw.org.iii.ideas.common.Messages;
import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainHandler extends BaseHandler
{

	Button btnBarCodeScan = null;

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
		initItem();
	}

	private void initItem()
	{
		btnBarCodeScan = (Button) getViewById(R.id.buttonBarcodeScan);
		btnBarCodeScan.setOnClickListener(itemClickListener);
	}

	private OnClickListener itemClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			switch (v.getId())
			{
				case R.id.buttonBarcodeScan:
					postMsg(Messages.MSG_SHOW_QR_SCANNER, 0, 0, null);
					break;
			}
		}
	};
}
