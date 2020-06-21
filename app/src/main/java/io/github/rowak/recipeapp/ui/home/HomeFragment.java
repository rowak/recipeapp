package io.github.rowak.recipeapp.ui.home;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.github.rowak.recipeapp.R;
import io.github.rowak.recipeapp.models.Category;
import io.github.rowak.recipeapp.net.Request;
import io.github.rowak.recipeapp.net.Response;
import io.github.rowak.recipeapp.net.ResponseType;
import io.github.rowak.recipeapp.net.ServerRequest;
import io.github.rowak.recipeapp.tools.CategorySort;

public class HomeFragment extends Fragment
		implements CategoryAdapter.ItemClickListener,
			ServerRequest.ServerResponseListener,
			View.OnKeyListener {
	private static final String DEFAULT_APPBAR_TITLE = "Categories";
	
	private RecyclerView list;
	private CategoryAdapter listAdapter;
	private LinearLayoutManager listLayoutManager;
	private ServerRequest serverRequest;
	
	public View onCreateView(@NonNull LayoutInflater inflater,
							 ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.recycler_list_layout,
				container, false);
		serverRequest = new ServerRequest(getContext());
		serverRequest.setListener(this);
		list = root.findViewById(R.id.rcyclrView);
		initializeList();
		sendCategoriesRequest();
		
		root.setFocusableInTouchMode(true);
		root.requestFocus();
		root.setOnKeyListener(this);
		
		return root;
	}
	
	@Override
	public void onItemClick(View view, int position) {
		Category category = listAdapter.getItem(position);
		int childCategories = listAdapter.getNumChildren(category);
		if (childCategories == 0) {
			createRecipesFragment(category);
		}
		updateActionBarTitle(category);
	}
	
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
		{
			moveToParentCategories();
			return true;
		}
		return false;
	}
	
	@Override
	public void serverResponseReceived(Response response) {
		ResponseType type = response.getType();
		JSONObject data = response.getData();
		if (type == ResponseType.CATEGORIES) {
			List<Category> categories = jsonToCategories(data);
			CategorySort.quickSort(categories);
			listAdapter.setData((ArrayList<Category>)categories);
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					listAdapter.notifyDataSetChanged();
				}
			});
		}
	}
	
	private void createRecipesFragment(Category category) {
		RecipesFragment recipesFragment = new RecipesFragment(category);
		recipesFragment.setRecipesFragmentEndListener(new RecipesFragment.RecipesFragmentEndListener() {
			@Override
			public void onRecipesFragmentEnd() {
				moveToParentCategories();
			}
		});
		getActivity().getSupportFragmentManager().beginTransaction()
				.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_left, R.anim.enter_from_right, R.anim.exit_to_right)
				.replace(((ViewGroup)getView().getParent()).getId(), recipesFragment, "recipesFragment")
				.addToBackStack("categoriesFragment")
				.commit();
	}
	
	private void moveToParentCategories() {
		listAdapter.moveToPreviousParent();
		listAdapter.notifyDataSetChanged();
		updateActionBarTitle(listAdapter.getCurrentParent());
	}
	
	private void sendCategoriesRequest() {
		serverRequest.requestDataAsync(Request.categories());
	}
	
	private List<Category> jsonToCategories(JSONObject responseData) {
		try {
			JSONArray data = responseData.getJSONArray("data");
			List<Category> categories = new ArrayList<Category>();
			for (int i = 0; i < data.length(); i++) {
				categories.add(Category.fromJSON(data.getJSONObject(i)));
			}
			return categories;
		} catch (JSONException e) {
			return null;
		}
	}
	
	private void updateActionBarTitle(Category category) {
		String title = category != null ? category.toTitleCase() : "Categories";
		if (category == null) {
		
		} else {
			title = category.toTitleCase();
			((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);
		}
		((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);
	}
	
	private void initializeList() {
		listLayoutManager = new LinearLayoutManager(getContext());
		list.setLayoutManager(listLayoutManager);
		listAdapter = new CategoryAdapter(getContext(),
				new ArrayList<Category>());
		list.setAdapter(listAdapter);
		listAdapter.setClickListener(this);
	}
}