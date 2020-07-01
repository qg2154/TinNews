package com.laioffer.tinnews.ui.save;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.laioffer.tinnews.R;
import com.laioffer.tinnews.databinding.FragmentSaveBinding;
import com.laioffer.tinnews.model.Article;
import com.laioffer.tinnews.repository.NewsRepository;
import com.laioffer.tinnews.repository.NewsViewModelFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class SaveFragment extends Fragment {

    private SaveViewModel viewModel;
    private FragmentSaveBinding binding;

    public SaveFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_save, container, false);
        binding =  FragmentSaveBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SavedNewsAdapter savedNewsAdapter = new SavedNewsAdapter();
        binding.recyclerView.setAdapter(savedNewsAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        NewsRepository repository = new NewsRepository(getContext());
        viewModel = new ViewModelProvider(this, new NewsViewModelFactory(repository)).get(SaveViewModel.class);
        //拿数据
        viewModel
                .getAllSavedArticles()
                .observe(
                        getViewLifecycleOwner(),
                        savedArticles -> {
                            if (savedArticles != null) {
                                Log.d("SaveFragment", savedArticles.toString());
                                savedNewsAdapter.setArticles(savedArticles);

                            }
                        });

        //inner class
        savedNewsAdapter.setOnClickListener(new SavedNewsAdapter.OnClickListener() {
           @Override
           public void onClick(Article article) {

               SaveFragmentDirections.ActionTitleSaveToDetail actionTitleSaveToDetail = SaveFragmentDirections.actionTitleSaveToDetail();
               actionTitleSaveToDetail.setArticle(article);
               NavHostFragment.findNavController(SaveFragment.this).navigate(actionTitleSaveToDetail);

           }

           @Override
         public void unLike(Article article) {
               viewModel.deleteSavedArticle(article);
           }
     });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //prevent memory leak
        viewModel.onCancel();
    }
}
