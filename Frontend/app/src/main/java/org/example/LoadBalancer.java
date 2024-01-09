package org.example;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class LoadBalancer {
    private final int numberOfReplicas;
    private final String serverName;
    private int basePort;
    private int serverIndex;
    private final ArrayList<String> servers;

    public LoadBalancer(String serverName, int basePort, int numberOfReplicas){
        this.serverName = serverName;
        this.basePort = basePort;
        this.numberOfReplicas = numberOfReplicas;
        this.servers = new ArrayList<>();

        try {
            InetAddress[] addresses = InetAddress.getAllByName(serverName);

            if (addresses.length > 0) {
                for (InetAddress address : addresses) {
                    servers.add("http://"+address.getHostAddress()+":"+this.basePort);
                }
            } else {
                System.out.println("No IP addresses found for " + serverName);
            }
        } catch (UnknownHostException e) {
            System.out.println("DNS lookup failed for " + serverName + ": " + e.getMessage());
        }
        this.basePort = basePort;
        serverIndex = 0;
    }

    public synchronized String getServer(){
        String serverName = servers.get(serverIndex);
        serverIndex++;
        if(serverIndex == numberOfReplicas)
            serverIndex = 0;
        return serverName;
    }

}
