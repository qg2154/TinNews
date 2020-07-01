package com.laioffer.tinnews.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.laioffer.tinnews.databinding.FragmentHomeBinding;
import com.laioffer.tinnews.model.Article;
import com.laioffer.tinnews.repository.NewsRepository;
import com.laioffer.tinnews.repository.NewsViewModelFactory;
import com.mindorks.placeholderview.SwipeDecor;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements TinNewsCard.OnSwipeListener {


    private HomeViewModel viewModel;
    //no longer findviewbyid class 自己生成
    private FragmentHomeBinding binding;

    @Override
    public void onLike(Article news) {
        viewModel.setFavoriteArticleInput(news);
    }

    @Override
    public void onDisLike(Article news) {
        if (binding.swipeView.getChildCount() < 3) {
            viewModel.setCountryInput("us");
        }
    }

    @Override
   public void onDestroyView() {
        super.onDestroyView();
               viewModel.onCancel();
            }


    public HomeFragment() {
        // Required empty public constructor
    }

    //create the ui for the first time
    @Override                                                //parent ViewGroup
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return a VIEW or NULL
        //return inflater.inflate(R.layout.fragment_home, container, false);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();



    }

    //创建view之后
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        //设定swipe view
        binding.swipeView
                .getBuilder()
                .setDisplayViewCount(3)
                .setSwipeDecor(
                        new SwipeDecor()
                                .setPaddingTop(20)
                                .setRelativeScale(0.01f));
        //call back click trigger this function
        binding.rejectBtn.setOnClickListener(v -> binding.swipeView.doSwipe(false));
        binding.acceptBtn.setOnClickListener(v -> binding.swipeView.doSwipe(true));


        NewsRepository repository = new NewsRepository(getContext());
        viewModel = new ViewModelProvider(this, new NewsViewModelFactory((repository)))
                .get(HomeViewModel.class);

        //observe 监听
        viewModel.setCountryInput("us");

        viewModel
                .getTopHeadlines()
                .observe(
                        getViewLifecycleOwner(),
                        newsResponse -> {
                            if (newsResponse != null) {
                                //Log.d("HomeFragment", newsResponse.toString());
                                for (Article article : newsResponse.articles) {
                                    //anonymous inner class === implements TinNewsCard.onSwipeListener
                                    TinNewsCard tinNewsCard = new TinNewsCard(article,this);
                                    binding.swipeView.addView(tinNewsCard);
                                }
                            }
                        });
        viewModel
                                .onFavorite()
                                .observe(
                                        getViewLifecycleOwner(),
                                        isSuccess -> {
                                                if (isSuccess) {
                                                        Toast.makeText(getContext(), "Success", LENGTH_SHORT).show();
                                                   } else {



                                                        Toast.makeText(getContext(), "You might have liked before", LENGTH_SHORT).show();
                                                   }
                                            });



    }

}
