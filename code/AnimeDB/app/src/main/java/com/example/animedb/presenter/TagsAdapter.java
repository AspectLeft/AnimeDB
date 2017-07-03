package com.example.animedb.presenter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.animedb.R;
import com.example.animedb.model.Tag;
import com.example.animedb.ui.SubjectsWithTagActivity;

import java.util.List;

/**
 * Created by Fang on 2017/6/17.
 *
 */

public class TagsAdapter extends RecyclerView.Adapter<TagsAdapter.ViewHolder> {
    private Context context;

    private List<Tag> tagList;


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context == null) {
            context = parent.getContext();
        }
        View view = LayoutInflater.from(context).inflate(R.layout.item_tag, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = holder.getAdapterPosition();
                Tag tag = tagList.get(position);
                Intent intent = new Intent(context, SubjectsWithTagActivity.class);
                intent.putExtra("tagName", tag.getName());
                context.startActivity(intent);
            }
        });


        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Tag tag = tagList.get(position);
        holder.name.setText(tag.getName());
        holder.voteNum.setText(String.valueOf(tag.getVoteNum()));

    }

    @Override
    public int getItemCount() {
        return tagList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView name;
        TextView voteNum;

        ViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.card_tag);
            name = (TextView) itemView.findViewById(R.id.item_tag_name);
            voteNum = (TextView) itemView.findViewById(R.id.item_tag_voteNum);
        }
    }

    public TagsAdapter(List<Tag> tagList) {
        this.tagList = tagList;
    }
}
