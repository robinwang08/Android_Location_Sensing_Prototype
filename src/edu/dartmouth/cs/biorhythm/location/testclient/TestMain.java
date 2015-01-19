package edu.dartmouth.cs.biorhythm.location.testclient;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import ch.boye.httpclientandroidlib.NameValuePair;
import ch.boye.httpclientandroidlib.message.BasicNameValuePair;
import edu.dartmouth.cs.biorhythm.location.data.LocationInfo;

public class TestMain {

	private String username = "u60@bio";
	private String password = "u60u60";
	private WebClient mWebClient = new WebClient();
	private final String uploadUrl = "http://127.0.0.1:8080/BioSurveyServer/UploadLocation.do";
	private final String downloadUrl = "http://127.0.0.1:8080/BioSurveyServer/GetLocationInfo.do";

	public void uploadLocation(Double lat, Double lng, String names,
			String types, String whos, String activities) {

		JSONArray jsonArray = new JSONArray();

		LocationInfo location = new LocationInfo();
		location.username = username;
		location.latitude = lat;
		location.longitude = lng;
		location.name = names;
		location.type = types;
		location.who = whos;
		location.activity = activities;
		jsonArray.put(location.toJson());

		String uploadJson = jsonArray.toString();

		System.out.println("upload: \n" + uploadJson);

		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("username", username));
		parameters.add(new BasicNameValuePair("password", password));
		parameters
				.add(new BasicNameValuePair("location_list_json", uploadJson));

		try {
			WebClient.WebResponse response = mWebClient.post(uploadUrl,
					parameters);
			System.out.println("upload result: code=" + response.mStatusCode
					+ ", content=" + response.mContent);
		} catch (Exception e) {
			System.err.println("upload err: " + e.getMessage());
		}
	}

	public void downloadLocation() {
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("username", username));
		parameters.add(new BasicNameValuePair("password", password));

		try {
			WebClient.WebResponse response = mWebClient.post(downloadUrl,
					parameters);
			System.out.println("download result: code=" + response.mStatusCode
					+ ", content=" + response.mContent);
		} catch (Exception e) {
			System.err.println("download err: " + e.getMessage());
		}
	}
}
