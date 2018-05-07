package cp.companion;

public class Preferences {

    private static Preferences selfInstance = null;
    public String ip = "";
    public String tcp = "";
    public String user = "";
    public String password = "";
    public String databaseName = "";
    public boolean instance = false;
    public boolean DBconfigured = false;
    public boolean DBConnected = false;
    public long time = 3600000;   // 60000 ms = 1 minuto  /  3600000 ms = 1 hora

    public static Preferences GetInstance() {
        if (selfInstance == null) {
             selfInstance = new Preferences();
        }
        return selfInstance;
    }
}
