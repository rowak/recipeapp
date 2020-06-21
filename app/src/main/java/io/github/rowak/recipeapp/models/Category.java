package io.github.rowak.recipeapp.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Category {
	private String name;
	private String parent;
	private int numRecipes;
	
	public Category(String name, String parent, int numRecipes) {
		this.name = name;
		this.parent = parent;
		this.numRecipes = numRecipes;
	}
	
	public static Category fromJSON(JSONObject json) {
		Category category = new Category(null, null, -1);
		try {
			if (json.has("name")) {
				category.name = json.getString("name");
			}
			if (json.has("parent")) {
				category.parent = json.getString("parent");
			}
			if (json.has("numRecipes")) {
				category.numRecipes = json.getInt("numRecipes");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return category;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getParent() {
		return parent;
	}
	
	public void setParent(String parent) {
		this.parent = parent;
	}
	
	public int getNumRecipes() {
		return numRecipes;
	}
	
	public void setNumRecipes(int numRecipes) {
		this.numRecipes = numRecipes;
	}
	
	public JSONObject toJSON() {
		JSONObject obj = new JSONObject();
		try {
			if (name != null) {
				obj.put("name", name);
			}
			if (parent != null) {
				obj.put("parent", parent);
			}
			if (numRecipes != -1) {
				obj.put("numRecipes", numRecipes);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	public String toTitleCase() {
		char[] chars = name.toCharArray();
		chars[0] = capitalizeChar(chars[0]);
		char c;
		for (int i = 0; i < chars.length; i++) {
			c = chars[i];
			if (i-1 > 0 && chars[i-1] == ' ') {
				chars[i] = capitalizeChar(c);
			}
		}
		return new String(chars);
	}
	
	private char capitalizeChar(char c) {
		return c >= 'a' && c <= 'z' ? (char)(c - 32) : c;
	}
	
	@Override
	public String toString() {
		return toJSON().toString();
	}
}
