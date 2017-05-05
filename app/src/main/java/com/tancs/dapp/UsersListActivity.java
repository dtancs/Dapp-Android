package com.tancs.dapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.tancs.dapp.adapters.UsersListAdapter;
import com.tancs.dapp.models.User;

import utils.VolleySingleton;

public class UsersListActivity extends BaseActivity implements UsersListAdapter.ItemClickCallback {

    private List<User> mUserslist = new ArrayList<User>();

    private RecyclerView mRecView;
    private UsersListAdapter mRecAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);

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

        requestUsersList();
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
        mRecView = (RecyclerView)findViewById(R.id.recyclerview_users_list);
        mRecView.setLayoutManager(new LinearLayoutManager(this));
        mRecAdapter = new UsersListAdapter(mUserslist,this);
        mRecView.setAdapter(mRecAdapter);
        mRecAdapter.setItemClickCallback(this);
    }

    private void requestUsersList() {
        String url = getString(R.string.apiBaseURL) + "/api/v1/users";

        JsonArrayRequest request = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {

                        for(int i = 0; i < jsonArray.length(); i++) {
                            try {

                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                mUserslist.add(new User(jsonObject.getString("id"),jsonObject.getString("name"),jsonObject.getString("email"),jsonObject.getString("created_at"),jsonObject.getString("updated_at")));

                                populateUsersList();
                            }
                            catch(JSONException e) {
                                Toast.makeText(UsersListActivity.this, "Unable to parse data !", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(UsersListActivity.this, "Unable to fetch data: " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


        // Access the RequestQueue through your singleton class.
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }
}
