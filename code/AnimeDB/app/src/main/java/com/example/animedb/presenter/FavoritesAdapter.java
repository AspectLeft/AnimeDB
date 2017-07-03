package com.example.animedb.presenter;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.animedb.R;
import com.example.animedb.model.AnimeDBHelper;
import com.example.animedb.model.Character;
import com.example.animedb.model.Favorite;
import com.example.animedb.model.Person;
import com.example.animedb.model.Subject;
import com.example.animedb.ui.CharacterActivity;
import com.example.animedb.ui.PersonActivity;
import com.example.animedb.ui.SubjectActivity;

import java.util.List;

/**
 * Created by Fang on 2017/5/21.
 *
 */

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

    private Context context;

    private List<Favorite> favoriteList;

    private AnimeDBHelper dbHelper;


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context == null) {
            context = parent.getContext();
        }
        View view = LayoutInflater.from(context).inflate(R.layout.item_favorite, parent, false);
        final FavoritesAdapter.ViewHolder holder = new FavoritesAdapter.ViewHolder(view);
        holder.cardView.findViewById(R.id.base_favorite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = holder.getAdapterPosition();
                Favorite favorite = favoriteList.get(position);
                Intent intent;
                switch (favorite.getType()) {
                    case Favorite.TYPE_SUBJECT:
                        intent = new Intent(context, SubjectActivity.class);
                        intent.putExtra("id", favorite.getObjectId());

                        context.startActivity(intent);
                        break;
                    case Favorite.TYPE_PERSON:
                        intent = new Intent(context, PersonActivity.class);
                        intent.putExtra("id", favorite.getObjectId());

                        context.startActivity(intent);
                        break;
                    case Favorite.TYPE_CHARACTER:
                        intent = new Intent(context, CharacterActivity.class);
                        intent.putExtra("id", favorite.getObjectId());
                        context.startActivity(intent);
                }
            }
        });
        holder.cardView.findViewById(R.id.base_favorite).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final int position = holder.getAdapterPosition();
                final Favorite favorite = favoriteList.get(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("是否删除收藏？")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                favorite.removeFromFavorites(dbHelper);
                                favoriteList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, getItemCount());
                            }
                        })
                        .show();

                return true;
            }
        });
        holder.mark.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final Favorite favorite = favoriteList.get(holder.getAdapterPosition());
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                ContentValues values = new ContentValues();
                values.put("mark", position);
                db.update("Favorites", values, "id = ?", new String[]{String.valueOf(favorite.getId())});
                db.close();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        holder.editComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Favorite favorite = favoriteList.get(holder.getAdapterPosition());
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LinearLayout layout = (LinearLayout) View.inflate(context, R.layout.dialog_favorite_edit_comment, null);
                final EditText editText = (EditText) layout.findViewById(R.id.favorite_edit_comment);
                editText.setText(favorite.getComment());
                editText.selectAll();
                builder.setView(layout)
                        .setTitle("修改备注")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String newComment = editText.getText().toString();
                                favorite.setComment(newComment);
                                holder.comment.setText(newComment);
                                SQLiteDatabase db = dbHelper.getReadableDatabase();
                                ContentValues values = new ContentValues();
                                values.put("comment", newComment);
                                db.update("Favorites", values, "id = ?", new String[]{String.valueOf(favorite.getId())});
                                db.close();
                            }
                        })
                        .show();
            }
        });

        //TODO


        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Favorite favorite = favoriteList.get(position);
        String pictureUrl;
        String nameCHS;
        switch (favorite.getType()) {
            case Favorite.TYPE_SUBJECT:
                Subject subject = favorite.getSubject();
                String titleCHS = subject.getTitleCHS();
                if (titleCHS.equals("")) {
                    holder.title.setText(subject.getTitle());
                }
                else {
                    holder.title.setText(titleCHS);
                }
                pictureUrl = subject.getPictureUrl();
                if (!pictureUrl.equals("")) {

                    Glide.with(context)
                            .load("http:" + pictureUrl)
                            .asBitmap()
                            .error(R.drawable.no_image_available)
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .into(holder.picture);
                }
                holder.mark.setSelection(favorite.getMark());
                holder.comment.setText(favorite.getComment());
                break;
            case Favorite.TYPE_PERSON:
                Person person = favorite.getPerson();
                nameCHS = person.getNameCHS();
                if (nameCHS.equals("")) {
                    holder.title.setText(person.getPersonName());
                }
                else {
                    holder.title.setText(nameCHS);
                }
                pictureUrl = person.getPictureUrl();
                if (!pictureUrl.equals("")) {

                    Glide.with(context)
                            .load("http:" + pictureUrl)
                            .asBitmap()
                            .error(R.drawable.no_image_available)
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .into(holder.picture);
                }
                holder.mark.setVisibility(View.GONE);
                holder.comment.setText(favorite.getComment());

                break;
            case Favorite.TYPE_CHARACTER:
                Character character = favorite.getCharacter();
                nameCHS = character.getNameCHS();
                if (nameCHS.equals("")) {
                    holder.title.setText(character.getCharacterName());
                }
                else {
                    holder.title.setText(nameCHS);
                }
                pictureUrl = character.getPictureUrl();
                if (!pictureUrl.equals("")) {
                    Glide.with(context)
                            .load("http:" + pictureUrl)
                            .asBitmap()
                            .error(R.drawable.no_image_available)
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .into(holder.picture);
                }
                holder.mark.setVisibility(View.GONE);
                holder.comment.setText(favorite.getComment());

                break;
        }

    }

    @Override
    public int getItemCount() {
        return favoriteList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView picture;
        TextView title;
        Spinner mark;
        TextView comment;
        ImageView editComment;

        ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_favorite);
            picture = (ImageView) itemView.findViewById(R.id.item_favorite_picture);
            title = (TextView) itemView.findViewById(R.id.item_favorite_title);
            mark = (Spinner) itemView.findViewById(R.id.item_favorite_mark);
            comment = (TextView) itemView.findViewById(R.id.item_favorite_comment);
            editComment = (ImageView) itemView.findViewById(R.id.item_favorite_edit_comment);
        }
    }

    public FavoritesAdapter(List<Favorite> favoriteList, AnimeDBHelper dbHelper) {
        this.favoriteList = favoriteList;
        this.dbHelper = dbHelper;
    }
}
