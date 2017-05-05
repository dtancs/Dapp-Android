package com.tancs.dapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import utils.VolleySingleton;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void switchActivity(int selection) {
        //Toast.makeText(BaseActivity.this, String.valueOf(selection), Toast.LENGTH_SHORT).show();

        Intent intent;
        SharedPreferences prefs = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);

        switch (selection){
            case 0:
                intent = new Intent(getBaseContext(), FeedActivity.class);
                startActivity(intent);
                break;
            case 1:
                intent = new Intent(getBaseContext(), UsersProfileActivity.class);
                intent.putExtra("id",prefs.getString("user_id", ""));
                startActivity(intent);
                break;
            case 2:
                intent = new Intent(getBaseContext(), UsersListActivity.class);
                startActivity(intent);
                break;
            case 3:
                intent = new Intent(getBaseContext(), RelationshipActivity.class);
                intent.putExtra("id",prefs.getString("user_id", ""));
                intent.putExtra("action_type","Followers");
                startActivity(intent);
                break;
            case 4:
                intent = new Intent(getBaseContext(), RelationshipActivity.class);
                intent.putExtra("id",prefs.getString("user_id", ""));
                intent.putExtra("action_type","Following");
                startActivity(intent);
                break;
            default:
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                requestLogout();
                return true;

            case R.id.action_settings:
                Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void requestLogout() {

        String user_id;
        String token;

        SharedPreferences prefs = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
        user_id = prefs.getString("user_id", "");
        token = prefs.getString("mobile_token", "");

        String url = getString(R.string.apiBaseURL) + "/api/v1/logout/" + user_id + "/" + token;

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(BaseActivity.this, "Logout succesfully", Toast.LENGTH_SHORT).show();
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
                                //Toast.makeText(BaseActivity.this, errors.getString("errors"), Toast.LENGTH_LONG).show();
                            }
                            catch(JSONException e) {
                                //Toast.makeText(BaseActivity.this, "Unable to parse response data !", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            //Toast.makeText(BaseActivity.this, "Unable to reach authentication server", Toast.LENGTH_LONG).show();
                        }
                    }
                });

        // Access the RequestQueue through your singleton class.
        VolleySingleton.getInstance(this).addToRequestQueue(jsObjRequest);

        // Empty the Shared Preferences
        SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE).edit();
        editor.putString("email", "");
        editor.putString("mobile_token", "");
        editor.putString("user_id", "");
        editor.apply();

        Intent intent = new Intent(getBaseContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
