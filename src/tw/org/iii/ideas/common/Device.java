package tw.org.iii.ideas.common;

import java.io.IOException;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

public abstract class Device
{
	public static int getSdkVer()
	{
		int sdkInt;
		try
		{
			sdkInt = android.os.Build.VERSION.SDK_INT;
		}
		catch (NumberFormatException nfe)
		{
			sdkInt = 10000;
		}
		Logs.showTrace("Android SDK: " + String.valueOf(sdkInt));
		return sdkInt;
	}

	public static int getWidth(Context context)
	{
		int width = 0;
		width = getScreenResolution(context).x;
		return width;
	}

	public static int getHeight(Context context)
	{
		int height = 0;
		height = getScreenResolution(context).y;
		return height;
	}

	public static Point getScreenResolution(Context context)
	{
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		return size;
	}

	public static String getAAID(Activity activity)
	{
		String aaid = "";

		try
		{
			aaid = AdvertisingIdClient.getAdvertisingIdInfo(activity).getId();

		}
		catch (IOException e)
		{
			// Signals connection to Google Play Services failed.
		}
		catch (IllegalStateException e)
		{
			// Indicates this method was called on the main thread.
		}
		catch (GooglePlayServicesRepairableException e)
		{
			// Indicates that Google Play is not installed on this device.
		}
		catch (GooglePlayServicesNotAvailableException e)
		{
			// Indicates that there was a recoverable error connecting to Google
			// Play Services.
		}

		return aaid;
	}

	public static void getGaid(final Activity activity)
	{
		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				try
				{
					String gaid = AdvertisingIdClient.getAdvertisingIdInfo(activity.getApplicationContext()).getId();
					if (gaid != null)
					{
						Logs.showTrace("AAID:" + gaid);
						// gaid get!
					}
				}
				catch (IllegalStateException e)
				{
					e.printStackTrace();
				}
				catch (GooglePlayServicesRepairableException e)
				{
					e.printStackTrace();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				catch (GooglePlayServicesNotAvailableException e)
				{
					e.printStackTrace();
				}
			}
		}).start();
	}
}
