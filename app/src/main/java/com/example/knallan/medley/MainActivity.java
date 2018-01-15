package com.example.knallan.medley;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    public static final String TAP_TO_SELECT_FOLDER = "TAP to select folder";
    public static final String TAP_TO_SIGNIN = "TAP to signin";

    Switch playFromFolderSwitch, playFromDriveSwitch;
    private MedleyExoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Restore preferences
        final SharedPreferences settings = getSharedPreferences(Utils.PREFS_NAME, 0);
        final SharedPreferences.Editor editor = settings.edit();

        // initiate view's
        playFromFolderSwitch = (Switch) findViewById(R.id.switch1);
        playFromDriveSwitch = (Switch) findViewById(R.id.switch2);
        final TextView folderpathView =  findViewById(R.id.folderPath);
        final TextView emailIdView =  findViewById(R.id.emailId);
        SeekBar medleyPlayTime =  findViewById(R.id.seekBar);


        boolean playFromFolder = settings.getBoolean("playFromFolder",false);
        boolean playFromDrive = settings.getBoolean("playFromDrive",false);

        playFromFolderSwitch.setChecked(playFromFolder);
        playFromDriveSwitch.setChecked(playFromDrive);
        final Intent intent = new Intent(this, GoogleSignInActivity.class);

        if(playFromFolder){
            folderpathView.setVisibility(View.VISIBLE);
            String folderPath = settings.getString("folderPath", TAP_TO_SELECT_FOLDER);
            folderpathView.setText(folderPath);
            LocalFileService.getInstance(settings);
        }
        if(playFromDrive){
            emailIdView.setVisibility(View.VISIBLE);
            String emailId = settings.getString("emailId", TAP_TO_SIGNIN);
            emailIdView.setText(emailId);
            intent.putExtra("Reload",true);
            startActivityForResult(intent,1);
        }

        playFromFolderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("playFromFolder", isChecked);
                // Commit the edits!
                editor.commit();
                if(isChecked){
                    folderpathView.setVisibility(View.VISIBLE);
                    String folderPath = settings.getString("folderPath", TAP_TO_SELECT_FOLDER);
                    folderpathView.setText(folderPath);
                    playFromDriveSwitch.setChecked(false);
                }else{
                    folderpathView.setVisibility(View.INVISIBLE);
                    playFromDriveSwitch.setChecked(true);
                }
            }
        });

        playFromDriveSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("playFromDrive", isChecked);
                // Commit the edits!
                editor.commit();
                if(isChecked){
                    emailIdView.setVisibility(View.VISIBLE);
                    String emailId = settings.getString("emailId", TAP_TO_SIGNIN);
                    emailIdView.setText(emailId);
                    playFromFolderSwitch.setChecked(false);
                }else{
                    emailIdView.setVisibility(View.INVISIBLE);
                    playFromFolderSwitch.setChecked(true);

                }
            }
        });


        folderpathView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String folderPath = settings.getString("folderPath", TAP_TO_SELECT_FOLDER);
                if(TAP_TO_SELECT_FOLDER.equalsIgnoreCase(folderPath)){
                    folderPath = Environment.getExternalStorageDirectory().getPath();
                    editor.putString("folderPath",folderPath );
                    editor.commit();
                    folderpathView.setText(folderPath);
                }
            }
        });

        emailIdView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String emailId = settings.getString("emailId", TAP_TO_SIGNIN);
                intent.putExtra("Reload",false);
                startActivityForResult(intent,1);
            }
        });


        if (!checkPermission()) {
            requestStoragePermission();
        }else{
            addButtonActions();
        }

        TextView title = findViewById(R.id.title);
        player = MedleyExoPlayer.getInstance();
        player.setTitleView(title);
        medleyPlayTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int minValue = 30;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                player.setMedleyPlayTime((progress+minValue)*1000);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private boolean checkPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }
    private void requestStoragePermission() {

        // Request the permission. The result will be received in onRequestPermissionResult().
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE

                },
                0);

    }
    /**
     * Shows a toast message.
     */
    protected void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void addButtonActions() {
        final Button buttonPlay = findViewById(R.id.play);
        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getSharedPreferences(Utils.PREFS_NAME, 0);
                boolean playFromFolder = settings.getBoolean("playFromFolder",false);
                boolean playFromDrive = settings.getBoolean("playFromDrive",false);

                if(playFromDrive==false && playFromFolder==false){
                    showMessage("Please select the source");
                }else{
                    medley();
                }


            }
        });

        final Button buttonStop = findViewById(R.id.stop);
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopMedley();
            }
        });
    }

    private void medley()  {
        try {
            player.play(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopMedley() {
        if (player != null)
            player.stop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                addButtonActions();
            } else {
                Log.i("Main", "Permission denied");
                showMessage("Permission denied.");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String emailId = data.getStringExtra("emailId");
                TextView emailIdView =  findViewById(R.id.emailId);
                emailIdView.setText(emailId);
                final SharedPreferences settings = getSharedPreferences(Utils.PREFS_NAME, 0);
                final SharedPreferences.Editor editor = settings.edit();
                editor.putString("emailId",emailId);
                editor.commit();
            }

        }
    }
}
