package com.extensivepro.mxl.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class HttpUtil {

	/**
	 * 连接服务器 对传入的url参数进行处理完成服务器的连接
	 * 
	 * @param url
	 *            连接字符串
	 * @return HttpURLConnection 服务连接
	 * @see [类、类#方法、类#成员]
	 */
	public static HttpURLConnection connectServer(String url) {

		if (url == null || url.length() == 0) {
			return null;
		}
		// 通过url，开启对服务器的连接
		HttpURLConnection httpConn = null;
		try {
			URL urls = new URL(url);
			// 打开连接，等待读取信息
			httpConn = (HttpURLConnection) urls.openConnection();
		} catch (UnknownHostException e) {
			return null;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return httpConn;
	}

	/**
	 * 获取已经连接的服务器端的内容
	 * 
	 * @param hc
	 *            HttpURLConnection
	 * @return byte[]
	 * @see [类、类#方法、类#成员]
	 */
	public static String readFromServer(HttpURLConnection hc) {
		// 创建一个输入流接收服务器端数据
		InputStream is = null;
		String str = "";
		boolean gerror = false;
		try {
			hc.setRequestMethod("POST");
			hc.setRequestProperty("Content-Type", "text/xml");
			hc.setConnectTimeout(30000);
			hc.setReadTimeout(30000);
			// hc.setRequestProperty("Charset", "UTF-8");
			// Log.d(TAG, hc.set);
			// int length=hc.getContentLength();
			int rc = hc.getResponseCode();
			Log.i("HttpConnector", "ResponseCode :" + rc);
			// dis = hc.openInputStream();
			if (rc != HttpURLConnection.HTTP_OK) {
				gerror = true;
				Log.i("HttpConnector-sendToServer()-363", "ResponseCode :" + rc);

			} else {
				is = hc.getInputStream();
				str = convertStreamToString2(is);
			}

		} catch (IOException e) {
			e.printStackTrace();
			Log.e("HttpConnector-readFromServer()", e.getMessage()
					+ " _-exception");
			gerror = true;
			return "err";
		} finally {
			try {
				if (null != is) {
					is.close();
				}

			} catch (IOException e) {
				Log.e("HttpConnector-readGroupInfoFromServer()", e.getMessage()
						+ " _exception");
				gerror = true;
			} finally {
				if (gerror) {
					is = null;
				}
				// 关闭网络连接
				closeHttpConnection(hc);
			}
		}

		return str;
	}

	/**
	 * InputStream数据转换为string
	 * 
	 * @param is
	 *            数据流
	 * @return 字符串
	 * @return
	 */
	public static String convertStreamToString2(InputStream is) {

		ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
		try {
			while ((len = is.read(buffer)) != -1) {
				outSteam.write(buffer, 0, len);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				outSteam.close();
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		if (new String(outSteam.toByteArray()).equals("err")) {
			return "err";
		}
		try {
			return new String(outSteam.toByteArray(), "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "err";
	}

	/**
	 * 关闭连接
	 * 
	 * @param httpConn
	 *            httpConn
	 * @see [类、类#方法、类#成员]
	 */
	public static void closeHttpConnection(HttpURLConnection httpConn) {
		if (httpConn == null) {
			return;
		} else {
			// 关闭与服务器的连接
			httpConn.disconnect();
		}
	}

	/**
	 * 获取已经连接的服务器端的内容（带有请求内容的）
	 * 
	 * @param hc
	 * @param upData
	 *            请求的内容
	 * @return 已经连接的服务器端返回的内容
	 * @see [类、类#方法、类#成员]
	 */
	public static String readFromServer(HttpURLConnection hc, String upData) {
		// 创建一个输入流接收服务器端数据
		InputStream dis = null;

		byte[] buf = null;
		boolean error = false;
		OutputStream dos = null;
		String str = "";
		if (hc == null) {

			return str;
		}
		try {
			// 设置http头
			hc.setRequestMethod("POST");
			// hc.setRequestProperty("Content-Type", "text/xml");
			// hc.setRequestProperty("Charset", "UTF-8");
			hc.setConnectTimeout(30000);
			hc.setReadTimeout(30000);

			hc.setDoInput(true);
			hc.setDoOutput(true);

			hc.setUseCaches(false);
			hc.setInstanceFollowRedirects(true);

			dos =  hc.getOutputStream();
			// 写入客户端需要发送的数据
			 dos.write(upData.getBytes());
//			dos.write(upData.getBytes("utf-8"));

			
			// 响应码
//			int rc = 0;

//			rc = hc.getResponseCode();
			// String message=hc.
			// Log.d("", message);
//			if (rc != HttpURLConnection.HTTP_OK) {
//				error = true;

//			} else {
				dis = hc.getInputStream();
				str = convertStreamToString(dis);
//			}
				
				dos.flush();
				dos.close();
		} catch (IOException e) {
			error = true;
		} finally {
			try {
				if (null != dis) {
					dis.close();
				}
			} catch (IOException e) {
				error = true;
			} finally {
				if (error) {
					dis = null;
					dos = null;
				}
				// 关闭网络连接
				closeHttpConnection(hc);
			}
		}
		return str;
	}

	/**
	 * InputStream数据转换为string
	 * 
	 * @param is
	 *            数据流
	 * @return 字符串
	 */
	public static String convertStreamToString(InputStream is) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		StringBuilder sb = new StringBuilder();
		String line = null;

		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + " ");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			try {
				is.close();
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	/**
	 * 获取已经连接的服务器端的内容
	 * 
	 * @param hc
	 *            HttpURLConnection
	 * @return byte[]
	 * @see [类、类#方法、类#成员]
	 */
	public static InputStream getPicture(HttpURLConnection hc) {
		// 创建一个输入流接收服务器端数据
		InputStream is = null;
		Bitmap bitmap = null;
		boolean gerror = false;
		try {
			hc.setRequestMethod("POST");
			hc.setRequestProperty("Content-Type", "text/xml");
			hc.setConnectTimeout(30000);
			hc.setReadTimeout(30000);
			int rc = hc.getResponseCode();
			Log.i("HttpConnector - getpicture", "ResponseCode :" + rc);
			if (rc != HttpURLConnection.HTTP_OK) {
				gerror = true;
				Log.i("HttpConnector-sendToServer()-picture", "ResponseCode :"
						+ rc);
			} else {
				is = hc.getInputStream();
				// bitmap = BitmapFactory.decodeStream(is);
			}

		} catch (IOException e) {
			e.printStackTrace();
			Log.e("HttpConnector-readFromServer()-picture", e.getMessage()
					+ " _-exception");
			gerror = true;
			return null;
		} finally {
			if (gerror) {
				is = null;
			}

		}
		return is;
	}

	/**
	 * 通过httpurl读取数据
	 * @param strUrl
	 * @return
	 * @throws Exception
	 */
	public static String questDataAsString(String strUrl, String data) throws Exception {
		String returnData = null;
		HttpURLConnection con = connectServer(strUrl);
		returnData = readFromServer(con, data);
		closeHttpConnection(con);
		return returnData;
	}
	/**
	 * 通过httpurl读取数据
	 * @param strUrl
	 * @return
	 * @throws Exception
	 */
	public static String questDataAsString(String strUrl) throws Exception {
		String returnData = null;
		HttpURLConnection con = connectServer(strUrl);
		returnData = readFromServer(con);
		closeHttpConnection(con);
		return returnData;
	}
	
	/**
	 * 获取网络的图片
	 * @param path 图片url地址 
	 * @return
	 */
		public static Bitmap getBitmap(String path){
			try {
				URL url = new URL(path);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				InputStream is = conn.getInputStream();
				return BitmapFactory.decodeStream(is);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		
}
