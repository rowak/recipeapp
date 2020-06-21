package io.github.rowak.recipeapp.net;

import org.json.JSONException;
import org.json.JSONObject;

public class Request {
	private RequestType reqType;
	private JSONObject data;
	
	public Request(RequestType reqType) {
		this.reqType = reqType;
	}
	
	public Request(RequestType reqType, JSONObject data) {
		this.reqType = reqType;
		this.data = data;
	}
	
	public static Request fromJSON(String json) {
		Request req = new Request(null);
		try {
			JSONObject obj = new JSONObject(json);
			if (obj.has("type")) {
				req.reqType = formatRequestType(obj.getString("type"));
			}
			if (obj.has("data")) {
				req.data = obj.getJSONObject("data");
			}
		} catch (JSONException je) {
			je.printStackTrace();
		}
		return req;
	}
	
	public static Request categories() {
		return new Request(RequestType.CATEGORIES);
	}
	
	public JSONObject toJSON() {
		return data;
	}
	
	public RequestType getType() {
		return reqType;
	}
	
	public JSONObject getData() {
		return data;
	}
	
	private static RequestType formatRequestType(String requestStr) {
		for (RequestType type : RequestType.values()) {
			if (type.toString().equals(requestStr)) {
				return type;
			}
		}
		return null;
	}
	
	@Override
	public String toString() {
		return toJSON().toString();
	}
}
