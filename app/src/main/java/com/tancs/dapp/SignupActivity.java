package com.tancs.dapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

public class SignupActivity extends BaseActivity {

    private String mUserID;
    private String mEmail;
    private String mUserName;
    private String mToken;

    EditText usernameView;
    EditText emailView;
    EditText passwordView;
    EditText confirmPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        SharedPreferences prefs = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
        mEmail = prefs.getString("email", "");
        mUserID = prefs.getString("user_id", "");
        mUserName = prefs.getString("user_name", "");
        mToken = prefs.getString("mobile_token", "");

        usernameView = (EditText) findViewById(R.id.edittext_signup_name);
        emailView = (EditText) findViewById(R.id.edittext_signup_email);
        passwordView = (EditText) findViewById(R.id.edittext_signup_password);
        confirmPasswordView = (EditText) findViewById(R.id.edittext_signup_confirmpassword);
    }

    public void clickSignup(View view) {
        requestSignup();
    }

    private void requestSignup() {
        String url = "http://192.168.1.101:3000/api/v1/users/";

        JSONObject params = new JSONObject();
        JSONObject user = new JSONObject();

        try {
            user.put("name", usernameView.getText().toString());
            user.put("email", emailView.getText().toString());
            user.put("password", passwordView.getText().toString());
            user.put("password_confirmation", confirmPasswordView.getText().toString());

            params.put("user", user);
        }
        catch(JSONException e) {
            Toast.makeText(SignupActivity.this, "Unable to create session data !", Toast.LENGTH_SHORT).show();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, params, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            mUserID = response.getString("id");
                            mUserName = response.getString("name");
                            mEmail = response.getString("email");
                            mToken = response.getString("mobile_token");

                            //Toast.makeText(LoginActivity.this, token, Toast.LENGTH_SHORT).show();

                            SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE).edit();
                            editor.putString("user_id", mUserID);
                            editor.putString("email", mEmail);
                            editor.putString("user_name", mUserName);
                            editor.putString("mobile_token", mToken);
                            editor.apply();

                            Toast.makeText(SignupActivity.this, "Sign up successful !", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(getBaseContext(), UsersProfileActivity.class);
                            intent.putExtra("id",mUserID);
                            startActivity(intent);
                        }
                        catch(JSONException e) {
                            Toast.makeText(SignupActivity.this, "Unable to parse data !", Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        NetworkResponse networkResponse = error.networkResponse;

                        if(networkResponse != null) {

                            switch (networkResponse.statusCode) {
                                case 422:       //unprocessable_entity
                                    String jsonError = new String(networkResponse.data);
                                    String errorString = "";
                                    JSONObject errorObject;
                                    JSONArray errors;
                                    try {
                                        errorObject = new JSONObject(jsonError);
                                        errors = errorObject.getJSONArray("errors");

                                        for (int i = 0; i < errors.length(); i++) {
                                            errorString += errors.get(i) + "\n";
                                        }

                                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SignupActivity.this);
                                        alertDialog.setTitle("Sign up error !");
                                        alertDialog.setMessage(errorString);
                                        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,int which) {

                                            }
                                        });
                                        alertDialog.show();
                                    }
                                    catch(JSONException e) {
                                        Toast.makeText(SignupActivity.this, "Unable to parse response data !", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                case 401:       //unauthorized
                                    Toast.makeText(SignupActivity.this, "Unable to authenticate request, please log in again", Toast.LENGTH_LONG).show();
                                    break;

                                default:
                                    Toast.makeText(SignupActivity.this, "Error code : " + networkResponse.statusCode, Toast.LENGTH_LONG).show();
                            }
                        }

                    }
                });

        // Access the RequestQueue through your singleton class.
        VolleySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
    }
}
