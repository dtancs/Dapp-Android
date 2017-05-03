package com.tancs.dapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;
import com.tancs.dapp.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import com.tancs.dapp.models.Micropost;

import utils.GravatarHelper;
import utils.VolleySingleton;

public class UsersProfileActivity extends BaseActivity {


    private String mUserID;
    private String mTargetID;
    private User mTargetUser;
    private List<Micropost> mPostlist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_profile);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(myToolbar);

        SharedPreferences prefs = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
        mUserID = prefs.getString("user_id", "");
        mTargetID = getIntent().getStringExtra("id");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_users_profile_newpost);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getBaseContext(), NewMicropostActivity.class);
                startActivity(intent);
            }
        });

        if(mUserID.equals(mTargetID) == false){
            fab.setEnabled(false);
            fab.setVisibility(View.GONE);
        }

        //Toast.makeText(this, mTargetID, Toast.LENGTH_SHORT).show();

        requestUser();

        /*View view = getLayoutInflater().inflate(R.layout.listitem_micropost,null);
        LinearLayout myView = (LinearLayout)findViewById(R.id.linearlayout_users_profile);
        myView.addView(view);*/
    }

        private void populateUser() {
            TextView mUserNameView = (TextView) findViewById(R.id.textview_users_profile_name);
            mUserNameView.setText(mTargetUser.getName());

            ImageView mUserAvatarView = (ImageView) findViewById(R.id.imageview_users_profile_avatar);
            Picasso.with(UsersProfileActivity.this)
                    .load(GravatarHelper.getImageURL(mTargetUser.getEmail()))
                    .placeholder(R.drawable.placeholder_avatar)
                    .error(R.drawable.placeholder_avatar)
                    .into(mUserAvatarView);
        }

        private void populatePosts() {
            for(int i = 0; i < mPostlist.size(); i++) {
                View view = getLayoutInflater().inflate(R.layout.listitem_micropost,null);
                LinearLayout myView = (LinearLayout)findViewById(R.id.linearlayout_users_profile);
                myView.addView(view);

                TextView mMicropostUserNameView = (TextView) view.findViewById(R.id.textview_micropost_user_name);
                mMicropostUserNameView.setText(mTargetUser.getName());

                TextView mMicropostTimeAgoView = (TextView) view.findViewById(R.id.textview_micropost_timeago);
                mMicropostTimeAgoView.setText(mPostlist.get(i).getCreated_time_ago());

                TextView mMicropostContentView = (TextView) view.findViewById(R.id.textview_micropost_content);
                mMicropostContentView.setText(mPostlist.get(i).getContent());

                ImageView mUserPostAvatarView = (ImageView) view.findViewById(R.id.imageview_micropost_user_avatar);
                Picasso.with(UsersProfileActivity.this)
                        .load(GravatarHelper.getImageURL(mTargetUser.getEmail()))
                        .placeholder(R.drawable.placeholder_avatar)
                        .error(R.drawable.placeholder_avatar)
                        .into(mUserPostAvatarView);
            }
        }

       private void requestUser() {
        String url = "http://192.168.1.101:3000/api/v1/users/" + mTargetID;

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            mTargetUser = new User(response.getString("id"), response.getString("name"), response.getString("email"), response.getString("created_at"), response.getString("updated_at"));
                            populateUser();

                            JSONArray mPosts = response.getJSONArray("microposts");
                            for(int i = 0; i < mPosts.length(); i++) {
                                try {

                                    JSONObject jsonObject = mPosts.getJSONObject(i);
                                    Micropost newPost = new Micropost();

                                    newPost.setId(jsonObject.getString("id"));
                                    newPost.setUser_id(jsonObject.getString("user_id"));
                                    newPost.setContent(jsonObject.getString("content"));
                                    newPost.setCreated_time_ago(jsonObject.getString("created_at"));

                                    mPostlist.add(newPost);

                                }
                                catch(JSONException e) {
                                    Toast.makeText(UsersProfileActivity.this, "Unable to parse posts data !", Toast.LENGTH_SHORT).show();
                                }
                            }
                            populatePosts();

                        }
                        catch(JSONException e) {
                            Toast.makeText(UsersProfileActivity.this, "Unable to parse data !", Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(UsersProfileActivity.this, "Unable to to retrieve user data !", Toast.LENGTH_SHORT).show();
                    }
                });

        // Access the RequestQueue through your singleton class.
        VolleySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
    }
}
