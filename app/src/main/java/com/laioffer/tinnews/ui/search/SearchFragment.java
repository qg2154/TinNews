package com.laioffer.tinnews.ui.search;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import com.laioffer.tinnews.R;
import com.laioffer.tinnews.databinding.FragmentSearchBinding;
import com.laioffer.tinnews.model.Article;
import com.laioffer.tinnews.repository.NewsRepository;
import com.laioffer.tinnews.repository.NewsViewModelFactory;
import com.laioffer.tinnews.ui.save.SavedNewsAdapter;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {
    private SearchViewModel viewModel;
    private FragmentSearchBinding binding;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_search, container, false);
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SearchNewsAdapter newsAdapter = new SearchNewsAdapter();
        newsAdapter.setLikeListener(new SearchNewsAdapter.LikeListener() {
            @Override
            public void onLike(Article article) {
                viewModel.setFavoriteArticleInput(article);
            }
            @Override
            public void onClick(Article article) {


                SearchFragmentDirections.ActionTitleSearchToDetail actionTitleSearchToDetail = SearchFragmentDirections.actionTitleSearchToDetail();
                actionTitleSearchToDetail.setArticle(article);
                NavHostFragment.findNavController(SearchFragment.this).navigate(actionTitleSearchToDetail);


            }
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        gridLayoutManager.setSpanSizeLookup(
                new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return position == 0 ? 2 : 1;
                    }
                });

        binding.recyclerView.setLayoutManager(gridLayoutManager);
        binding.recyclerView.setAdapter(newsAdapter);

        binding.searchView.setOnEditorActionListener(
                (v, actionId, event) -> {
                    String searchText = binding.searchView.getText().toString();
                    if (actionId == EditorInfo.IME_ACTION_DONE && !searchText.isEmpty()) {
                        viewModel.setSearchInput(searchText);
                        return true;
                    } else {
                        return false;
                    }
                });


        NewsRepository repository = new NewsRepository(getContext());
        viewModel = new ViewModelProvider(this, new NewsViewModelFactory(repository))
                .get(SearchViewModel.class);
        //viewModel.setSearchInput("Covid-19");
        viewModel
                .searchNews()
                .observe(
                        getViewLifecycleOwner(),
                        newsResponse -> {
                            if (newsResponse != null) {
                                Log.d("SearchFragment", newsResponse.toString());
                                newsAdapter.setArticles(newsResponse.articles);

                            }
                        });

        viewModel
                .onFavorite()
                .observe(
                        getViewLifecycleOwner(),
                        isSuccess -> {
                            if (isSuccess) {
                                Toast.makeText(requireActivity(), "Success", LENGTH_SHORT).show();
                                newsAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(requireActivity(), "You might have liked before", LENGTH_SHORT).show();
                            }
                        });



    }
}
