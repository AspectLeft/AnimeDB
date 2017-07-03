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
import com.example.animedb.model.Character;
import com.example.animedb.model.Subject;
import com.example.animedb.ui.CharacterActivity;
import com.example.animedb.ui.SubjectActivity;
import com.example.animedb.ui.WorksActivity;

import java.util.List;

/**
 * Created by Fang on 2017/6/16.
 *
 */

public class CharactersOfPersonAdapter extends RecyclerView.Adapter<CharactersOfPersonAdapter.ViewHolder> {
    private Context context;

    private int personId;
    private List<Character> characterList;


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context == null) {
            context = parent.getContext();
        }
        View view = LayoutInflater.from(context).inflate(R.layout.item_character_of_person, parent, false);
        final CharactersOfPersonAdapter.ViewHolder holder = new CharactersOfPersonAdapter.ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = holder.getAdapterPosition();
                Character character = characterList.get(position);
                Intent intent = new Intent(context, CharacterActivity.class);
                intent.putExtra("id", character.getId());
                context.startActivity(intent);
            }
        });
        holder.subjectLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = holder.getAdapterPosition();
                Character character = characterList.get(position);
                Intent intent = new Intent(context, SubjectActivity.class);
                intent.putExtra("id", character.getSubjects().get(0).getId());
                context.startActivity(intent);
            }
        });
        holder.subjectMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = holder.getAdapterPosition();
                Character character = characterList.get(position);
                Intent intent = new Intent(context, WorksActivity.class);
                intent.putExtra("characterId", character.getId());
                intent.putExtra("personId", personId);
                context.startActivity(intent);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Character character = characterList.get(position);

        holder.name.setText(character.getCharacterName());

        List<Subject> subjects = character.getSubjects();
        if (subjects.isEmpty()) {
            holder.subjectLayout.setVisibility(View.GONE);
        }
        else {
            holder.subjectLayout.setVisibility(View.VISIBLE);
            Subject firstSubject = subjects.get(0);
            holder.subjectTitle.setText(firstSubject.getTitle());
            String subjectPictureUrl = firstSubject.getPictureUrl();
            if (subjects.size() == 1) {
                holder.subjectMore.setVisibility(View.GONE);
            }
            else {
                holder.subjectMore.setVisibility(View.VISIBLE);
            }
            if (!subjectPictureUrl.equals("")) {
                Glide.with(context)
                        .load("http:" + subjectPictureUrl)
                        .asBitmap()
                        .placeholder(R.drawable.no_image_available)
                        .error(R.drawable.no_image_available)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(holder.subjectPicture);
            }
            else {
                Glide.clear(holder.subjectPicture);
                holder.subjectPicture.setImageResource(R.drawable.no_image_available);
            }
            String job = firstSubject.getDuty();
            if (job.equals("")) {
                holder.duty.setVisibility(View.GONE);
            }
            else {
                holder.duty.setText(job);
            }
        }

        String pictureUrl = character.getPictureUrl();
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
    }

    @Override
    public int getItemCount() {
        return characterList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView picture;
        TextView name;
        TextView duty;

        LinearLayout subjectLayout;
        ImageView subjectPicture;
        TextView subjectTitle;
        ImageView subjectMore;

        ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_character_of_person);
            picture = (ImageView) itemView.findViewById(R.id.item_character_picture);
            name = (TextView) itemView.findViewById(R.id.item_character_name);
            duty = (TextView) itemView.findViewById(R.id.item_character_job);

            subjectLayout = (LinearLayout) itemView.findViewById(R.id.layout_subject);
            subjectPicture = (ImageView) itemView.findViewById(R.id.item_subject_picture);
            subjectTitle = (TextView) itemView.findViewById(R.id.item_subject_title);
            subjectMore = (ImageView) itemView.findViewById(R.id.item_subject_more);

        }
    }

    public CharactersOfPersonAdapter(List<Character> characterList, int personId) {
        this.characterList = characterList;
        this.personId = personId;
    }
}
