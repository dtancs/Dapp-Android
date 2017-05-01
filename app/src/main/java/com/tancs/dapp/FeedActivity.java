package com.tancs.dapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.squareup.picasso.Picasso;
import com.tancs.dapp.adapters.FeedListAdapter;
import com.tancs.dapp.models.Micropost;
import com.tancs.dapp.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import utils.GravatarHelper;
import utils.VolleySingleton;

public class FeedActivity extends AppCompatActivity implements FeedListAdapter.ItemClickCallback{

    private RecyclerView mRecView;
    private FeedListAdapter mRecAdapter;

    private String mUserID;
    private String mEmail;

    private List<Micropost> mPostlist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        SharedPreferences prefs = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
        mEmail = prefs.getString("email", "");
        mUserID = prefs.getString("user_id", "");

        requestUsersList();
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
        String url = "http://192.168.1.101:3000/api/v1/feed/" + mUserID;

        JsonArrayRequest request = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {

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

                        populatePosts();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(FeedActivity.this, "Unable to fetch data: " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


        // Access the RequestQueue through your singleton class.
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }
}
