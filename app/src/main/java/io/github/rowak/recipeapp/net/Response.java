package io.github.rowak.recipeapp.net;

import org.json.JSONException;
import org.json.JSONObject;

public class Response
{
	private ResponseType respType;
	private JSONObject data;
	
	public Response(ResponseType respType, JSONObject data) {
		this.respType = respType;
		this.data = data;
	}
	
	public static Response INVALID_REQUEST = new Response(
			ResponseType.INVALID_REQUEST, new JSONObject());
	
	public static Response fromJSON(String json) {
		Response resp = new Response(null, null);
		try {
			JSONObject obj = new JSONObject(json);
			if (obj.has("type")) {
				resp.respType = formatResponseType(obj.getString("type"));
			}
			resp.data = obj;
		}
		catch (JSONException je) {
			je.printStackTrace();
		}
		return resp;
	}
	
	public JSONObject toJSON() {
		return data;
	}
	
	public ResponseType getType() {
		return respType;
	}
	
	public JSONObject getData() {
		return data;
	}
	
	private static ResponseType formatResponseType(String responseStr) {
		for (ResponseType type : ResponseType.values()) {
			if (type.toString().equals(responseStr)) {
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
