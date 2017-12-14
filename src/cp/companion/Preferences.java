package cp.companion;

public class Preferences {

    private static Preferences selfInstance = null;
    public String ip = null;
    public String tcp = null;
    public String user = null;
    public String password = null;
    public String databaseName = null;
    public boolean instance = false;
    public boolean DBconfigured = false;
    public long time = 15000;   //60000 ms = 1 minuto

    public static Preferences GetInstance() {
        if (selfInstance == null) {
             selfInstance = new Preferences();
        }
        return selfInstance;
    }
}
