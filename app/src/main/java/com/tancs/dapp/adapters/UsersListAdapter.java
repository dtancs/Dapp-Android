package com.tancs.dapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tancs.dapp.R;
import com.tancs.dapp.models.User;

import java.util.List;

/**
 * Created by tancs on 4/28/17.
 */

public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.UsersListViewHolder>{

    private List<User> listUsers;
    private LayoutInflater inflater;

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

    }

    @Override
    public int getItemCount() {
        return listUsers.size();
    }

    class UsersListViewHolder extends RecyclerView.ViewHolder{

        private TextView name;
        private ImageView avatar;
        private View container;

        public UsersListViewHolder(View itemView) {
            super(itemView);

            name = (TextView)itemView.findViewById(R.id.textview_user_name);
            avatar = (ImageView)itemView.findViewById(R.id.imageview_user_avatar);
        }
    }
}
