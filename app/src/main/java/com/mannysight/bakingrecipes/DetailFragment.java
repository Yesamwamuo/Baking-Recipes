package com.mannysight.bakingrecipes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.mannysight.bakingrecipes.model.Recipe;
import com.mannysight.bakingrecipes.model.Step;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.mannysight.bakingrecipes.StepsFragment.EXTRA_TEXT_STEP_INDEX;

/**
 * Created by wamuo on 6/11/2017.
 */

public class DetailFragment extends Fragment implements View.OnClickListener {
    private static final int IMAGE_ONLY_STATE = 23;
    private static final int VIDEO_ONLY_STATE = 24;
    private static final int NO_MEDIA_STATE = 25;

    @BindView(R.id.step_desc)
    TextView stepDescription;

    @BindView(R.id.detail_video)
    SimpleExoPlayerView detailVideoView;

    @BindView(R.id.detail_thumbnail)
    ImageView detailThumb;

    @BindView(R.id.detail_media_layout)
    FrameLayout frameLayout;

    @BindView(R.id.detail_layout)
    LinearLayout linearLayout;

    @BindView(R.id.detail_linear_layout)
    LinearLayout detail_linear_layout;

    @BindView(R.id.detail_button)
    Button detailButton;

    private SimpleExoPlayer player;
    private FragmentManager fragmentManager;

    private long playbackPosition;
    private int currentWindow;
    private boolean playWhenReady = true;

    private Context context;
    private Recipe recipe;
    private ArrayList<Step> stepsList;
    private Step step;

    private int stepIndex = -1;
    private boolean mTwoPane;

    public DetailFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_step_detail, container, false);
        ButterKnife.bind(this, rootView);
        context = getContext();

        Intent intentThatStartedThisActivity = getActivity().getIntent();
        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(EXTRA_TEXT_STEP_INDEX) && stepIndex == -1) {
                stepIndex = intentThatStartedThisActivity.getIntExtra(EXTRA_TEXT_STEP_INDEX, -1);
            }
            if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
                recipe = intentThatStartedThisActivity.getParcelableExtra(Intent.EXTRA_TEXT);
            }
        }

        if (recipe != null && stepIndex != -1) {
            stepsList = (ArrayList<Step>) recipe.getSteps();
            step = getStep(stepIndex);
            bind(step);
        }
        return rootView;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        releasePlayer();
    }


    private Step getStep(int stepIndex) {
        return stepsList.get(stepIndex);
    }


    private void bind(Step step) {
        stepDescription.setText(step.getDescription());
        switch (getState(step)) {
            case IMAGE_ONLY_STATE:
                detailVideoView.setVisibility(View.GONE);
                setImage(step.getThumbnailURL());
                break;
            case VIDEO_ONLY_STATE:
                hideSystemUi();
                detailThumb.setVisibility(View.GONE);
                initializePlayer(step.getVideoURL());
                break;
            case NO_MEDIA_STATE:
                frameLayout.setVisibility(View.GONE);
                linearLayout.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
                break;
        }

        int end = recipe.getSteps().size() - 1;
        if (stepIndex == end) {
            detailButton.setVisibility(View.GONE);
        }
    }

    private int getState(Step step) {
        if (step.getVideoURL().isEmpty() && !step.getThumbnailURL().isEmpty()) {
            return IMAGE_ONLY_STATE;
        } else if (!step.getVideoURL().isEmpty() && step.getThumbnailURL().isEmpty()) {
            return VIDEO_ONLY_STATE;
        } else if (step.getVideoURL().isEmpty() && step.getThumbnailURL().isEmpty()) {
            return NO_MEDIA_STATE;
        }
        throw new IllegalStateException();
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    private void setImage(String url) {
        Picasso.with(context)
                .load(url)
                .fit()
                .placeholder(R.mipmap.ic_launcher)
                .into(detailThumb);
    }

    private void initializePlayer(String url) {
        if (player == null) {
            player = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(getActivity()),
                    new DefaultTrackSelector(), new DefaultLoadControl());
            detailVideoView.setPlayer(player);
            player.setPlayWhenReady(playWhenReady);
            player.seekTo(currentWindow, playbackPosition);
        }
        MediaSource mediaSource = buildMediaSource(Uri.parse(url));
        player.prepare(mediaSource, true, false);
    }

    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.release();
            player = null;
        }
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource(uri, new DefaultHttpDataSourceFactory("baking_recipe"),
                new DefaultExtractorsFactory(), null, null);
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && mTwoPane == false) {

            detail_linear_layout.setVisibility(View.GONE);

            detailVideoView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    }

    public void setStepIndex(int stepIndex) {
        this.stepIndex = stepIndex;
    }


    protected void addClick(int id) {
        try {
            getView().findViewById(id).setOnClickListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onClick(View v) {
        if (v.getId() == R.id.detail_button) {
            onMyButtonClick(v);
        }
    }

    private void onMyButtonClick(View v) {
        int position = recipe.getSteps().size() - 1;
        if (stepIndex < position) {
            stepIndex++;
        } else {
            detailButton.setVisibility(View.GONE);
            return;
        }
        fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        DetailFragment detailFragment = new DetailFragment();
        detailFragment.setStepIndex(stepIndex);
        detailFragment.setRecipe(recipe);
        fragmentTransaction
                .replace(R.id.detail_fragment_container, detailFragment)
                .commit();
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (getActivity() instanceof StepsActivity) {
            mTwoPane = true;
        }

        if (mTwoPane) {
            detailButton.setVisibility(View.GONE);
        } else {
            addClick(R.id.detail_button);
        }
    }
}
