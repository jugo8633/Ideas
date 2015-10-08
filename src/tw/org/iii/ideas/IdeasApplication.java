package tw.org.iii.ideas;

import java.util.HashMap;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import android.app.Application;

public class IdeasApplication extends Application
{
	private static final String	PROPERTY_ID		= "UA-67610675-1";
	public static int			GENERAL_TRACKER	= 0;

	public enum TrackerName
	{
		APP_TRACKER, // Tracker used only in this app.
		GLOBAL_TRACKER // Tracker used by all the apps from a company.
	}

	HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

	public IdeasApplication()
	{
		super();
	}

	synchronized Tracker getTracker(TrackerName trackerId)
	{
		if (!mTrackers.containsKey(trackerId))
		{

			GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
			Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(R.xml.app_tracker)
					: analytics.newTracker(PROPERTY_ID);
			mTrackers.put(trackerId, t);

		}
		return mTrackers.get(trackerId);
	}

}
