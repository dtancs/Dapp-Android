package com.tancs.dapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tancs.dapp.MainActivity;
import com.tancs.dapp.R;
import com.tancs.dapp.models.User;
import utils.GravatarHelper;

import java.util.List;

/**
 * Created by tancs on 4/28/17.
 */

public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.UsersListViewHolder>{



    private List<User> listUsers;
    private LayoutInflater inflater;

    private ItemClickCallback itemClickCallback;

    public interface ItemClickCallback {
        void onItemClick(int p);
    }

    public void setItemClickCallback(final ItemClickCallback itemClickCallback) {
        this.itemClickCallback = itemClickCallback;
    }

    public UsersListAdapter (List<User> listData, Context c){
        this.inflater = LayoutInflater.from(c);
        this.listUsers = listData;
    }

    @Override
    public UsersListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.listitem_users_list,parent,false);

        return new UsersListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UsersListViewHolder holder, int position) {
        User user = listUsers.get(position);
        holder.name.setText(user.getName());

        Picasso.with(this.inflater.getContext())
                .load(GravatarHelper.getImageURL(user.getEmail()))
                .placeholder(R.drawable.placeholder_avatar)
                .error(R.drawable.placeholder_avatar)
                .into(holder.avatar);
    }

    @Override
    public int getItemCount() {
        return listUsers.size();
    }

    class UsersListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private View container;
        private TextView name;
        private ImageView avatar;

        public UsersListViewHolder(View itemView) {
            super(itemView);

            container = itemView.findViewById(R.id.container_root_userslist);
            container.setOnClickListener(this);

            name = (TextView)itemView.findViewById(R.id.textview_user_name);
            avatar = (ImageView)itemView.findViewById(R.id.imageview_user_avatar);

        }

        @Override
        public void onClick(View v){
            if (v.getId() == R.id.container_root_userslist){
                itemClickCallback.onItemClick(getAdapterPosition());
            }
        }
    }
}
