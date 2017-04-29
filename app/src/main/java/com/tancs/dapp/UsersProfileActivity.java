package com.tancs.dapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;
import com.tancs.dapp.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import utils.GravatarHelper;
import utils.VolleySingleton;

public class UsersProfileActivity extends AppCompatActivity {

    private String mUserID;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_profile);

        mUserID = getIntent().getStringExtra("id");
        //Toast.makeText(this, mUserID, Toast.LENGTH_SHORT).show();

        requestUser();

    }

        private void populateUser() {
            TextView mUserNameView = (TextView) findViewById(R.id.textview_users_profile_name);
            mUserNameView.setText(mUser.getName());

            ImageView mUserAvatarView = (ImageView) findViewById(R.id.imageview_users_profile_avatar);
            Picasso.with(UsersProfileActivity.this)
                    .load(GravatarHelper.getImageURL(mUser.getEmail()))
                    .placeholder(R.drawable.placeholder_avatar)
                    .error(R.drawable.placeholder_avatar)
                    .into(mUserAvatarView);
        }

       private void requestUser() {
        String url = "http://192.168.1.101:3000/api/v1/users/" + mUserID;

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            mUser = new User(response.getString("id"), response.getString("name"), response.getString("email"), response.getString("created_at"), response.getString("updated_at"));
                            populateUser();
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
