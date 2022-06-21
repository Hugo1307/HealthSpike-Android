package pt.ua.deti.icm.android.health_spike;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import pt.ua.deti.icm.android.health_spike.viewmodels.SettingsViewModel;
import pt.ua.deti.icm.android.health_spike.viewmodels.StepsViewModel;

public class SettingsActivity extends AppCompatActivity {

    public static String settingsSharedPreferences = "SettingsPreferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences prefs = getSharedPreferences(SettingsActivity.settingsSharedPreferences, MODE_PRIVATE);

        SwitchCompat heartRateMonitorEnabled = findViewById(R.id.heartRateMonitorEnabled);
        SwitchCompat activityMonitorEnabled = findViewById(R.id.activity_monitor_enabled_switch);

        EditText activityMonitorCoolDown = findViewById(R.id.activity_monitor_coolDown_edittext);
        EditText heartRateMonitorCoolDown = findViewById(R.id.heartRateNotificationsCoolDownEdittext);
        EditText stepsDailyGoal = findViewById(R.id.steps_daily_goal_edittext);

        heartRateMonitorEnabled.setChecked(prefs.getBoolean("HR_Monitor_Enabled", true));
        activityMonitorEnabled.setChecked(prefs.getBoolean("Activity_Monitor_Enabled", true));

        activityMonitorCoolDown.setText(String.valueOf(prefs.getInt("Activity_Monitor_CoolDown", 60)));
        heartRateMonitorCoolDown.setText(String.valueOf(prefs.getInt("HR_Monitor_CoolDown", 30)));
        stepsDailyGoal.setText(String.valueOf(prefs.getInt("Steps_Daily_Goal", 5000)));

        findViewById(R.id.btnSaveSettings).setOnClickListener(view -> {

            SharedPreferences.Editor editor = getSharedPreferences(settingsSharedPreferences, MODE_PRIVATE).edit();

            editor.putBoolean("HR_Monitor_Enabled", heartRateMonitorEnabled.isChecked());

            Log.i("HealthSpike", "HR Monitor: " + heartRateMonitorEnabled.isChecked());

            editor.putBoolean("Activity_Monitor_Enabled", activityMonitorEnabled.isChecked());

            editor.putInt("Activity_Monitor_CoolDown", Integer.parseInt(activityMonitorCoolDown.getText().toString()));
            editor.putInt("HR_Monitor_CoolDown", Integer.parseInt(heartRateMonitorCoolDown.getText().toString()));
            editor.putInt("Steps_Daily_Goal", Integer.parseInt(stepsDailyGoal.getText().toString()));

            editor.apply();

            new ViewModelProvider(this).get(SettingsViewModel.class)
                    .getDailyGoal()
                    .postValue(Integer.parseInt(stepsDailyGoal.getText().toString()));

            Toast.makeText(this, "Your settings were updated!", Toast.LENGTH_SHORT).show();

            finish();

        });

    }

}