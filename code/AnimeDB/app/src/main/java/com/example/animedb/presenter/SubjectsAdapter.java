package com.example.animedb.presenter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.animedb.R;
import com.example.animedb.model.Subject;
import com.example.animedb.ui.SubjectActivity;

import java.util.List;

/**
 * Created by Fang on 2017/5/14.
 *
 */

public class SubjectsAdapter extends RecyclerView.Adapter<SubjectsAdapter.ViewHolder> {
    private Context context;

    private List<Subject> subjectList;


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context == null) {
            context = parent.getContext();
        }
        View view = LayoutInflater.from(context).inflate(R.layout.item_subject, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = holder.getAdapterPosition();
                Subject subject = subjectList.get(position);
                Intent intent = new Intent(context, SubjectActivity.class);
                intent.putExtra("id", subject.getId());

                context.startActivity(intent);
            }
        });
        //TODO
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Subject subject = subjectList.get(position);
        String titleCHS = subject.getTitleCHS();
        if (titleCHS.equals("")) {
            holder.title.setText(subject.getTitle());
        } else {
            holder.title.setText(titleCHS);
        }
        String pictureUrl = subject.getPictureUrl();
        if (!pictureUrl.equals("")) {

            Glide.with(context)
                    .load("http:" + pictureUrl)
                    .asBitmap()
                    .placeholder(R.drawable.no_image_available)
                    .error(R.drawable.no_image_available)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(holder.picture);

        }
        String duty = subject.getDuty();
        if (duty.equals("")) {
            holder.duty.setVisibility(View.GONE);
        }
        else {
            holder.duty.setText(duty.substring(0, duty.length() - 1));
        }
    }



    @Override
    public int getItemCount() {
        return subjectList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView picture;
        TextView title;
        TextView duty;

        ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_subject);
            picture = (ImageView) itemView.findViewById(R.id.item_subject_picture);
            title = (TextView) itemView.findViewById(R.id.item_subject_title);
            duty = (TextView) itemView.findViewById(R.id.item_subject_duty);
        }
    }

    public SubjectsAdapter(List<Subject> subjectList) {
        this.subjectList = subjectList;

    }
}
