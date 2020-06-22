package io.github.rowak.recipeapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.github.rowak.recipeapp.models.Ingredient;
import io.github.rowak.recipeapp.models.Recipe;
import io.github.rowak.recipeapp.models.RecipeHeader;
import io.github.rowak.recipeapp.net.Request;
import io.github.rowak.recipeapp.net.RequestType;
import io.github.rowak.recipeapp.net.Response;
import io.github.rowak.recipeapp.net.ResponseType;
import io.github.rowak.recipeapp.net.ServerRequest;
import io.github.rowak.recipeapp.tools.IngredientSort;

public class RecipeInfoActivity extends Activity
	implements ServerRequest.ServerResponseListener {
	private static String HTML_HEADER4_TAG_OPEN = "<h4>";
	private static String HTML_HEADER4_TAG_CLOSE = "</h4>";
	
	private RecipeHeader recipeHeader;
	private Recipe recipe;
	private ServerRequest serverRequest;
	private SharedPreferences prefs;
	private SharedPreferences.Editor editor;
	
	private TextView txtViewRecipeName;
	private TextView txtViewDescription;
	private TextView txtViewPrepTime;
	private TextView txtViewCookTime;
	private TextView txtViewTotalTime;
	private TextView txtViewIngredients;
	private TextView txtViewDirections;
	private RatingBar rtingBarRecipe;
	private EditText edtTxtServings;
	private ImageView imgViewRecipeImg;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recipe_info);
		recipeHeader = getHeaderFromIntent(getIntent());
		serverRequest = new ServerRequest(this);
		serverRequest.setListener(this);
		sendRecipeRequest();
		prefs = PreferenceManager.getDefaultSharedPreferences(
				getApplicationContext());
		editor = prefs.edit();
		initUI();
		loadRecipeImage();
	}
	
	@Override
	public void serverResponseReceived(Response response) {
		ResponseType type = response.getType();
		JSONObject data = response.getData();
		if (type == ResponseType.RECIPE) {
			try {
				Recipe recipe = Recipe.fromJSON(data.getJSONObject("data"));
				loadRecipe(recipe);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	private float getRecipeRating() {
		return prefs.getFloat("recipeRating" + recipeHeader.getId(), 0);
	}
	
	private void loadRecipe(final Recipe recipe) {
		this.recipe = recipe;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				txtViewIngredients.setVisibility(View.VISIBLE);
				txtViewDirections.setVisibility(View.VISIBLE);
				
				updateIngredients();
				txtViewDirections.setText(Html.fromHtml(recipe.getDirections()));
				txtViewDirections.setPadding(0, 0,
						0, getNavigationBarWidth()/2);
			}
		});
	}
	
	private void loadRecipeImage() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					final Bitmap image = recipeHeader.getImage();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							imgViewRecipeImg.setImageBitmap(image);
						}
					});
				} catch (IOException e) {
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							txtViewRecipeName.setPadding(0,
									getStatusBarWidth(), 0, 0);
						}
					});
				}
			}
		}).start();
	}
	
	private void updateIngredients() {
		String ingredientsHtml = getIngredientsHtml();
		txtViewIngredients.setText(Html.fromHtml(ingredientsHtml));
	}
	
	private void initUI() {
		txtViewRecipeName = findViewById(R.id.txtViewRecipeName);
		txtViewDescription = findViewById(R.id.txtViewDescription);
		txtViewPrepTime = findViewById(R.id.txtViewPrepTime);
		txtViewCookTime = findViewById(R.id.txtViewCookTime);
		txtViewTotalTime = findViewById(R.id.txtViewTotalTime);
		txtViewIngredients = findViewById(R.id.txtViewIngredients);
		txtViewDirections = findViewById(R.id.txtViewDirections);
		
		rtingBarRecipe = findViewById(R.id.rtingBarRecipe);
		
		edtTxtServings = findViewById(R.id.edtTxtServings);
		
		imgViewRecipeImg = findViewById(R.id.imgViewRecipeImg);
		
		txtViewRecipeName.setText(recipeHeader.getName());
		txtViewDescription.setText(recipeHeader.getDescription());
		String description = recipeHeader.getDescription();
		if (description == null || (description != null && description.equals(""))) {
			txtViewDescription.setText("No description");
			txtViewDescription.setTypeface(null, Typeface.ITALIC);
		}
		txtViewPrepTime.setText("Prep Time: " + formatTime(recipeHeader.getPrepTime()));
		txtViewCookTime.setText("Cook Time: " + formatTime(recipeHeader.getCookTime()));
		txtViewTotalTime.setText("Total Time: " + formatTime(recipeHeader.getPrepTime() +
				recipeHeader.getCookTime()));
		
		txtViewIngredients.setVisibility(View.GONE);
		txtViewDirections.setVisibility(View.GONE);
		
		rtingBarRecipe.setRating(getRecipeRating());
		rtingBarRecipe.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
				if (fromUser) {
					editor.putFloat("recipeRating" + recipeHeader.getId(), rating);
					editor.commit();
				}
			}
		});
		
		edtTxtServings.setText(recipeHeader.getServings() + "");
		edtTxtServings.setOnKeyListener(new View.OnKeyListener()
		{
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event)
			{
				if (recipe != null) {
					updateIngredients();
				}
				return false;
			}
		});
	}
	
	private String getIngredientsHtml() {
		StringBuilder sb = new StringBuilder();
		int servings = getServings();
		Ingredient[] ingredients = recipe.getIngredients();
		IngredientSort.sortByCategory(ingredients);
		if (ingredients[0].getCategory() != null) {
			sb.append(HTML_HEADER4_TAG_OPEN +
					ingredients[0].getCategory() + HTML_HEADER4_TAG_CLOSE);
		}
		for (int i = 0; i < ingredients.length; i++) {
			if (i > 0 && !categoriesEqual(ingredients[i].getCategory(),
					ingredients[i-1].getCategory())) {
				sb.append(HTML_HEADER4_TAG_OPEN +
						ingredients[i].getCategory() + HTML_HEADER4_TAG_CLOSE);
			}
			sb.append(ingredients[i].toString(servings,
					recipe.getServings()));
			if (i < ingredients.length-1) {
				sb.append("<br><br>");
			}
		}
		return sb.toString();
	}
	
	private boolean categoriesEqual(String category1, String category2) {
		return (category1 != null && category2 != null &&
				category1.equals(category2)) ||
				(category1 == null && category2 == null);
	}
	
	private int getServings() {
		try {
			return Integer.parseInt(edtTxtServings.getText().toString());
		} catch (NumberFormatException e) {
			return recipe.getServings();
		}
	}
	
	private int getStatusBarWidth() {
		int result = 0;
		int resourceId = getResources().getIdentifier(
				"status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}
	
	private int getNavigationBarWidth() {
		int resourceId = getResources().getIdentifier(
				"navigation_bar_height", "dimen", "android");
		if (resourceId > 0) {
			return getResources().getDimensionPixelSize(resourceId);
		}
		return 0;
	}
	
	private String formatTime(int minutes) {
		StringBuilder sb = new StringBuilder();
		int days = minutes / (60*24);
		int hours = minutes / 60;
		minutes %= 60;
		if (days > 0) {
			sb.append(days + "d");
			if (hours > 0 || minutes > 0) {
				sb.append(" ");
			}
		}
		if (hours > 0) {
			sb.append(hours + "h");
			if (minutes > 0) {
				sb.append(" ");
			}
		}
		if (minutes > 0) {
			sb.append(minutes + "m");
		}
		return sb.toString();
	}
	
	private void sendRecipeRequest() {
		JSONObject data = new JSONObject();
		try {
			data.put("recipeName", recipeHeader.getName());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Request request = new Request(RequestType.RECIPE, data);
		serverRequest.requestDataAsync(request);
	}
	
	private RecipeHeader getHeaderFromIntent(Intent intent) {
		return new RecipeHeader.Builder()
				.setId(intent.getIntExtra("id", -1))
				.setName(intent.getStringExtra("name"))
				.setCreator(intent.getStringExtra("creator"))
				.setDescription(intent.getStringExtra("description"))
				.setCategory(intent.getStringExtra("category"))
				.setImageUrl(intent.getStringExtra("imageUrl"))
				.setPrepTime(intent.getIntExtra("prepTime", -1))
				.setCookTime(intent.getIntExtra("cookTime", -1))
				.setServings(intent.getIntExtra("servings", -1))
				.build();
	}
}
