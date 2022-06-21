package pt.ua.deti.icm.android.health_spike.connectivity;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class ConnectivityHelper {

    private final Context context;

    public ConnectivityHelper(Context context) {
        this.context = context;
    }

    public Set<String> getNodes() {

        List<Node> nodes;

        Log.d("HealthSpike", "Getting all nodes");

        try {
            nodes = Tasks.await(Wearable.getNodeClient(context).getConnectedNodes());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return new HashSet<>();
        }

        Log.d("HealthSpike", "Nodes amount: " + nodes.size());

        return nodes.stream().map(Node::getId).collect(Collectors.toSet());

    }

    public void sendMessage(String messagePath, String nodeId, String message) {

        if (nodeId == null) {
            Log.d("HealthSpike", "Error forwarding message - node id was null.");
            return;
        }

        Task<Integer> sendTask = Wearable.getMessageClient(context).sendMessage(nodeId, messagePath, message.getBytes(StandardCharsets.UTF_8));

        sendTask.addOnSuccessListener(integer -> Log.d("HealthSpike", "Successfully sent message."));
        sendTask.addOnFailureListener(e -> Log.e("HealthSpike", "Error sending message."));

    }

}
