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
import com.example.animedb.model.Person;
import com.example.animedb.ui.CharacterActivity;
import com.example.animedb.ui.MakersActivity;
import com.example.animedb.ui.PersonActivity;

import java.util.List;

/**
 * Created by Fang on 2017/6/15.
 *
 */

public class CharactersOfSubjectAdapter extends RecyclerView.Adapter<CharactersOfSubjectAdapter.ViewHolder> {
    private Context context;

    private int subjectId;
    private List<Character> characterList;
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context == null) {
            context = parent.getContext();
        }
        View view = LayoutInflater.from(context).inflate(R.layout.item_character_of_subject, parent, false);
        final ViewHolder holder = new ViewHolder(view);
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
        holder.dubberLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = holder.getAdapterPosition();
                Character character = characterList.get(position);
                Intent intent = new Intent(context, PersonActivity.class);
                intent.putExtra("id", character.getDubbers().get(0).getId());
                context.startActivity(intent);
            }
        });
        holder.dubberMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = holder.getAdapterPosition();
                Character character = characterList.get(position);
                Intent intent = new Intent(context, MakersActivity.class);
                intent.putExtra("subjectId", subjectId);
                intent.putExtra("characterId", character.getId());
                context.startActivity(intent);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Character character = characterList.get(position);
        String sex = character.getSex();
        String job = character.getJob();
        List<Person> dubbers = character.getDubbers();

        holder.name.setText(character.getCharacterName());
        holder.brief.setText("");

        if (!sex.equals("")) {
            holder.brief.append("性别 " + sex);
        }
        if (job.equals("")) {
            holder.duty.setVisibility(View.GONE);
        }
        else {
            holder.duty.setText(job);
        }


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
        TextView brief;
        TextView duty;

        LinearLayout dubberLayout;
        ImageView dubberPicture;
        TextView dubberName;
        ImageView dubberMore;

        ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_character_of_subject);
            picture = (ImageView) itemView.findViewById(R.id.item_character_picture);
            name = (TextView) itemView.findViewById(R.id.item_character_name);
            brief = (TextView) itemView.findViewById(R.id.item_character_brief);
            duty = (TextView) itemView.findViewById(R.id.item_character_job);

            dubberLayout = (LinearLayout) itemView.findViewById(R.id.layout_person);
            dubberPicture = (ImageView) itemView.findViewById(R.id.item_person_picture);
            dubberName = (TextView) itemView.findViewById(R.id.item_person_name);
            dubberMore = (ImageView) itemView.findViewById(R.id.item_person_more);
        }
    }

    public CharactersOfSubjectAdapter(List<Character> characterList, int subjectId) {
        this.characterList = characterList;
        this.subjectId = subjectId;
    }
}
