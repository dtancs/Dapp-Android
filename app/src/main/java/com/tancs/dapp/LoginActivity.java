package com.tancs.dapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utils.VolleySingleton;

public class LoginActivity extends BaseActivity {

    private String mEmail, mToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences prefs = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
        mEmail = prefs.getString("email", "");
        mToken = prefs.getString("mobile_token", "");

        if(mToken.isEmpty() == false) {
            Intent intent = new Intent(getBaseContext(), FeedActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void clickLogin(View view) {
        requestAuthentication();
    }

    public void clickSignup(View view){
        Intent intent = new Intent(getBaseContext(), SignupActivity.class);
        startActivity(intent);
    }

    private void requestAuthentication() {
        String url = "http://192.168.1.101:3000/api/v1/login/";

        JSONObject params = new JSONObject();
        JSONObject session = new JSONObject();

        EditText emailView = (EditText)findViewById(R.id.edittext_login_email);
        EditText passwordView = (EditText)findViewById(R.id.edittext_login_password);

        mEmail = emailView.getText().toString();

        try {
            session.put("email", mEmail);
            session.put("password", passwordView.getText().toString());

            params.put("session", session);
        }
        catch(JSONException e) {
            Toast.makeText(LoginActivity.this, "Unable to create session data !", Toast.LENGTH_SHORT).show();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, params, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            String user_id = response.getString("user_id");
                            String token = response.getString("mobile_token");
                            String name = response.getString("user_name");

                            //Toast.makeText(LoginActivity.this, token, Toast.LENGTH_SHORT).show();

                            SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE).edit();
                            editor.putString("email", mEmail);
                            editor.putString("mobile_token", token);
                            editor.putString("user_id", user_id);
                            editor.putString("user_name", name);
                            editor.apply();

                            Intent intent = new Intent(getBaseContext(), FeedActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        catch(JSONException e) {
                            Toast.makeText(LoginActivity.this, "Unable to parse data !", Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null && networkResponse.data != null) {
                            String jsonError = new String(networkResponse.data);
                            JSONObject errors;

                            try {
                                errors = new JSONObject(jsonError);
                                // Print Error!
                                Toast.makeText(LoginActivity.this, errors.getString("errors"), Toast.LENGTH_LONG).show();
                            }
                            catch(JSONException e) {
                                Toast.makeText(LoginActivity.this, "Unable to parse response data !", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "Unable to reach authentication server", Toast.LENGTH_LONG).show();
                        }
                    }
                });

        // Access the RequestQueue through your singleton class.
        VolleySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
    }
}
