package io.github.rowak.recipeapp.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class RecipeHeader {
	private int id;
	private String name;
	private String creator;
	private String description;
	private String category;
	private String imageUrl;
	private int prepTime;
	private int cookTime;
	private int servings;
	
	public static class Builder {
		private int id;
		private String name;
		private String creator;
		private String description;
		private String category;
		private String imageUrl;
		private int prepTime;
		private int cookTime;
		private int servings;
		
		public Builder setId(int id) {
			this.id = id;
			return this;
		}
		
		public Builder setName(String name) {
			this.name = name;
			return this;
		}
		
		public Builder setCreator(String creator) {
			this.creator = creator;
			return this;
		}
		
		public Builder setDescription(String description) {
			this.description = description;
			return this;
		}
		
		public Builder setCategory(String category) {
			this.category = category;
			return this;
		}
		
		public Builder setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
			return this;
		}
		
		public Builder setPrepTime(int prepTime) {
			this.prepTime = prepTime;
			return this;
		}
		
		public Builder setCookTime(int cookTime) {
			this.cookTime = cookTime;
			return this;
		}
		
		public Builder setServings(int servings) {
			this.servings = servings;
			return this;
		}
		
		public RecipeHeader build() {
			RecipeHeader header = new RecipeHeader();
			header.id = id;
			header.name = name;
			header.creator = creator;
			header.description = description;
			header.category = category;
			header.imageUrl = imageUrl;
			header.prepTime = prepTime;
			header.cookTime = cookTime;
			header.servings = servings;
			return header;
		}
	}
	
	public static RecipeHeader fromJSON(JSONObject json) {
		RecipeHeader.Builder headerBuilder = new RecipeHeader.Builder();
		try {
			if (json.has("id")) {
				headerBuilder.setId(json.getInt("id"));
			}
			if (json.has("name")) {
				headerBuilder.setName(json.getString("name"));
			}
			if (json.has("creator")) {
				headerBuilder.setCreator(json.getString("creator"));
			}
			if (json.has("description")) {
				headerBuilder.setDescription(json.getString("description"));
			}
			if (json.has("category")) {
				headerBuilder.setCategory(json.getString("category"));
			}
			if (json.has("imageUrl")) {
				headerBuilder.setImageUrl(json.getString("imageUrl"));
			}
			if (json.has("prepTime")) {
				headerBuilder.setPrepTime(json.getInt("prepTime"));
			}
			if (json.has("cookTime")) {
				headerBuilder.setCookTime(json.getInt("cookTime"));
			}
			if (json.has("servings")) {
				headerBuilder.setServings(json.getInt("servings"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return headerBuilder.build();
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getCreator() {
		return creator;
	}
	
	public void setCreator(String creator) {
		this.creator = creator;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getCategory() {
		return category;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}
	
	public String getImageUrl() {
		return imageUrl;
	}
	
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	public Bitmap getImage() throws IOException {
		URL url = new URL(getImageUrl());
		return BitmapFactory.decodeStream(url.openStream());
	}
	
	public int getPrepTime() {
		return prepTime;
	}
	
	public void setPrepTime(int prepTime) {
		this.prepTime = prepTime;
	}
	
	public int getCookTime() {
		return cookTime;
	}
	
	public void setCookTime(int cookTime) {
		this.cookTime = cookTime;
	}
	
	public int getTotalTime() {
		return prepTime + cookTime;
	}
	
	public int getServings() {
		return servings;
	}
	
	public void setServings(int servings) {
		this.servings = servings;
	}
	
	public JSONObject toJSON() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("id", id);
			obj.put("name", name);
			obj.put("creator", creator);
			obj.put("description", description);
			obj.put("category", category);
			obj.put("imageUrl", imageUrl);
			obj.put("prepTime", prepTime);
			obj.put("cookTime", cookTime);
			obj.put("servings", servings);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	@Override
	public String toString() {
		return toJSON().toString();
	}
}
