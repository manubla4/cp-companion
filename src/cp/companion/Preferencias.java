package cp.companion;

public class Preferencias {

    private static Preferencias pref = null;
    public String ip = null;
    public String tcp = null;
    public String user = null;
    public String password = null;
    public String databaseName = null;
    public boolean instance = false;

    public static Preferencias GetInstance() {
        if (pref == null) {
             pref = new Preferencias();
        }
        return pref;
    }
}
