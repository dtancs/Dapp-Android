package com.tancs.dapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
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

    private String mToken;
    private String mUserID;
    private String mTargetID;
    private User mTargetUser;
    private List<Micropost> mPostlist = new ArrayList<>();

    private TextView mUserNameView;
    private TextView mFollowersView;
    private TextView mFollowingView;
    private Button mFollowButton;

    JsonObjectRequest jsObjRequest;
    JsonObjectRequest jsObjRequestFollow;

    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_profile);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(myToolbar);

        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(myToolbar)
                .withTranslucentStatusBar(false)
                .withSelectedItem(-1)
                .withCloseOnClick(true)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Feed"),
                        new PrimaryDrawerItem().withName("Profile"),
                        new PrimaryDrawerItem().withName("Users"),
                        new PrimaryDrawerItem().withName("Followers"),
                        new PrimaryDrawerItem().withName("Following")

                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switchActivity(position);
                        return true;
                    }
                })
                .build();

        SharedPreferences prefs = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
        mUserID = prefs.getString("user_id", "");
        mTargetID = getIntent().getStringExtra("id");
        mToken = prefs.getString("mobile_token", "");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_users_profile_newpost);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getBaseContext(), NewMicropostActivity.class);
                intent.putExtra("previous_activity","UsersProfileActivity");
                startActivity(intent);
            }
        });

        mProgressBar = (ProgressBar) findViewById(R.id.progressbar_users_profile);

        mFollowButton = (Button) findViewById(R.id.button_users_profile_followbutton);
        mFollowersView = (TextView) findViewById(R.id.textview_users_profile_followers);
        mFollowingView = (TextView) findViewById(R.id.textview_users_profile_following);

        if(mUserID.equals(mTargetID) == false){
            fab.setEnabled(false);
            fab.setVisibility(View.GONE);
        }
        else {
            mFollowButton.setEnabled(false);
            mFollowButton.setVisibility(View.INVISIBLE);
        }

        requestUser();

    }

    @Override
    public void onResume() {
        super.onResume();

        if(mUserNameView != null) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }

    @Override
    protected void onStop () {
        super.onStop();

         if(jsObjRequest != null){
             jsObjRequest.cancel();
         }

        if(jsObjRequestFollow != null){
            jsObjRequestFollow.cancel();
        }

    }

    public void clickFollowers(View view) {
        Intent intent = new Intent(getBaseContext(), RelationshipActivity.class);
        intent.putExtra("id",mTargetID);
        intent.putExtra("action_type","Followers");
        startActivity(intent);
    }
    public void clickFollowing(View view) {
        Intent intent = new Intent(getBaseContext(), RelationshipActivity.class);
        intent.putExtra("id",mTargetID);
        intent.putExtra("action_type","Following");
        startActivity(intent);
    }

    public void clickFollow(View view) {
        if(mFollowButton.getText().toString().contentEquals("Follow")) {
            requestFollow("Follow");
        }
        else {
            requestFollow("Unfollow");
        }
    }

        private void populateUser() {
            mUserNameView = (TextView) findViewById(R.id.textview_users_profile_name);
            mUserNameView.setText(mTargetUser.getName());
            getSupportActionBar().setTitle(mTargetUser.getName());

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

        mProgressBar.setVisibility(View.VISIBLE);
        String url = getString(R.string.apiBaseURL) + "/api/v1/users/" + mTargetID;

        jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        mProgressBar.setVisibility(View.GONE);

                        try {
                            mTargetUser = new User(response.getString("id"), response.getString("name"), response.getString("email"), response.getString("created_at"), response.getString("updated_at"));
                            populateUser();

                            mFollowersView.setText("Followers\n" + response.getString("followers_count"));
                            mFollowingView.setText("Following\n" + response.getString("following_count"));

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

                            JSONArray mFollowers = response.getJSONArray("followers");
                            for(int i = 0; i < mFollowers.length(); i++) {
                                try {
                                        JSONObject jsonObject = mFollowers.getJSONObject(i);

                                        if(mUserID.contentEquals(jsonObject.getString("id"))){
                                            mFollowButton.setText("Unfollow");
                                    }

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

                        mProgressBar.setVisibility(View.GONE);
                        Toast.makeText(UsersProfileActivity.this, "Unable to to retrieve user data !", Toast.LENGTH_SHORT).show();
                    }
                });

        // Access the RequestQueue through your singleton class.
        jsObjRequest.setTag("requestUser");
        VolleySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
    }

    private void requestFollow(final String actionType) {
        String url = getString(R.string.apiBaseURL) + "/api/v1/relationship";

        JSONObject params = new JSONObject();
        JSONObject relationship = new JSONObject();

        try {
            relationship.put("current_user_id", mUserID);
            relationship.put("target_user_id", mTargetID);
            relationship.put("action_type", actionType);

            params.put("relationship", relationship);
            params.put("mobile_token", mToken);
        }
        catch(JSONException e) {
            Toast.makeText(UsersProfileActivity.this, "Unable to create session data !", Toast.LENGTH_SHORT).show();
        }

        jsObjRequestFollow = new JsonObjectRequest
                (Request.Method.POST, url, params, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                    try {
                        mFollowersView.setText("Followers\n" + response.getString("target_user_followers_count"));
                        mFollowingView.setText("Following\n" +response.getString("target_user_following_count"));

                        if(actionType.contentEquals("Follow")) {
                            mFollowButton.setText("Unfollow");
                        }
                        else {
                            mFollowButton.setText("Follow");
                        }
                    }
                    catch(JSONException e) {
                        Toast.makeText(UsersProfileActivity.this, "Unable to parse data !", Toast.LENGTH_SHORT).show();
                    }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        NetworkResponse networkResponse = error.networkResponse;

                        if(networkResponse != null) {

                            switch (networkResponse.statusCode) {

                                case 401:       //unauthorized
                                    Toast.makeText(UsersProfileActivity.this, "Unable to authenticate request, please log in again", Toast.LENGTH_LONG).show();
                                    break;

                                default:
                                    Toast.makeText(UsersProfileActivity.this, "Error code : " + networkResponse.statusCode, Toast.LENGTH_LONG).show();
                            }
                        }

                    }
                });

        // Access the RequestQueue through your singleton class.
        jsObjRequestFollow.setTag("requestFollow");
        VolleySingleton.getInstance(this).addToRequestQueue(jsObjRequestFollow);
    }
}
