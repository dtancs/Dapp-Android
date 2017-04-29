package com.tancs.dapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import utils.VolleySingleton;

public class UsersProfileActivity extends AppCompatActivity {

    private String mUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_profile);

        mUserID = getIntent().getStringExtra("id");
        //Toast.makeText(this, mUserID, Toast.LENGTH_SHORT).show();

        requestUser();
    }

       private void requestUser() {
        String url = "http://192.168.1.101:3000/api/v1/users/" + mUserID;

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        /*TextView mTxtDisplay;
                        mTxtDisplay = (TextView) findViewById(R.id.users_textview);
                        mTxtDisplay.setText("Response: " + response.toString());*/

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        /*TextView mTxtDisplay;
                        mTxtDisplay = (TextView) findViewById(R.id.users_textview);
                        mTxtDisplay.setText("Response: Error retrieving data");*/
                    }
                });

        // Access the RequestQueue through your singleton class.
        VolleySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
    }
}
