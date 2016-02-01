package jazz.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class HttpClient {
	private int connectionTimeout = 0;
	private int readTimeout = 0;
	private String contentType;
	private String charset;
	private int responseCode;
	private String responseMessage;
	
	public int getConnectionTimeout() {
		return connectionTimeout;
	}
	
	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}
	
	public int getReadTimeout() {
		return readTimeout;
	}
	
	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}
	
	public int getResponseCode() {
		return responseCode;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public InputStream head(String url) {
		return request(url, null, "HEAD");
	}
	
	public InputStream get(String url) {
		return request(url, null, "GET");
	}
	
	public InputStream put(String url, String data) {
		return request(url, data, "PUT");
	}
	
	public InputStream post(String url, String data) {
		return request(url, data, "POST");
	}

	public InputStream delete(String url, String data) {
		return request(url, data, "DELETE");
	}
	
	private InputStream request(String url, String data, String method) {
		OutputStream outputStream = null;
		InputStream inputStream = null;
		HttpURLConnection conn = null;
		try {
			URL urlObject = new URL(url);
			conn = (HttpURLConnection)urlObject.openConnection();
			conn.setRequestMethod(method);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			if (connectionTimeout > 0) {
				conn.setConnectTimeout(connectionTimeout);
			}
			if (readTimeout > 0) {
				conn.setReadTimeout(readTimeout);
			}
			
			if (data != null && data.length() > 0) {
				conn.setRequestProperty("Content-Type", String.format("%s;charset=%s", contentType, charset));
				conn.addRequestProperty("Content-Length", Integer.toString(data.getBytes().length));
				outputStream = conn.getOutputStream();
				outputStream.write(data.getBytes(charset));
				outputStream.flush();
			}
			
			//判断返回编码
			responseCode = conn.getResponseCode();
			if (responseCode < 200 || responseCode >= 300) {
				try {outputStream.close();} catch (Exception e) {}
				responseMessage = conn.getResponseMessage();
				throw new RuntimeException(String.format("error when http request, code: %s, message: %s", responseCode, responseMessage));
			}
			
			//读取Web返回结果
			Map<String, String> headers = getHeaders(conn);
			inputStream = conn.getInputStream();
			
			charset = getCharsetFromHeaders(headers);
			contentType = getContentTypeFromHeader(headers); 
			
			return inputStream;
		} catch (MalformedURLException e) {
			throw new RuntimeException("error when http request", e);
		} catch (IOException e) {
			throw new RuntimeException("error when http request", e);
		} finally {
			//try {inputStream.close();} catch (Exception e) {}
			try {outputStream.close();} catch (Exception e) {}
			//conn.disconnect();
		}
	}
	
	private String getContentTypeFromHeader(Map<String, String> headers) {
		String ct = "text/plain";
		if (headers.containsKey("Content-Type")) {
			ct = headers.get("Content-Type");
			if (ct != null && ct.contains(";")) {
				ct = ct.substring(0, ct.indexOf(';'));
			}
		}
		return ct;
	}
	
	private String getCharsetFromHeaders(Map<String, String> headers) {
		String charset = "utf-8";
		
		if (!headers.containsKey("Content-Type")) {
			return charset;
		}
		
		String contentType = headers.get("Content-Type");
		if (contentType != null) {
			String[] parts = contentType.split(";");
			for (String part : parts) {
				String[] keyValue = part.split("=");
				if (keyValue.length > 1) {
					String key = keyValue[0].trim();
					String value = keyValue[1].trim();
					if (key.equals("charset")) {
						charset = value;
						break;
					}
				}
			}
		}
	
		//这里最好是使用标准charset检查，防止charset错误
		//不过不检查也没关系，请求字符串写错了是客户端的职责，服务端不检查也是正常的，只需要有异常抛出
		return charset;
	}
	
	private Map<String, String> getHeaders(URLConnection connection) {
		Map<String, String> headers = new HashMap<String, String>();
		String key;
		int i = 1;
		while ((key = connection.getHeaderFieldKey(i)) != null) {
			String value = connection.getHeaderField(i); 
			headers.put(key, value);
			
			i++;
		}
		return headers;
	}
}