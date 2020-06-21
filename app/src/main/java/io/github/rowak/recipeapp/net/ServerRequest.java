package io.github.rowak.recipeapp.net;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Iterator;

import io.github.rowak.recipeapp.R;
import io.github.rowak.recipeapp.models.Category;
import io.github.rowak.recipeapp.models.Recipe;
import io.github.rowak.recipeapp.models.RecipeHeader;

public class ServerRequest {
	private Context context;
	private SharedPreferences prefs;
	private ServerResponseListener listener;
	
	public ServerRequest(Context context) {
		this.context = context;
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	public enum ResponseCode {
		SUCCESS, FAILED, ERROR
	}
	
	public interface ServerResponseListener {
		void serverResponseReceived(Response response);
	}
	
	public void setListener(ServerResponseListener listener) {
		this.listener = listener;
	}
	
//	public ResponseCode requestData(Request request) {
//		String url = getURL(request);
//		try {
//			HttpRequest httpreq = HttpRequest.get(url);
//			if (httpreq != null) {
//				String body = httpreq.body();
//				httpreq.closeOutputQuietly();
//				if (body != null) {
//					Response resp = Response.fromJSON(body);
//					return handleResponse(resp);
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return ResponseCode.ERROR;
//	}
	
	public Response requestData(Request request) {
		String url = getURL(request);
		try {
			HttpRequest httpreq = HttpRequest.get(url);
			if (httpreq != null) {
				String body = httpreq.body();
				httpreq.closeOutputQuietly();
				return Response.fromJSON(body);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Response.INVALID_REQUEST;
	}
	
	public void requestDataAsync(final Request request) {
		new Thread(new Runnable()
		{
			@Override
			public void run() {
				String url = getURL(request);
				try {
					HttpRequest httpreq = HttpRequest.get(url);
					if (httpreq != null) {
						String body = httpreq.body();
						httpreq.closeOutputQuietly();
						if (listener != null) {
							listener.serverResponseReceived(
									Response.fromJSON(body));
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	public Recipe requestRecipe(String recipeName) {
		JSONObject requestData = new JSONObject();
		try {
			requestData.put("recipeName", recipeName);
			Request request = new Request(RequestType.RECIPE, requestData);
			Response response = requestData(request);
			if (response.getType() == ResponseType.RECIPE) {
				return Recipe.fromJSON(response.getData().getJSONObject("data"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Category[] requestCategories() {
		Request request = new Request(RequestType.CATEGORIES, new JSONObject());
		Response response = requestData(request);
		try {
			if (response.getType() == ResponseType.CATEGORIES) {
				JSONArray data = response.getData().getJSONArray("data");
				Category[] categories = new Category[data.length()];
				for (int i = 0; i < categories.length; i++) {
					categories[i] = Category.fromJSON(data.getJSONObject(i));
				}
				return categories;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public RecipeHeader[] requestRecipeHeaders() {
		Request request = new Request(RequestType.RECIPE_HEADERS, new JSONObject());
		Response response = requestData(request);
		try {
			if (response.getType() == ResponseType.RECIPE_HEADERS) {
				JSONArray data = response.getData().getJSONArray("data");
				RecipeHeader[] recipeHeaders = new RecipeHeader[data.length()];
				for (int i = 0; i < recipeHeaders.length; i++) {
					recipeHeaders[i] = RecipeHeader.fromJSON(data.getJSONObject(i));
				}
				return recipeHeaders;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private String getURL(Request request) {
		String url = getServer();
		url += "/" + request.getType();
		url += "?version=" + getAppVersion().substring(1);
		String type = request.getType().toString().toLowerCase();
		if (type != null) {
			JSONObject data = request.getData();
			if (data != null) {
				for (Iterator<String> it = data.keys(); it.hasNext(); ) {
					String key = it.next();
					try {
						url += "&" + key + "=" + data.get(key);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return url;
	}
	
	private String getAppVersion() {
		try {
			PackageInfo pInfo = context.getPackageManager().getPackageInfo(
					context.getApplicationContext().getPackageName(), 0);
			return pInfo.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private String getServer() {
		String server = prefs.getString("server",
				context.getString(R.string.main_server));
		String hostname = server.substring(0, server.lastIndexOf(':'));
		String portstr = server.substring(server.lastIndexOf(':')+1);
		int port = -1;
		if (server.indexOf(':') == server.lastIndexOf(':') &&
				(server.contains("http://") || server.contains("https://"))) {
			hostname = server;
		} else {
			try {
				port = Integer.parseInt(portstr);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return port == -1 ? hostname :
				prefs.getString("server",
						context.getString(R.string.main_server));
	}
	
//	private ResponseCode handleResponse(Response response) {
//		ResponseCode code = ResponseCode.FAILED;
//		try {
//			SharedPreferences.Editor editor = prefs.edit();
//			JSONObject rawjson = response.toJSON();
//			if (response.getType() == ResponseType.RECIPE) {
//				JSONArray data = rawjson.getJSONArray("data");
//				editor.putString("files", data.toString());
//				code = ResponseCode.SUCCESS;
//			}
//			else if (response.getType() == ResponseType.RECIPE_HEADERS) {
//				JSONArray data = rawjson.getJSONArray("data");
//				editor.putString("tags", data.toString());
//				code = ResponseCode.SUCCESS;
//			}
//			else if (response.getType() == ResponseType.CATEGORIES) {
//				JSONArray data = rawjson.getJSONArray("data");
//				editor.putString("series", data.toString());
//				code = ResponseCode.SUCCESS;
//			}
//			else if (response.getType() == ResponseType.INVALID_REQUEST ||
//					 response.getType() == ResponseType.DATABASE_ERROR ||
//					 response.getType() == ResponseType.RESOURCE_NOT_FOUND) {
//				code = ResponseCode.ERROR;
//			}
////			else if (response.getType() == ResponseType.OUTDATED_CLIENT) {
////				showToast("Error: You must update the app in order " +
////						"to connect to this server");
////			}
//			if (code == ResponseCode.SUCCESS) {
//				long time = Calendar.getInstance().getTime().getTime();
//				editor.putLong("lastUpdate", time);
//				editor.commit();
//			}
//		} catch (JSONException je) {
//			je.printStackTrace();
//		}
//		return code;
//	}
}
