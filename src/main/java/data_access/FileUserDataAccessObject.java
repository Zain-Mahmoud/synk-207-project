package data_access;

import entities.User;
import entities.UserFactory;
import use_case.gateways.UserGateway;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * DAO for user data implemented using a File to persist the data.
 */
public class FileUserDataAccessObject implements UserGateway {

    private static final String HEADER = "uid,username,avatarpath,password";

    private final File csvFile;
    private final Map<String, Integer> headers = new LinkedHashMap<>();
    private final Map<String, User> usersByName = new HashMap<>();
    private final Map<String, User> usersByUid = new HashMap<>();


    public FileUserDataAccessObject(String csvPath, UserFactory userFactory) throws IOException {

        csvFile = new File(csvPath);
        headers.put("uid", 0);
        headers.put("username", 1);
        headers.put("avatarpath", 2);
        headers.put("password", 3);

        if (csvFile.length() == 0) {
            save();
        }
        else {

            try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
                final String header = reader.readLine();

                if (!header.equals(HEADER)) {
                    throw new RuntimeException(String.format("header should be%n: %s%n but was:%n%s", HEADER, header));
                }

                String row;
                while ((row = reader.readLine()) != null) {
                    final String[] col = row.split(",");
                    final String uid = String.valueOf(col[headers.get("uid")]);
                    final String username = String.valueOf(col[headers.get("username")]);
                    final String avatarpath = String.valueOf(col[headers.get("avatarpath")]);
                    final String password = String.valueOf(col[headers.get("password")]);

                    final User user = userFactory.create(uid, username, avatarpath, password);
                    usersByName.put(username, user);
                    usersByUid.put(uid, user);
                }
            }
        }
    }

    private void save() {
        final BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(csvFile));
            writer.write(String.join(",", headers.keySet()));
            writer.newLine();

            for (User user : usersByUid.values()) {
                final String line = String.format("%s,%s,%s,%s",
                        user.getUid(),
                        user.getUsername(),
                        user.getAvatarPath(),
                        user.getPassword());
                writer.write(line);
                writer.newLine();
            }

            writer.close();

        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean existsByUid(String uid) {
        return usersByUid.containsKey(uid);
    }

    @Override
    public boolean existsByName(String identifier) {
        return usersByName.containsKey(identifier);
    }

    @Override
    public void updateUser(String oldUID, User user) {

    }

    @Override
    public void deleteUser(String uid) {

    }

    @Override
    public User getByUid(String uid) {
        return usersByUid.get(uid);
    }

    @Override
    public User getByName(String username) {
        return usersByName.get(username);
    }

    @Override
    public boolean isUsernameTaken(String username) {
        for (User u : usersByName.values()) {
            if (u.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void save(User user) {
        usersByName.put(user.getUsername(), user);
        usersByUid.put(user.getUid(), user);
        this.save();
    }
}
