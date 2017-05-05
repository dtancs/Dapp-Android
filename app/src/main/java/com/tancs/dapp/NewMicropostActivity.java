package com.tancs.dapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import utils.GravatarHelper;
import utils.VolleySingleton;

public class NewMicropostActivity extends BaseActivity {

    private String mUserID;
    private String mEmail;
    private String mUserName;
    private String mToken;

    private MenuItem mCount;

    private String mPreviousActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_micropost);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("New post");

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

        mPreviousActivity = getIntent().getStringExtra("previous_activity");

        SharedPreferences prefs = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
        mEmail = prefs.getString("email", "");
        mUserID = prefs.getString("user_id", "");
        mUserName = prefs.getString("user_name", "");
        mToken = prefs.getString("mobile_token", "");

        TextView mNameView = (TextView) findViewById(R.id.textview_newpost_name);
        mNameView.setText(mUserName);

        ImageView mAvatarView = (ImageView) findViewById(R.id.imageview_newpost_avatar);
        Picasso.with(NewMicropostActivity.this)
                .load(GravatarHelper.getImageURL(mEmail))
                .placeholder(R.drawable.placeholder_avatar)
                .error(R.drawable.placeholder_avatar)
                .into(mAvatarView);

        EditText contentText = (EditText) findViewById(R.id.edittext_newpost_content);
        TextWatcher mTextEditorWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //This sets a textview to the current length
                mCount.setTitle(String.valueOf(140 - s.length()));
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        contentText.addTextChangedListener(mTextEditorWatcher);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_post, menu);

        mCount = menu.findItem(R.id.action_count);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_post:
                requestPost();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void requestPost() {
        String url = "http://192.168.1.101:3000/api/v1/microposts";

        JSONObject params = new JSONObject();
        JSONObject session = new JSONObject();

        EditText contentView = (EditText)findViewById(R.id.edittext_newpost_content);


        try {
            session.put("id", mUserID);
            session.put("mobile_token", mToken);

            params.put("session", session);
            params.put("content", contentView.getText());
        }
        catch(JSONException e) {
            Toast.makeText(NewMicropostActivity.this, "Unable to create session data !", Toast.LENGTH_SHORT).show();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, params, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        Toast.makeText(NewMicropostActivity.this, "Post successful!", Toast.LENGTH_SHORT).show();

                        if(mPreviousActivity.equals("FeedActivity")){
                            finish();
                        }
                        else {
                            Intent intent = new Intent(getBaseContext(), UsersProfileActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("id",mUserID);
                            startActivity(intent);
                            finish();
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
                                Toast.makeText(NewMicropostActivity.this, errors.getString("errors"), Toast.LENGTH_LONG).show();
                            }
                            catch(JSONException e) {
                                Toast.makeText(NewMicropostActivity.this, "Unable to parse response data !", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText(NewMicropostActivity.this, "Unable to reach authentication server", Toast.LENGTH_LONG).show();
                        }
                    }
                });

        // Access the RequestQueue through your singleton class.
        VolleySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
    }

}
