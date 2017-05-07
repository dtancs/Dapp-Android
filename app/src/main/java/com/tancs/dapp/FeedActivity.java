package com.tancs.dapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.squareup.picasso.Picasso;
import com.tancs.dapp.adapters.FeedListAdapter;
import com.tancs.dapp.models.Micropost;
import com.tancs.dapp.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import utils.GravatarHelper;
import utils.VolleySingleton;

public class FeedActivity extends BaseActivity implements FeedListAdapter.ItemClickCallback{

    private RecyclerView mRecView;
    private FeedListAdapter mRecAdapter;

    private String mUserID;
    private String mEmail;

    private List<Micropost> mPostlist = new ArrayList<>();

    private boolean isRefresh = false;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Feed");

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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_feed_newpost);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getBaseContext(), NewMicropostActivity.class);
                intent.putExtra("previous_activity","FeedActivity");
                startActivity(intent);
            }
        });

        mProgressBar = (ProgressBar) findViewById(R.id.progressbar_feed);

        SharedPreferences prefs = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
        mEmail = prefs.getString("email", "");
        mUserID = prefs.getString("user_id", "");

        requestUsersList();
    }

    @Override
    public void onResume() {
        super.onResume();

        if(mRecAdapter != null) {
            isRefresh = true;
            mPostlist.clear();
            requestUsersList();
        }
    }

    @Override
    protected void onStop () {
        super.onStop();

        VolleySingleton.getInstance(this).getRequestQueue().cancelAll("FeedActivity");
    }

    @Override
    public void onItemClick(int p) {
        Micropost item = (Micropost) mPostlist.get(p);
        //Toast.makeText(this, item.getId(), Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, UsersProfileActivity.class);
        intent.putExtra("id",item.getUser_id());
        startActivity(intent);
    }

    private void populatePosts() {
        mRecView = (RecyclerView)findViewById(R.id.recyclerview_feed_list);
        mRecView.setLayoutManager(new LinearLayoutManager(this));
        mRecAdapter = new FeedListAdapter(mPostlist,this);
        mRecView.setAdapter(mRecAdapter);
        mRecAdapter.setItemClickCallback(this);
    }

    private void requestUsersList() {
        mProgressBar.setVisibility(View.VISIBLE);

        String url = getString(R.string.apiBaseURL) + "/api/v1/feed/" + mUserID;

        JsonArrayRequest request = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {

                        mProgressBar.setVisibility(View.GONE);

                        for(int i = 0; i < jsonArray.length(); i++) {
                            try {

                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Micropost newPost = new Micropost();

                                newPost.setId(jsonObject.getString("id"));
                                newPost.setUser_id(jsonObject.getString("user_id"));
                                newPost.setContent(jsonObject.getString("content"));
                                newPost.setCreated_time_ago(jsonObject.getString("created_at"));
                                newPost.setEmail(jsonObject.getString("user_email"));
                                newPost.setName(jsonObject.getString("user_name"));

                                mPostlist.add(newPost);
                            }
                            catch(JSONException e) {
                                Toast.makeText(FeedActivity.this, "Unable to parse data !", Toast.LENGTH_SHORT).show();
                            }
                        }

                        if(isRefresh == false) {
                            populatePosts();
                        }
                        else{
                            mRecAdapter.notifyDataSetChanged();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        mProgressBar.setVisibility(View.GONE);
                        Toast.makeText(FeedActivity.this, "Unable to fetch data: " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


        // Access the RequestQueue through your singleton class.
        request.setTag("FeedActivity");
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }
}
