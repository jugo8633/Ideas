package tw.org.iii.ideas.common;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.apache.http.protocol.HTTP;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public abstract class Utility
{
	public static boolean isValidStr(String strStr)
	{
		if (null != strStr && 0 < strStr.trim().length())
			return true;
		return false;
	}

	public static String convertNull(String strStr)
	{
		String strValue = strStr;
		if (null == strValue)
		{
			strValue = "";
		}
		return strValue;
	}

	/** convert null string to default string **/
	public static String convertNull(String strStr, String strDefault)
	{
		String strValue = strStr;

		if (!isValidStr(strStr))
		{
			strValue = strDefault;
		}

		return strValue;
	}

	/** Check String is numeric data type **/
	public static boolean isNumeric(String str)
	{
		if (!isValidStr(str))
			return false;
		return str.matches("[-+]?\\d*\\.?\\d+");
	}

	public static String UrlEncode(final String strText)
	{
		try
		{
			return URLEncoder.encode(convertNull(strText), HTTP.UTF_8);
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static boolean checkInternet(Context conetxt)
	{
		boolean bValid = true;
		ConnectivityManager conManager = (ConnectivityManager) conetxt.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networInfo = conManager.getActiveNetworkInfo();

		if (null == networInfo || !networInfo.isAvailable())
		{
			bValid = false;
		}

		return bValid;
	}

	public static String getTime()
	{
		SimpleDateFormat formatter = null;
		formatter = new SimpleDateFormat("yyyyMMdd HH:mm:ss", Locale.getDefault());
		Date curDate = new Date(System.currentTimeMillis());
		return formatter.format(curDate);
	}

	public static String getTime(final String strFormat)
	{
		SimpleDateFormat formatter = null;
		formatter = new SimpleDateFormat(strFormat, Locale.getDefault());
		Date curDate = new Date(System.currentTimeMillis());
		return formatter.format(curDate);
	}

	public static String md5(String string)
	{
		byte[] hash;
		try
		{
			hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
		}
		catch (NoSuchAlgorithmException e)
		{
			throw new RuntimeException("Huh, MD5 should be supported?", e);
		}
		catch (UnsupportedEncodingException e)
		{
			throw new RuntimeException("Huh, UTF-8 should be supported?", e);
		}

		StringBuilder hex = new StringBuilder(hash.length * 2);
		for (byte b : hash)
		{
			if ((b & 0xFF) < 0x10)
				hex.append("0");
			hex.append(Integer.toHexString(b & 0xFF));
		}
		return hex.toString();
	}
}
