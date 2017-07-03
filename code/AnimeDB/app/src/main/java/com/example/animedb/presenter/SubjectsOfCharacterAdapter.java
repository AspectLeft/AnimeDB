package com.example.animedb.presenter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.animedb.R;
import com.example.animedb.model.Person;
import com.example.animedb.model.Subject;
import com.example.animedb.ui.MakersActivity;
import com.example.animedb.ui.PersonActivity;
import com.example.animedb.ui.SubjectActivity;

import java.util.List;

/**
 * Created by Fang on 2017/6/17.
 *
 */

public class SubjectsOfCharacterAdapter extends RecyclerView.Adapter<SubjectsOfCharacterAdapter.ViewHolder> {
    private Context context;

    private int characterId;
    private List<Subject> subjectList;


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context == null) {
            context = parent.getContext();
        }
        View view = LayoutInflater.from(context).inflate(R.layout.item_subject_of_character, parent, false);
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
        holder.dubberLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = holder.getAdapterPosition();
                Subject subject = subjectList.get(position);
                Intent intent = new Intent(context, PersonActivity.class);
                intent.putExtra("id", subject.getDubbers().get(0).getId());
                context.startActivity(intent);
            }
        });
        holder.dubberMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = holder.getAdapterPosition();
                Subject subject = subjectList.get(position);
                Intent intent = new Intent(context, MakersActivity.class);
                intent.putExtra("subjectId", subject.getId());
                intent.putExtra("characterId", characterId);
                context.startActivity(intent);
            }
        });



        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Subject subject = subjectList.get(position);
        holder.title.setText(subject.getTitle());
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
        else {
            Glide.clear(holder.picture);
            holder.picture.setImageResource(R.drawable.no_image_available);
        }
        String duty = subject.getDuty();
        if (duty.equals("")) {
            holder.job.setVisibility(View.GONE);
        }
        else {
            holder.job.setText(duty.substring(0, duty.length() - 1));
        }

        List<Person> dubbers = subject.getDubbers();
        if (dubbers.isEmpty()) {
            holder.dubberLayout.setVisibility(View.GONE);
        }
        else {
            holder.dubberLayout.setVisibility(View.VISIBLE);
            Person firstDubber = dubbers.get(0);
            holder.dubberName.setText(firstDubber.getPersonName());
            String dubberPictureUrl = firstDubber.getPictureUrl();
            if (dubbers.size() == 1) {
                holder.dubberMore.setVisibility(View.GONE);
            }
            else {
                holder.dubberMore.setVisibility(View.VISIBLE);
            }
            if (!dubberPictureUrl.equals("")) {
                Glide.with(context)
                        .load("http:" + dubberPictureUrl)
                        .asBitmap()
                        .placeholder(R.drawable.no_image_available)
                        .error(R.drawable.no_image_available)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(holder.dubberPicture);
            }
            else {
                Glide.clear(holder.dubberPicture);
                holder.dubberPicture.setImageResource(R.drawable.no_image_available);
            }
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

        TextView job;

        LinearLayout dubberLayout;
        ImageView dubberPicture;
        TextView dubberName;
        ImageView dubberMore;


        ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_subject_of_character);
            picture = (ImageView) itemView.findViewById(R.id.item_subject_picture);
            title = (TextView) itemView.findViewById(R.id.item_subject_title);

            job = (TextView) itemView.findViewById(R.id.item_character_job);

            dubberLayout = (LinearLayout) itemView.findViewById(R.id.layout_person);
            dubberPicture = (ImageView) itemView.findViewById(R.id.item_person_picture);
            dubberName = (TextView) itemView.findViewById(R.id.item_person_name);
            dubberMore = (ImageView) itemView.findViewById(R.id.item_person_more);
        }
    }

    public SubjectsOfCharacterAdapter(List<Subject> subjectList, int characterId) {
        this.subjectList = subjectList;
        this.characterId = characterId;
    }
}
