package accesscontrol.persistence;

import org.hsqldb.persist.HsqlProperties;

public class Database {

    final String dbLocation = "../hsqldb-2.7.2/hsqldb/db/";
    org.hsqldb.server.Server server;

    public void startDBServer() {
        HsqlProperties props = new HsqlProperties();
        props.setProperty("server.database.0", "file:" + dbLocation + "AccessControlDB;");
        props.setProperty("server.dbname.0", "AccessControlDB");
        server = new org.hsqldb.Server();
        try {
            server.setProperties(props);
        } catch (Exception e) {
            return;
        }
        server.start();
    }

    public void stopDBServer() {
        server.shutdown();
    }

}

