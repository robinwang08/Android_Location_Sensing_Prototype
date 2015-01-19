package edu.dartmouth.cs.biorhythm.location.data;

import org.json.JSONException;
import org.json.JSONObject;

public class LocationInfo {

	public String username;
	public double latitude;
	public double longitude;
	public String name;
	public String type;
	public String activity;
	public String who;

	public JSONObject toJson() {
		JSONObject jsonObj = new JSONObject();

		
		try {
			jsonObj.put("username", username);
			jsonObj.put("latitude", latitude);
			jsonObj.put("longitude", longitude);
			jsonObj.put("name", name);
			jsonObj.put("type", type);
			jsonObj.put("activity", activity);
			jsonObj.put("who", who);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonObj;
	}

	public static LocationInfo fromJson(JSONObject json) {
		LocationInfo locationInfo = new LocationInfo();

		try{
		locationInfo.username = json.getString("username");
		locationInfo.latitude = json.getDouble("latitude");
		locationInfo.longitude = json.getDouble("longitude");
		locationInfo.name = json.getString("name");
		locationInfo.type = json.getString("type");
		locationInfo.activity = json.getString("activity");
		locationInfo.who = json.getString("who");
		}
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

}
