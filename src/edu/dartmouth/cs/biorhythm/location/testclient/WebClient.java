package edu.dartmouth.cs.biorhythm.location.testclient;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import ch.boye.httpclientandroidlib.Consts;
import ch.boye.httpclientandroidlib.Header;
import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.HttpStatus;
import ch.boye.httpclientandroidlib.NameValuePair;
import ch.boye.httpclientandroidlib.client.ClientProtocolException;
import ch.boye.httpclientandroidlib.client.HttpClient;
import ch.boye.httpclientandroidlib.client.entity.UrlEncodedFormEntity;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;
import ch.boye.httpclientandroidlib.client.methods.HttpPost;
import ch.boye.httpclientandroidlib.client.methods.HttpUriRequest;
import ch.boye.httpclientandroidlib.client.params.ClientPNames;
import ch.boye.httpclientandroidlib.client.params.CookiePolicy;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;
import ch.boye.httpclientandroidlib.params.HttpConnectionParams;
import ch.boye.httpclientandroidlib.util.EntityUtils;

public class WebClient {

	private DefaultHttpClient mHttpClient;

	public WebClient() {
		mHttpClient = new DefaultHttpClient();

		mHttpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY,
				CookiePolicy.BROWSER_COMPATIBILITY);
		mHttpClient.getParams().setParameter(
				HttpConnectionParams.CONNECTION_TIMEOUT, 1000);
		mHttpClient.getParams().setParameter(HttpConnectionParams.SO_TIMEOUT,
				1000);
		mHttpClient.getParams().setParameter(HttpConnectionParams.SO_KEEPALIVE,
				true);
	}

	// post method
	public WebResponse post(String url, List<NameValuePair> parameters)
			throws ClientProtocolException, IOException {
		WebResponse webResponse = new WebResponse();

		HttpResponse response;
		HttpEntity entity;

		HttpPost httpost = new HttpPost(url);
		List<NameValuePair> nvps;
		if (parameters == null) {
			nvps = new ArrayList<NameValuePair>();
		} else {
			nvps = parameters;
		}
		httpost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));

		response = sendRequest(mHttpClient, httpost);
		entity = response.getEntity();

		webResponse.mContent = getResponseContent(entity);
		webResponse.mStatusCode = response.getStatusLine().getStatusCode();
		EntityUtils.consume(entity);

		return webResponse;
	}

	// post method
	public WebResponse get(String url) throws ClientProtocolException,
			IOException {
		WebResponse webResponse = new WebResponse();

		HttpResponse response;
		HttpEntity entity;

		HttpGet httpGet = new HttpGet(url);

		response = sendRequest(mHttpClient, httpGet);
		entity = response.getEntity();

		webResponse.mContent = getResponseContent(entity);
		webResponse.mStatusCode = response.getStatusLine().getStatusCode();
		EntityUtils.consume(entity);

		return webResponse;
	}

	private String getResponseContent(HttpEntity entity) {
		byte[] buf = new byte[2 * 1024 * 1024];
		StringBuilder builder = new StringBuilder();
		int len = 0;
		try {
			len = entity.getContent().read(buf);
			while (len > 0) {
				builder.append(new String(buf, 0, len));
				len = entity.getContent().read(buf);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return builder.toString();
	}

	private HttpResponse sendRequest(HttpClient httpclient,
			HttpUriRequest request) throws ClientProtocolException, IOException {
		HttpResponse response = httpclient.execute(request);
		int respCode = response.getStatusLine().getStatusCode();
		if (respCode == HttpStatus.SC_MOVED_TEMPORARILY
				|| respCode == HttpStatus.SC_MOVED_PERMANENTLY) {
			Header[] headers = response.getAllHeaders();
			String location = null;
			for (Header header : headers) {
				if (header.getName().equals("Location")) {
					String redirUrl = header.getValue();
					if (redirUrl.startsWith("https://")
							|| redirUrl.startsWith("http://")) {
						location = header.getValue();
					} else {
						URL url = new URL(new URL(request.getURI()
								.toASCIIString()), redirUrl);
						location = url.toString();
					}
					break;
				}
			}

			if (location != null) {
				EntityUtils.consume(response.getEntity());
				HttpGet httpget = new HttpGet(location);
				response = sendRequest(httpclient, httpget);
			}
		}

		return response;
	}

	public class WebResponse {
		public int mStatusCode;
		public String mContent;
	}
}
