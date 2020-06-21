package io.github.rowak.recipeapp.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Recipe {
	private RecipeHeader header;
	private Ingredient[] ingredients;
	private String directions;  // HTML encoded
	
	public static class Builder {
		private RecipeHeader.Builder headerBuilder;
		private List<Ingredient> ingredients;
		private String directions;
		
		public Builder() {
			headerBuilder = new RecipeHeader.Builder();
			ingredients = new ArrayList<Ingredient>();
		}
		
		public Builder setId(int id) {
			headerBuilder.setId(id);
			return this;
		}
		
		public Builder setName(String name) {
			headerBuilder.setName(name);
			return this;
		}
		
		public Builder setCreator(String creator) {
			headerBuilder.setCreator(creator);
			return this;
		}
		
		public Builder setCategory(String category) {
			headerBuilder.setCategory(category);
			return this;
		}
		
		public Builder setDescription(String description) {
			headerBuilder.setDescription(description);
			return this;
		}
		
		public Builder setImageUrl(String imageUrl) {
			headerBuilder.setImageUrl(imageUrl);
			return this;
		}
		
		public Builder setPrepTime(int prepTime) {
			headerBuilder.setPrepTime(prepTime);
			return this;
		}
		
		public Builder setCookTime(int cookTime) {
			headerBuilder.setCookTime(cookTime);
			return this;
		}
		
		public Builder setServings(int servings) {
			headerBuilder.setServings(servings);
			return this;
		}
		
		public Builder addIngredient(Ingredient ingredient) {
			ingredients.add(ingredient);
			return this;
		}
		
		public Builder setIngredients(List<Ingredient> ingredients) {
			this.ingredients = ingredients;
			return this;
		}
		
		public Builder setDirections(String directions) {
			this.directions = directions;
			return this;
		}
		
		public Recipe build() {
			Recipe recipe = new Recipe();
			recipe.header = headerBuilder.build();
			recipe.ingredients = ingredients.toArray(new Ingredient[0]);
			recipe.directions = directions;
			return recipe;
		}
	}
	
	public static Recipe fromJSON(JSONObject json) {
		Recipe.Builder recipeBuilder = new Recipe.Builder();
		try {
			if (json.has("header")) {
				RecipeHeader header = RecipeHeader.fromJSON(json.getJSONObject("header"));
				recipeBuilder.setId(header.getId());
				recipeBuilder.setName(header.getName());
				recipeBuilder.setCreator(header.getCreator());
				recipeBuilder.setDescription(header.getDescription());
				recipeBuilder.setCategory(header.getCategory());
				recipeBuilder.setImageUrl(header.getImageUrl());
				recipeBuilder.setPrepTime(header.getPrepTime());
				recipeBuilder.setCookTime(header.getCookTime());
				recipeBuilder.setServings(header.getServings());
			}
			if (json.has("ingredients")) {
				JSONArray ingredientsJson = json.getJSONArray("ingredients");
				for (int i = 0; i < ingredientsJson.length(); i++) {
					recipeBuilder.addIngredient(Ingredient.fromJSON(ingredientsJson.getJSONObject(i)));
				}
			}
			if (json.has("description")) {
				recipeBuilder.setDescription(json.getString("description"));
			}
			if (json.has("directions")) {
				recipeBuilder.setDirections(json.getString("directions"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return recipeBuilder.build();
	}
	
	public RecipeHeader getHeader() {
		return header;
	}
	
	public void setHeader(RecipeHeader header) {
		this.header = header;
	}
	
	public int getId() {
		return header.getId();
	}
	
	public void setId(int id) {
		header.setId(id);
	}
	
	public String getName() {
		return header.getName();
	}
	
	public void setName(String name) {
		header.setName(name);
	}
	
	public String getCreator() {
		return header.getCreator();
	}
	
	public void setCreator(String creator) {
		header.setCreator(creator);
	}
	
	public String getDescription() {
		return header.getDescription();
	}
	
	public void setDescription(String description) {
		header.setDescription(description);
	}
	
	public String getCategory() {
		return header.getCategory();
	}
	
	public void setCategory(String category) {
		header.setCategory(category);
	}
	
	public String getImageUrl() {
		return header.getImageUrl();
	}
	
	public void setImageUrl(String imageUrl) {
		header.setImageUrl(imageUrl);
	}
	
	public Bitmap getImage() throws IOException {
		return header.getImage();
	}
	
	public int getPrepTime() {
		return header.getPrepTime();
	}
	
	public void setPrepTime(int prepTime) {
		header.setPrepTime(prepTime);
	}
	
	public int getCookTime() {
		return header.getCookTime();
	}
	
	public void setCookTime(int cookTime) {
		header.setCookTime(cookTime);
	}
	
	public int getServings() {
		return header.getServings();
	}
	
	public void setServings(int servings) {
		header.setServings(servings);
	}
	
	public Ingredient[] getIngredients() {
		return ingredients;
	}
	
	public void setIngredients(Ingredient[] ingredients) {
		this.ingredients = ingredients;
	}
	
	public String getDirections() {
		return directions;
	}
	
	public void setDirections(String directions) {
		this.directions = directions;
	}
	
	public JSONObject toJSON() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("header", header.toJSON());
			JSONArray arr = new JSONArray();
			for (Ingredient ingredient : ingredients) {
				arr.put(ingredient.toJSON());
			}
			obj.put("ingredients", arr);
			obj.put("directions", directions);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	@Override
	public String toString() {
		return header.getName();
	}
}
