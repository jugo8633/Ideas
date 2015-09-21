package tw.org.iii.ideas.module;

import android.app.Activity;
import android.os.Bundle;
import tw.org.iii.ideas.R;

public class BarcodeScanHandler extends Activity
{
	public static final int ACTIVITY_REQUEST_CODE = 0x0003;

	public BarcodeScanHandler()
	{

	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.barcode_scan);
	}

}
