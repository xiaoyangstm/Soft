package com.example.a6yue9hao_app;

import com.example.a6yue9hao_app.databinding.ActivityTeamBinding;

import simple.app.SimpleActivity;

public class TeamActivity extends SimpleActivity {
    private ActivityTeamBinding binding;
    @Override
    protected void onCreate() {

        binding = ActivityTeamBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnDeviceControl.setOnClickListener(v->{
            startActivity(ActivityControl.class);
        });

        binding.btnEnvironmentMonitor.setOnClickListener(v->{
            startActivity(Activityshow.class);
        });

        binding.btnThresholdSettings.setOnClickListener(v->{
            startActivity(AcitivityYuzhi.class);
        });

    }
}
