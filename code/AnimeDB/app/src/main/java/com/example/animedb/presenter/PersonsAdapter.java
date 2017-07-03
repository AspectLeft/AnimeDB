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
import com.example.animedb.model.Person;
import com.example.animedb.ui.PersonActivity;

import java.util.List;

/**
 * Created by Fang on 2017/5/23.
 *
 */

public class PersonsAdapter extends RecyclerView.Adapter<PersonsAdapter.ViewHolder> {
    private Context context;

    private List<Person> personList;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context == null) {
            context = parent.getContext();
        }
        View view = LayoutInflater.from(context).inflate(R.layout.item_person, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = holder.getAdapterPosition();
                Person person = personList.get(position);
                Intent intent = new Intent(context, PersonActivity.class);
                intent.putExtra("id", person.getId());

                context.startActivity(intent);
            }
        });


        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Person person = personList.get(position);
        holder.name.setText(person.getPersonName());
        holder.brief.setText("");
        String sex = person.getSex();
        if (!sex.equals("")) {
            holder.brief.append("性别 " + sex);
        }
        String birthday = person.getBirthday();
        if (!birthday.equals("")) {
            holder.brief.append(" / 生日 " + birthday);
        }
        String duty = person.getDuty();
        if (duty.equals("")) {
            String job = person.getJob();
            if (job.equals("")) {
                holder.duty.setVisibility(View.GONE);
            }
            else {
                holder.duty.setText(job);
            }
        }
        else {
            holder.duty.setText(duty.substring(0, duty.length() - 1));
        }

        String pictureUrl = person.getPictureUrl();
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
        return personList.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView picture;
        TextView name;
        TextView brief;
        TextView duty;


        ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_person);
            picture = (ImageView) itemView.findViewById(R.id.item_person_picture);
            name = (TextView) itemView.findViewById(R.id.item_person_name);
            brief = (TextView) itemView.findViewById(R.id.item_person_brief);
            duty = (TextView) itemView.findViewById(R.id.item_person_duty);
        }
    }

    public PersonsAdapter(List<Person> personList) {
        this.personList = personList;
    }
}
