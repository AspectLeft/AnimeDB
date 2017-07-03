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
import com.example.animedb.model.Character;
import com.example.animedb.ui.CharacterActivity;

import java.util.List;

/**
 * Created by Fang on 2017/6/6.
 *
 */

public class CharactersAdapter extends RecyclerView.Adapter<CharactersAdapter.ViewHolder> {

    private Context context;

    private List<Character> characterList;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context == null) {
            context = parent.getContext();
        }
        View view = LayoutInflater.from(context).inflate(R.layout.item_character, parent, false);
        final CharactersAdapter.ViewHolder holder = new CharactersAdapter.ViewHolder(view);
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


        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Character character = characterList.get(position);
        holder.name.setText(character.getCharacterName());
        holder.brief.setText("");
        String sex = character.getSex();
        if (!sex.equals("")) {
            holder.brief.append("性别 " + sex);
        }
        String job = character.getJob();
        if (job.equals("")) {
            holder.duty.setVisibility(View.GONE);
        }
        else {
            holder.duty.setText(job);
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

        ViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.card_character);
            picture = (ImageView) itemView.findViewById(R.id.item_character_picture);
            name = (TextView) itemView.findViewById(R.id.item_character_name);
            brief = (TextView) itemView.findViewById(R.id.item_character_brief);
            duty = (TextView) itemView.findViewById(R.id.item_character_job);
        }
    }

    public CharactersAdapter(List<Character> characterList) {
        this.characterList = characterList;
    }
}
