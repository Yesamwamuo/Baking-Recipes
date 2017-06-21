package com.mannysight.bakingrecipes;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mannysight.bakingrecipes.model.Recipe;
import com.mannysight.bakingrecipes.model.Step;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wamuo on 6/11/2017.
 */

class StepAdapter extends RecyclerView.Adapter<StepAdapter.StepAdapterViewHolder>{
    private ArrayList<Step> mSteps;
    private Context context;
    private Recipe mRecipe;

    private final StepAdapterOnClickHandler mClickHandler;

    public StepAdapter(StepAdapterOnClickHandler mClickHandler) {
        this.mClickHandler = mClickHandler;
    }


    @Override
    public StepAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        int layoutIdForListItem = R.layout.step_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new StepAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StepAdapterViewHolder holder, int position) {
        Step step = mSteps.get(position);
        holder.bind(context, step);
    }

    @Override
    public int getItemCount() {
        if (mSteps == null) return 0;
        return mSteps.size();
    }

    public void setStepList(Recipe recipe) {
        mRecipe = recipe;
        mSteps = (ArrayList<Step>) recipe.getSteps();
        notifyDataSetChanged();
    }

    public class StepAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.step_short_desc)
        TextView stepTv;

        public StepAdapterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void bind(Context context, Step step) {
            stepTv.setText(step.getShortDescription());
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onClick(mRecipe, adapterPosition);
        }
    }

    public interface StepAdapterOnClickHandler {
        void onClick(Recipe recipe, int stepIndex);
    }
}
