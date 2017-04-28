package com.tancs.dapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.tancs.dapp.adapters.UsersListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.tancs.dapp.adapters.UsersListAdapter;
import com.tancs.dapp.models.User;

import utils.VolleySingleton;

public class UsersListActivity extends AppCompatActivity {

    private ArrayList<String> mEntries;
    private List<User> mUserslist = new ArrayList<User>();

    private RecyclerView mRecView;
    private UsersListAdapter mRecAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);

        requestUsersList();


    }

    private void populateUsersList(){
        mRecView = (RecyclerView)findViewById(R.id.recyclerview_users_list);
        mRecView.setLayoutManager(new LinearLayoutManager(this));
        mRecAdapter = new UsersListAdapter(mUserslist,this);
        mRecView.setAdapter(mRecAdapter);
    }

/*    private void requestUser() {
        String url = "http://192.168.1.101:3000/api/v1/users/1";

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        TextView mTxtDisplay;
                        mTxtDisplay = (TextView) findViewById(R.id.users_textview);

                        mTxtDisplay.setText("Response: " + response.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        TextView mTxtDisplay;
                        mTxtDisplay = (TextView) findViewById(R.id.users_textview);

                        mTxtDisplay.setText("Response: Error retrieving data");
                    }
                });

        // Access the RequestQueue through your singleton class.
        VolleySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
    }*/

    private void requestUsersList() {
        String url = "http://192.168.1.101:3000/api/v1/users";

        JsonArrayRequest request = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {

                        for(int i = 0; i < jsonArray.length(); i++) {
                            try {
                                /*JSONObject jsonObject = jsonArray.getJSONObject(i);
                                mEntries.add(jsonObject.toString());

                                TextView mTxtDisplay;
                                mTxtDisplay = (TextView) findViewById(R.id.users_textview);

                                mTxtDisplay.setText(jsonObject.toString());*/

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

        mEntries = new ArrayList<>();

        // Access the RequestQueue through your singleton class.
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }
}
