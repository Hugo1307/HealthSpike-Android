package pt.ua.deti.icm.android.health_spike.body_activity;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import pt.ua.deti.icm.android.health_spike.connectivity.ConnectivityHelper;
import pt.ua.deti.icm.android.health_spike.connectivity.ConnectivityTopics;

public class SyncBodyActivityWorker extends Worker {

    public SyncBodyActivityWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        String value = BodyActivityHandler.getInstance().getActivityStatus() != null ? BodyActivityHandler.getInstance().getActivityStatus().toString() : BodyActivityStatus.LOW_ACTIVITY.toString();

        new Thread(() -> {
            ConnectivityHelper connectivityHelper = new ConnectivityHelper(getApplicationContext());
            connectivityHelper.getNodes()
                    .stream()
                    .findFirst()
                    .ifPresent(nodeId -> connectivityHelper.sendMessage(ConnectivityTopics.ACTIVITY_STATUS_TOPIC.getTopic(), nodeId, value));
        }).start();

        Log.i("HealthSpike_Worker", "Sending activity status data.");

        return Result.success();

    }

}
