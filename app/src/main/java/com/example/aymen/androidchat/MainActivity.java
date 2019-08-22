package com.example.aymen.androidchat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.nkzawa.socketio.client.IO;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import java.net.URISyntaxException;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{


    public static final String NICKNAME = "usernickname";
    public static final String SECRET_KEY= "secretkey";

    private SharedPreferences mPreferences;
    private String sharedPrefFile = "com.example.android.hellosharedprefs";

    private SignInButton signIn;
    public static GoogleApiClient googleApiClient;
    private static final int REQ_CODE = 9001;

    private EditText secretKey;

    public static Intent background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        secretKey = (EditText) findViewById(R.id.secretKey);
        signIn = findViewById(R.id.signIn);
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API,signInOptions).build();

        //call UI component  by id
        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        Log.i("Nickname_problem", mPreferences.getString(ChatBoxActivity.NICKNAME_KEY, "nullable"));
        String nick = mPreferences.getString(ChatBoxActivity.NICKNAME_KEY, "nullable");
        String key = mPreferences.getString(SECRET_KEY, "nullable");
        Log.i("Secret_key_debug",key);


        if (!nick.equals("nullable")) {
            //Toast.makeText(getApplicationContext(),nick,Toast.LENGTH_LONG).show();
            if(AppBackgroundService.isRunning)
            {
                Intent in = new Intent(this,ChatBoxActivity.class);
                startActivity(in);
                MainActivity.this.finish();
            }
            else {
                background = new Intent(MainActivity.this, AppBackgroundService.class);
                background.putExtra(NICKNAME, nick);
                background.putExtra(SECRET_KEY, key);
                startService(background);
                MainActivity.this.finish();
            }
        }


        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(secretKey.getText()))
                {
                    Toast.makeText(getApplicationContext(),"Please Enter Secret Key",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    signIn();
                }

            }
        });

    }

    private void signIn()
    {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent, REQ_CODE);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQ_CODE)
        {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResult(result);
        }
    }

    private void handleResult(GoogleSignInResult result)
    {
        if(result.isSuccess())
        {
            GoogleSignInAccount account = result.getSignInAccount();
            String name = account.getDisplayName();
            String email = account.getEmail();


            background = new Intent(MainActivity.this, AppBackgroundService.class);
            background.putExtra(NICKNAME,name);
            background.putExtra(SECRET_KEY,secretKey.getText().toString().trim());
            Log.i("Secret_key_debug",secretKey.getText().toString().trim());
            startService(background);
            MainActivity.this.finish();

            //Toast.makeText(getApplicationContext(), name, Toast.LENGTH_LONG).show();

        }
        else
        {
            Toast.makeText(getApplicationContext(), result.getStatus().getStatusMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
