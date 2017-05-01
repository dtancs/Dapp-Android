package com.tancs.dapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tancs.dapp.R;
import com.tancs.dapp.models.Micropost;

import java.util.List;

import utils.GravatarHelper;

/**
 * Created by tancs on 5/1/17.
 */

public class FeedListAdapter extends RecyclerView.Adapter<FeedListAdapter.FeedListViewHolder>{

    private List<Micropost> listPosts;
    private LayoutInflater inflater;

    private FeedListAdapter.ItemClickCallback itemClickCallback;

    public interface ItemClickCallback {
        void onItemClick(int p);
    }

    public void setItemClickCallback(final FeedListAdapter.ItemClickCallback itemClickCallback) {
        this.itemClickCallback = itemClickCallback;
    }

    public FeedListAdapter (List<Micropost> listData, Context c){
        this.inflater = LayoutInflater.from(c);
        this.listPosts = listData;
    }

    @Override
    public FeedListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.listitem_micropost,parent,false);

        return new FeedListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FeedListViewHolder holder, int position) {
        Micropost post = listPosts.get(position);
        holder.name.setText(post.getName());
        holder.content.setText(post.getContent());
        holder.timeago.setText(post.getCreated_time_ago());

        Picasso.with(this.inflater.getContext())
                .load(GravatarHelper.getImageURL(post.getEmail()))
                .placeholder(R.drawable.placeholder_avatar)
                .error(R.drawable.placeholder_avatar)
                .into(holder.avatar);
    }

    @Override
    public int getItemCount() {
        return listPosts.size();
    }

    class FeedListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private View container;
        private TextView name;
        private TextView content;
        private TextView timeago;
        private ImageView avatar;

        public FeedListViewHolder(View itemView) {
            super(itemView);

            container = itemView.findViewById(R.id.container_root_micropostlist);
            container.setOnClickListener(this);

            name = (TextView)itemView.findViewById(R.id.textview_micropost_user_name);
            content = (TextView)itemView.findViewById(R.id.textview_micropost_content);
            timeago = (TextView)itemView.findViewById(R.id.textview_micropost_timeago);
            avatar = (ImageView)itemView.findViewById(R.id.imageview_micropost_user_avatar);

        }

        @Override
        public void onClick(View v){
            if (v.getId() == R.id.container_root_micropostlist){
                itemClickCallback.onItemClick(getAdapterPosition());
            }
        }
    }
}
