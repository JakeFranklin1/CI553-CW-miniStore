package ci553.ministore.clients;

import ci553.ministore.clients.start.App;
import ci553.ministore.clients.start.AutoSetup;

public class Main {
    public static void main(String[] args) {
        AutoSetup.setRunAuto(false);
        App.main(args);
    }
}
