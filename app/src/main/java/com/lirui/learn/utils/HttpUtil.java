package com.lirui.learn.utils;

import com.lirui.lib_common.util.FileTypeUtil;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


/**
 * 网络请求相关工具类
 */
public class HttpUtil {
	private static final String TAG = "HttpUtil";
	public static String sessionID = null;

	private static final String END = "\r\n";
	private static final String TWOHYPHENS = "--";
	private static final String BOUNDARY = "sdwanggexx";//山东网格信息

	/**
	 * 上传文件
	 * 
	 * @param path
	 *            文件上传接口
	 * @param files
	 *            文件集合(<参数名,文件路径>)
	 * @param params
	 *            其他参数集合
	 * @param encoding
	 *            编码方式
	 * @return String
	 * @throws IOException
	 */
	public static String sendPOSTWithFilesForString(String path,
			HashMap<String, String> files, HashMap<String, String> params,
			String encoding) throws HttpException {
		HttpURLConnection conn = null;
		try {
			URL url = new URL(path);
			conn = (HttpURLConnection) url.openConnection();
			// 设置每次传输的流大小，可以有效防止手机因为内存不足崩溃
			// 此方法用于在预先不知道内容长度时启用没有进行内部缓冲的 HTTP 请求正文的流。
			// 设置此参数后导致请求有时成功有时失败（网上说是服务器不支持,具体原因未明）
			// conn.setChunkedStreamingMode(0);// 128K 128 * 1024

			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + BOUNDARY);

			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Charset", encoding);
			if (sessionID != null)
				conn.setRequestProperty("Cookie", sessionID);// 设置cookie
			DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

			// 参数
			writeParams(path,dos, params, encoding);
			// 文件
			writeFiles(dos, files);
			dos.writeBytes(TWOHYPHENS + BOUNDARY + TWOHYPHENS + END);
			dos.flush();
			dos.close();
			String cookie = conn.getHeaderField("set-cookie");
			if (cookie != null)
				sessionID = cookie.substring(0, cookie.indexOf(";"));// 获取sessionID

			String data = StreamUtil.iputStreamToString(get(conn));
			if (conn != null)
				conn.disconnect();
			return data;
		} catch (Exception e) {
			if (conn != null)
				conn.disconnect();
			throw new HttpException(e);

		}

	}

	/**
	 * 处理文件集
	 * 
	 * @param dos
	 *            数据输出流
	 * @param files
	 *            文件集
	 * @throws IOException
	 */
	private static void writeFiles(DataOutputStream dos,
			HashMap<String, String> files) throws IOException {

		for (Map.Entry<String, String> entry : files.entrySet()) {
			FileInputStream fStream = null;
			try {
				dos.writeBytes(TWOHYPHENS + BOUNDARY + END);
				dos.writeBytes("Content-Disposition: form-data; " + "name=\""
						+ entry.getKey() + "\";filename=\"" + entry.getValue()
						+ "\"" + END);
				String filetype = FileTypeUtil.getFileType(entry
						.getValue());// 获取文件类型
				dos.writeBytes("Content-type: " + filetype + END);
				dos.writeBytes(END);
				int bufferSize = 1024 * 10;
				byte[] buffer = new byte[bufferSize];
				int length = -1;
				File file = new File(entry.getValue());
				fStream = new FileInputStream(file);
				while ((length = fStream.read(buffer)) != -1) {
					dos.write(buffer, 0, length);
				}
				dos.writeBytes(END);
				// dos.writeBytes(TWOHYPHENS + BOUNDARY + TWOHYPHENS + END);
			} catch (IOException e) {
				throw e;
			} finally {
				if (fStream != null)
					try {
						fStream.close();
					} catch (IOException e) {
						throw e;
					}
			}
		}
	}

	/**
	 * 处理参数集
	 * 
	 * @param dos
	 *            数据输出流
	 * @param params
	 *            参数集
	 * @param encoding
	 *            编码方式
	 * @throws IOException
	 */
	private static void writeParams(String path,DataOutputStream dos,
			HashMap<String, String> params, String encoding) throws IOException {
		StringBuilder data = new StringBuilder();
		if (params != null && !params.isEmpty()) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				// 方便查看发送参数，无实际意义
				data.append(entry.getKey()).append("=");
				data.append(entry.getValue());
				data.append("&");
				if (entry.getValue() != null) {
					dos.writeBytes(TWOHYPHENS + BOUNDARY + END);
					dos.writeBytes("Content-Disposition: form-data; "
							+ "name=\"" + entry.getKey() + "\"" + END);
					dos.writeBytes(END);
					dos.write(entry.getValue().getBytes(encoding));// writeBytes方法默认以ISO-8859-1编码,此处易出现汉字乱码问题
					dos.writeBytes(END);
					// dos.writeBytes(TWOHYPHENS + BOUNDARY + TWOHYPHENS + END);
				}
			}
			data.deleteCharAt(data.length() - 1);
		}
	}

	/**
	 * 发送POST请求
	 * 
	 * @param path
	 *            请求接口
	 * @param params
	 *            发送参数集合(<参数名,参数值>)
	 * @return String
	 * @throws HttpException
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	public static String sendPOSTForString(String path,
			HashMap<String, String> params, String encoding)
			throws HttpException {
		StringBuilder data = new StringBuilder();
		if (params != null && !params.isEmpty()) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				data.append(entry.getKey()).append("=");
				String value;
				if (entry.getValue() != null) {
					value = entry.getValue().replace("&", "%26"); // 转义&
//					value = entry.getValue().replace("+", "%2B"); // 转义&
				} else {
					value = entry.getValue();
				}
				data.append(value);
				data.append("&");
			}
			data.deleteCharAt(data.length() - 1);
		}
		HttpURLConnection conn = null;
		try {
			byte[] entity = data.toString().getBytes();
			conn = (HttpURLConnection) new URL(path).openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			// conn.setChunkedStreamingMode(0);
			conn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
			conn.setUseCaches(false);
			conn.setRequestProperty("Charset", encoding);
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length",
					String.valueOf(entity.length));
			if (sessionID != null)
				conn.setRequestProperty("Cookie", sessionID);// 设置cookie
			DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
			dos.write(entity);
			dos.flush();
			dos.close();
			String cookie = conn.getHeaderField("set-cookie");
			if (cookie != null)
				sessionID = cookie.substring(0, cookie.indexOf(";"));// 获取sessionID

			int code = conn.getResponseCode();

			InputStream in = (code == HttpURLConnection.HTTP_OK) ? conn
					.getInputStream() : null;

			String indata = StreamUtil.iputStreamToString(in);
			if (conn != null)
				conn.disconnect();
			return indata;
		} catch (Exception e) {
			if (conn != null)
				conn.disconnect();
			throw new HttpException(e);
		}
	}

	/**
	 * 获取服务器返回流
	 * 
	 * @param conn
	 *            连接
	 * @return InputStream or null
	 * @throws IOException
	 */
	private static InputStream get(HttpURLConnection conn) throws IOException {
		int code = conn.getResponseCode();
		return (code == HttpURLConnection.HTTP_OK) ? conn.getInputStream()
				: null;
	}

	/**
	 * 清除Session
	 */
	public static void clearSession() {
		sessionID = null;
	}

}
