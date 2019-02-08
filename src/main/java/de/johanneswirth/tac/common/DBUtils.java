package de.johanneswirth.tac.common;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

import java.io.*;
import java.util.logging.Level;

import static de.johanneswirth.tac.common.Utils.LOGGER;

public class DBUtils {

    public static void updateDB(Jdbi jdbi, int exptectedVersion) {
        jdbi.withHandle(handle -> {
            int currentVersion = handle.createQuery("SELECT * FROM version").mapToBean(Integer.class).findOnly();
            for (int version = currentVersion + 1; version <= exptectedVersion; version++) {
                File file = new File("sql/update_" + version + ".sql");
                executeFile(handle, file);
            }
            return null;
        });
    }

    public static void initDB(Jdbi jdbi) {
        File file = new File("sql/init.sql");
        jdbi.withHandle(handle -> {
            executeFile(handle, file);
            return null;
        });
    }

    private static void executeFile(Handle handle, File file) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                handle.execute(line);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "", e);
        }
    }
}
