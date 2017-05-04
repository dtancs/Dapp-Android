package com.tancs.dapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.tancs.dapp.adapters.UsersListAdapter;
import com.tancs.dapp.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import utils.VolleySingleton;

public class RelationshipActivity extends BaseActivity implements UsersListAdapter.ItemClickCallback{

    private String mTargetID;
    private String mActionType;

    private List<User> mUserslist = new ArrayList<User>();

    private RecyclerView mRecView;
    private UsersListAdapter mRecAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relationship);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Users");

        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(myToolbar)
                .withTranslucentStatusBar(false)
                .withSelectedItem(-1)
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

        mTargetID = getIntent().getStringExtra("id");
        mActionType = getIntent().getStringExtra("action_type");

        requestRelationshipsList();
    }

    @Override
    public void onItemClick(int p) {
        User item = mUserslist.get(p);
        //Toast.makeText(this, item.getId(), Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, UsersProfileActivity.class);
        intent.putExtra("id",item.getId());
        startActivity(intent);
    }

    private void populateUsersList(){
        mRecView = (RecyclerView)findViewById(R.id.recyclerview_relationship_list);
        mRecView.setLayoutManager(new LinearLayoutManager(this));
        mRecAdapter = new UsersListAdapter(mUserslist,this);
        mRecView.setAdapter(mRecAdapter);
        mRecAdapter.setItemClickCallback(this);
    }

    private void requestRelationshipsList() {
        String url = "http://192.168.1.101:3000/api/v1/relationships/" + mTargetID;

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            JSONArray followingArray = response.getJSONArray("following");
                            JSONArray followersArray = response.getJSONArray("followers");

                            if(mActionType.contentEquals("Followers")) {
                                for(int i = 0; i < followersArray.length(); i++) {
                                    try {

                                        JSONObject jsonObject = followersArray.getJSONObject(i);

                                        User tmpUser = new User();
                                        tmpUser.setId(jsonObject.getString("user_id"));
                                        tmpUser.setName(jsonObject.getString("name"));
                                        tmpUser.setEmail(jsonObject.getString("email"));

                                        mUserslist.add(tmpUser);
                                    }
                                    catch(JSONException e) {
                                        Toast.makeText(RelationshipActivity.this, "Unable to parse data !", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            else {

                                for(int i = 0; i < followingArray.length(); i++) {
                                    try {

                                        JSONObject jsonObject = followingArray.getJSONObject(i);

                                        User tmpUser = new User();
                                        tmpUser.setId(jsonObject.getString("user_id"));
                                        tmpUser.setName(jsonObject.getString("name"));
                                        tmpUser.setEmail(jsonObject.getString("email"));

                                        mUserslist.add(tmpUser);
                                    }
                                    catch(JSONException e) {
                                        Toast.makeText(RelationshipActivity.this, "Unable to parse data !", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            populateUsersList();
                        }
                        catch(JSONException e) {
                            Toast.makeText(RelationshipActivity.this, "Unable to parse data !", Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        NetworkResponse networkResponse = error.networkResponse;

                        if(networkResponse != null) {

                            switch (networkResponse.statusCode) {
                                default:
                                    Toast.makeText(RelationshipActivity.this, "Error code : " + networkResponse.statusCode, Toast.LENGTH_LONG).show();
                            }
                        }

                    }
                });


        // Access the RequestQueue through your singleton class.
        VolleySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
    }
}
