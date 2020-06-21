package io.github.rowak.recipeapp.models;

import org.json.JSONException;
import org.json.JSONObject;

import io.github.rowak.recipeapp.tools.Fraction;

public class Ingredient {
	private static final String[] NON_PLURAL_UNITS = {
			"oz", "dr", "gt", "gtt", "smdg", "smi",
			"pn", "ds", "ssp", "csp", "fl.dr", "tsp",
			"dsp", "dssp", "dstspn", "tbsp", "Tbsp",
			"oz", "fl.oz", "wgf", "tcf", "pt", "qt",
			"pot", "gal"
	};
	
	private String measurementQty;
	private String measurementUnit;
	private String name;
	private String state;
	private String category;
	
	public static class Builder {
		private String measurementQty;
		private String measurementUnit;
		private String name;
		private String state;
		private String category;
		
		public Builder setMeasurementQty(String measurementQty) {
			this.measurementQty = measurementQty;
			return this;
		}
		
		public Builder setMeasurementUnit(String measurementUnit) {
			this.measurementUnit = measurementUnit;
			return this;
		}
		
		public Builder setName(String name) {
			this.name = name;
			return this;
		}
		
		public Builder setState(String state) {
			this.state = state;
			return this;
		}
		
		public Builder setCategory(String category) {
			this.category = category;
			return this;
		}
		
		public Ingredient build() {
			Ingredient ingredient = new Ingredient();
			ingredient.measurementQty = measurementQty;
			ingredient.measurementUnit = measurementUnit;
			ingredient.name = name;
			ingredient.state = state;
			ingredient.category = category;
			return ingredient;
		}
	}
	
	public static Ingredient fromJSON(JSONObject json) {
		Ingredient.Builder ingredientBuilder = new Ingredient.Builder();
		try {
			if (json.has("measurementQty")) {
				ingredientBuilder.setMeasurementQty(json.getString("measurementQty"));
			}
			if (json.has("measurementUnit")) {
				ingredientBuilder.setMeasurementUnit(json.getString("measurementUnit"));
			}
			if (json.has("name")) {
				ingredientBuilder.setName(json.getString("name"));
			}
			if (json.has("state")) {
				ingredientBuilder.setState(json.getString("state"));
			}
			if (json.has("category")) {
				ingredientBuilder.setCategory(json.getString("category"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ingredientBuilder.build();
	}
	
	public String getMeasurementQty() {
		return measurementQty;
	}
	
	public void setMeasurementQty(String measurementQty) {
		this.measurementQty = measurementQty;
	}
	
	public String getMeasurementUnit() {
		return measurementUnit;
	}
	
	public void setMeasurementUnit(String measurementUnit) {
		this.measurementUnit = measurementUnit;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getState() {
		return state;
	}
	
	public void setState(String state) {
		this.state = state;
	}
	
	public String getCategory() {
		return category;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}
	
	public JSONObject toJSON() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("measurementQty", measurementQty);
			obj.put("measurementUnit", measurementUnit);
			obj.put("name", name);
			obj.put("state", state);
			obj.put("category", category);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	public String toString(int servings, int recipeServings) {
		StringBuilder sb = new StringBuilder();
		if (measurementQty != null) {
			Fraction frac = Fraction.fromString(measurementQty);
			frac.multiply(new Fraction(servings, recipeServings));
			sb.append(frac.toMixedStr() + " ");
		}
		if (measurementUnit != null) {
			String qty = measurementQty == null ?
					capitalizeFirstChar(measurementUnit) :
					measurementUnit;
			sb.append(qty);
			if (unitIsPlural()) {
				sb.append("s");
			}
			sb.append(" ");
		}
		if (name != null) {
			String ingredientName = measurementQty == null &&
					measurementUnit == null ?
					capitalizeFirstChar(name) : name;
			sb.append(ingredientName);
			if (ingredientNameIsPlural()) {
				sb.append("s");
			}
		}
		if (state != null) {
			sb.append(", " + state);
		}
		return capitalizeFirstChar(sb.toString());
	}
	
	@Override
	public String toString() {
		return toString(1, 1);
	}
	
	private String capitalizeFirstChar(String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}
	
	private boolean unitIsPlural() {
		if (measurementQty != null) {
			for (String unit : NON_PLURAL_UNITS) {
				if (unit.equals(measurementUnit)) {
					return false;
				}
			}
			Fraction frac = Fraction.fromString(measurementQty);
			return frac.toDecimal() > 1;
		}
		return false;
	}
	
	private boolean ingredientNameIsPlural() {
		if (measurementQty != null && measurementUnit == null) {
			Fraction frac = Fraction.fromString(measurementQty);
			return frac.toDecimal() > 1 && name.charAt(name.length()-1) != 's';
		}
		return false;
	}
}
