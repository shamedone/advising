package edu.sfsu.updater;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import edu.sfsu.db.CampusDB;
import edu.sfsu.db.DB;

public class Updater {

    private void update() {
        Properties props = new Properties();

        try {
            FileReader f = new FileReader("advising.properties");
            BufferedReader r = new BufferedReader(f);
            props.load(r);
            r.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        UpdatedCheckpointDB checkpointDB = (UpdatedCheckpointDB) DB.init(UpdatedCheckpointDB.class, "CHECKPOINT", props);
        CampusDB campusDB = (CampusDB) DB.init(CampusDB.class, "CAMPUS", props);

       checkpointDB.updateNames(campusDB);
    }

    public static void main(String[] args) {
        new Updater().update();
    }
}
