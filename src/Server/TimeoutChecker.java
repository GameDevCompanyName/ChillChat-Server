package Server;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class TimeoutChecker {
    Map<String, Integer> clients;
    Broadcaster broadcaster;

    public TimeoutChecker(Broadcaster broadcaster){

        clients = new HashMap<>();
        this.broadcaster = broadcaster;
        Timer timer = new Timer();
        timer.schedule(new Counter(), 0, 1000);

    }


    public void count(){
        for (Map.Entry<String, Integer> clientEntry: clients.entrySet()){
            String name = clientEntry.getKey();
            Integer timer = clientEntry.getValue();

            if(timer>=0){
                timer++;
                if(timer>29)
                    sendEcho(name);
                else
                    clients.replace(name, timer);
            }
            else{
                timer--;
                if(timer<-10)
                    deleteClient(name);
                else
                    clients.replace(name, timer);
            }

        }
    }


    public void addClient(String name){
        if(!clients.containsKey(name))
            clients.put(name, 0);
    }

    public void deleteClient(String name){
        clients.remove(name);
        broadcaster.getConnectionByLogin(name).disconnect("timeout");
        broadcaster.disconnectClient(name);
    }

    public void refreshCounter(String name){
        if(name!=null)
            if(clients.containsKey(name))
                clients.replace(name, 0);
    }

    public void sendEcho(String name){
        if(broadcaster.getConnectionByLogin(name)==null)
            return;
        broadcaster.sendConnectionMessage(name, ServerMessage.serverPingSend());
        clients.replace(name, -1);
    }

    private class Counter extends TimerTask{

        @Override
        public void run() {
            count();
        }
    }
}

