package com.werocksta.dagger2demo.view.fragment;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.werocksta.dagger2demo.MainApplication;
import com.werocksta.dagger2demo.R;
import com.werocksta.dagger2demo.adapter.GithubRepoAdapter;
import com.werocksta.dagger2demo.manager.ApiService;
import com.werocksta.dagger2demo.model.RepoCollection;
import com.werocksta.dagger2demo.presenter.RepoPresenter;
import com.werocksta.dagger2demo.presenter.RepoPresenterImpl;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class RepoFragment extends Fragment implements RepoPresenter.View, GithubRepoAdapter.OnClickRepo {

    @BindView(R.id.rvList)
    RecyclerView rvList;

    @BindView(R.id.smootProgressBar)
    SmoothProgressBar smoothProgressBar;

    @Inject
    ApiService service;

    RepoPresenter presenter;
    GithubRepoAdapter adapter;

    public static final String EXTRA_USER = "EXTRA_USER";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        ((MainApplication) getActivity().getApplication()).getComponent().inject(this);
    }

    public static RepoFragment newInstance(String user) {
        RepoFragment fragment = new RepoFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_USER, user);
        fragment.setArguments(bundle);
        return fragment;
    }

    public RepoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_repo, container, false);
        ButterKnife.bind(this, view);

        adapter = new GithubRepoAdapter();
        presenter = new RepoPresenterImpl(this, service);
        setUpView();
        presenter.getRepo(getArguments().getString(EXTRA_USER));
        return view;
    }

    private void setUpView() {
        rvList.setHasFixedSize(false);
        rvList.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        rvList.setItemAnimator(new DefaultItemAnimator());
        rvList.setAdapter(adapter);
    }

    @Override
    public void loading() {
        smoothProgressBar.progressiveStart();
    }

    @Override
    public void displayRepo(List<RepoCollection> repo) {
        adapter.setOnClickRepo(this);
        adapter.setRepoList(repo);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void getRepoError(String message) {
        smoothProgressBar.progressiveStop();
    }

    @Override
    public void loadComplete() {
        smoothProgressBar.progressiveStop();
    }

    @Override
    public void onClickRepoItem(RepoCollection repo) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(getActivity(), Uri.parse(repo.getHtmlUrl()));
    }
}
