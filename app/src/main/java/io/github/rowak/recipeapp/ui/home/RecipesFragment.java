package io.github.rowak.recipeapp.ui.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.rowak.recipeapp.R;
import io.github.rowak.recipeapp.RecipeInfoActivity;
import io.github.rowak.recipeapp.models.Category;
import io.github.rowak.recipeapp.models.RecipeHeader;
import io.github.rowak.recipeapp.net.Request;
import io.github.rowak.recipeapp.net.RequestType;
import io.github.rowak.recipeapp.net.Response;
import io.github.rowak.recipeapp.net.ResponseType;
import io.github.rowak.recipeapp.net.ServerRequest;

public class RecipesFragment extends Fragment
		implements RecipeHeaderAdapter.ItemClickListener,
		ServerRequest.ServerResponseListener,
		View.OnKeyListener {
	private Category parentCategory;
	private RecyclerView list;
	private RecipeHeaderAdapter listAdapter;
	private LinearLayoutManager listLayoutManager;
	private ServerRequest serverRequest;
	private RecipesFragmentEndListener listener;
	
	public RecipesFragment(Category parentCategory) {
		this.parentCategory = parentCategory;
	}
	
	public interface RecipesFragmentEndListener {
		void onRecipesFragmentEnd();
	}
	
	public void setRecipesFragmentEndListener(RecipesFragmentEndListener listener) {
		this.listener = listener;
	}
	
	public View onCreateView(@NonNull LayoutInflater inflater,
							 ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.recycler_list_layout,
				container, false);
		serverRequest = new ServerRequest(getContext());
		serverRequest.setListener(this);
		list = root.findViewById(R.id.rcyclrView);
		initializeList();
		sendRecipeHeadersRequest();
		
		root.setFocusableInTouchMode(true);
		root.requestFocus();
		root.setOnKeyListener(this);
		
		return root;
	}
	
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
		{
			getActivity().getSupportFragmentManager().popBackStack();
			listener.onRecipesFragmentEnd();
			return true;
		}
		return false;
	}
	
	@Override
	public void onItemClick(View view, int position) {
		RecipeHeader recipeHeader = listAdapter.getItem(position);
		Intent intent = new Intent(getActivity(), RecipeInfoActivity.class);
		intent.putExtra("id", recipeHeader.getId());
		intent.putExtra("name", recipeHeader.getName());
		intent.putExtra("creator", recipeHeader.getCreator());
		intent.putExtra("description", recipeHeader.getDescription());
		intent.putExtra("category", recipeHeader.getCategory());
		intent.putExtra("imageUrl", recipeHeader.getImageUrl());
		intent.putExtra("prepTime", recipeHeader.getPrepTime());
		intent.putExtra("cookTime", recipeHeader.getCookTime());
		intent.putExtra("servings", recipeHeader.getServings());
		startActivity(intent);
	}
	
	@Override
	public void serverResponseReceived(Response response) {
		ResponseType type = response.getType();
		JSONObject data = response.getData();
		if (type == ResponseType.RECIPE_HEADERS) {
			List<RecipeHeader> recipeHeaders = jsonToRecipeHeaders(data);
			listAdapter.setData(recipeHeaders, getRecipeImagesMap(recipeHeaders));
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					listAdapter.notifyDataSetChanged();
				}
			});
		}
	}
	
	private void sendRecipeHeadersRequest() {
		JSONObject data = new JSONObject();
		try {
			data.put("categoryName", parentCategory.getName());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Request request = new Request(RequestType.RECIPE_HEADERS, data);
		serverRequest.requestDataAsync(request);
	}
	
	private List<RecipeHeader> jsonToRecipeHeaders(JSONObject responseData) {
		try {
			JSONArray data = responseData.getJSONArray("data");
			List<RecipeHeader> recipeHeaders = new ArrayList<RecipeHeader>();
			for (int i = 0; i < data.length(); i++) {
				recipeHeaders.add(RecipeHeader.fromJSON(data.getJSONObject(i)));
			}
			return recipeHeaders;
		} catch (JSONException e) {
			return null;
		}
	}
	
	private Map<Integer, Bitmap> getRecipeImagesMap(List<RecipeHeader> recipeHeaders) {
		Map<Integer, Bitmap> recipeImages = new HashMap<Integer, Bitmap>();
		for (RecipeHeader header : recipeHeaders) {
			try {
				recipeImages.put(header.getId(), header.getImage());
			} catch (IOException e) {
//				e.printStackTrace();
			}
		}
		return recipeImages;
	}
	
	private void initializeList() {
		listLayoutManager = new LinearLayoutManager(getContext());
		list.setLayoutManager(listLayoutManager);
		listAdapter = new RecipeHeaderAdapter(getContext(),
				new ArrayList<RecipeHeader>(), parentCategory);
		list.setAdapter(listAdapter);
		listAdapter.setClickListener(this);
	}
}
