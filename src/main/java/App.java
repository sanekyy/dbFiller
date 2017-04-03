import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by ihb on 01.04.17.
 */

public class App {

    private static String SCRIPT_PATH = "/home/ihb/Documents/Projects/IdeaProjects/dbFiller/src/main/resources/script";

    private static String NICKNAMES_PATH = "/home/ihb/Documents/Projects/IdeaProjects/dbFiller/src/main/resources/nicknames";
    private static String EMAILS_PATH = "/home/ihb/Documents/Projects/IdeaProjects/dbFiller/src/main/resources/emails";
    private static String PASSWORDS_PATH = "/home/ihb/Documents/Projects/IdeaProjects/dbFiller/src/main/resources/passwords";
    private static String FIRST_NAMES_PATH = "/home/ihb/Documents/Projects/IdeaProjects/dbFiller/src/main/resources/firstNames";
    private static String LAST_NAMES_PATH = "/home/ihb/Documents/Projects/IdeaProjects/dbFiller/src/main/resources/lastNames";

    private static File file;

    static Random rand = new Random(System.currentTimeMillis());

    public static void main(String[] args) throws IOException {

        initFile();

        clearDB();

        createTables();

        generateUsers();

    }

    private static void initFile() throws IOException {
        file = FileUtils.getFile(SCRIPT_PATH);
        try {
            FileUtils.forceDelete(file);
        } catch (Exception ignored){

        }
        FileUtils.touch(file);
    }

    private static void clearDB() throws IOException {
        FileUtils.write(file,"DROP DATABASE poll;\n" +
                "CREATE DATABASE poll;\n" +
                "USE poll;\n"
        , Charset.defaultCharset(), true);
    }

    private static void createTables() throws IOException {
        String user = "CREATE TABLE IF NOT EXISTS\n" +
                "    `user` (\n" +
                "        `id` INT NOT NULL AUTO_INCREMENT,\n" +
                "        `nickname` VARCHAR(100) NOT NULL UNIQUE,\n" +
                "        `password` VARCHAR(100) NOT NULL,\n" +
                "        `email` VARCHAR(100) NOT NULL UNIQUE,\n" +
                "        `first_name` VARCHAR(100) NOT NULL,\n" +
                "        `last_name` VARCHAR(100) NOT NULL,\n" +
                "        `date_of_registration` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                "        `date_of_last_visit` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                "        PRIMARY KEY(`id`)\n" +
                "    );\n";

        String country = "CREATE TABLE IF NOT EXISTS\n" +
                "    `country` (\n" +
                "        `id` SMALLINT(3) NOT NULL AUTO_INCREMENT,\n" +
                "        `name` VARCHAR(100) NOT NULL,\n" +
                "        `code` SMALLINT(3) UNSIGNED NOT NULL UNIQUE,\n" +
                "        PRIMARY KEY(`id`)\n" +
                "    );\n";

        String questionnaire = "CREATE TABLE IF NOT EXISTS\n" +
                "    `questionnaire` (\n" +
                "        `id` INT NOT NULL AUTO_INCREMENT,\n" +
                "        `user_id` INT NOT NULL UNIQUE,\n" +
                "        `age` TINYINT(3) UNSIGNED,\n" +
                "        `sex` TINYINT(1) UNSIGNED,\n" +
                "        `country_id` SMALLINT(3),\n" +
                "        PRIMARY KEY(`id`),\n" +
                "        FOREIGN KEY (user_id) REFERENCES user(id)\n" +
                "    );\n";

        String preference = "CREATE TABLE IF NOT EXISTS\n" +
                "    `preference` (\n" +
                "        `id` INT NOT NULL AUTO_INCREMENT,\n" +
                "        `name` VARCHAR(100) NOT NULL UNIQUE,\n" +
                "        PRIMARY KEY(`id`)\n" +
                "    );\n";

        String user_preferences = "CREATE TABLE IF NOT EXISTS\n" +
                "    `user_preferences` (\n" +
                "        `id` INT NOT NULL AUTO_INCREMENT,\n" +
                "        `preference_id` INT,\n" +
                "        `user_id` INT NOT NULL,\n" +
                "        PRIMARY KEY(`id`),\n" +
                "        FOREIGN KEY (user_id) REFERENCES user(id),\n" +
                "        FOREIGN KEY (preference_id) REFERENCES preference(id)\n" +
                "    );\n";

        String device = "CREATE TABLE IF NOT EXISTS\n" +
                "    `device` (\n" +
                "        `id` INT NOT NULL AUTO_INCREMENT,\n" +
                "        `type` VARCHAR(100) NOT NULL,\n" +
                "        `name` VARCHAR(100) NOT NULL,\n" +
                "        PRIMARY KEY(`id`)\n" +
                "    );\n";

        String token = "CREATE TABLE IF NOT EXISTS\n" +
                "    `token` (\n" +
                "        `id` INT NOT NULL AUTO_INCREMENT,\n" +
                "        `user_id` INT NOT NULL,\n" +
                "        `key` VARCHAR(100) NOT NULL UNIQUE,\n" +
                "        `end_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                "        `creation_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                "        `device_id` INT NOT NULL,\n" +
                "        PRIMARY KEY(`id`),\n" +
                "        FOREIGN KEY (user_id) REFERENCES user(id),\n" +
                "        FOREIGN KEY (device_id) REFERENCES device(id)\n" +
                "    );\n";

        String permission = "CREATE TABLE IF NOT EXISTS\n" +
                "    `permission` (\n" +
                "        `id` INT NOT NULL AUTO_INCREMENT,\n" +
                "        `name` VARCHAR(100) NOT NULL UNIQUE,\n" +
                "        PRIMARY KEY(`id`)\n" +
                "    );\n";

        String token_permissions = "CREATE TABLE IF NOT EXISTS\n" +
                "    `token_permissions` (\n" +
                "        `id` INT NOT NULL AUTO_INCREMENT,\n" +
                "        `token_id` INT NOT NULL,\n" +
                "        `permission_id` INT NOT NULL,\n" +
                "        PRIMARY KEY(`id`),\n" +
                "        FOREIGN KEY (token_id) REFERENCES token(id),\n" +
                "        FOREIGN KEY (permission_id) REFERENCES permission(id)\n" +
                "    );\n";

        String post = "CREATE TABLE IF NOT EXISTS\n" +
                "    `post` (\n" +
                "        `id` INT NOT NULL AUTO_INCREMENT,\n" +
                "        `user_id` INT NOT NULL,\n" +
                "        `description` TEXT(300),\n" +
                "        `end_time` TIMESTAMP NOT NULL,\n" +
                "        PRIMARY KEY(`id`),\n" +
                "        FOREIGN KEY (user_id) REFERENCES user(id)\n" +
                "    );\n";

        String variant = "CREATE TABLE IF NOT EXISTS\n" +
                "    `variant` (\n" +
                "        `id` INT NOT NULL AUTO_INCREMENT,\n" +
                "        `variant_path` TEXT(300) NOT NULL,\n" +
                "        `publication_time` TIMESTAMP,\n" +
                "        `post_id` INT NOT NULL,\n" +
                "        PRIMARY KEY(`id`),\n" +
                "        FOREIGN KEY (post_id) REFERENCES post(id)\n" +
                "    );\n";

        String comment = "CREATE TABLE IF NOT EXISTS\n" +
                "    `comment` (\n" +
                "        `id` INT NOT NULL AUTO_INCREMENT,\n" +
                "        `user_id` INT NOT NULL,\n" +
                "        `text` TEXT(300) NOT NULL,\n" +
                "        `comment_id` INT,\n" +
                "        PRIMARY KEY(`id`),\n" +
                "        FOREIGN KEY (user_id) REFERENCES user(id),\n" +
                "        FOREIGN KEY (comment_id) REFERENCES comment(id)\n" +
                "    );\n";

        String vote = "CREATE TABLE IF NOT EXISTS\n" +
                "    `vote` (\n" +
                "        `id` INT NOT NULL AUTO_INCREMENT,\n" +
                "        `user_id` INT NOT NULL,\n" +
                "        `variant_id` INT NOT NULL,\n" +
                "        PRIMARY KEY(`id`),\n" +
                "        FOREIGN KEY (user_id) REFERENCES user(id),\n" +
                "        FOREIGN KEY (variant_id) REFERENCES variant(id)\n" +
                "    );\n";

        FileUtils.write(file, user + country + questionnaire + preference
                + user_preferences + device + token + permission + token_permissions
                + post + variant + comment + vote
        , Charset.defaultCharset(), true);

    }

    private static void generateUsers() throws IOException {
        List<String> nicknames = new ArrayList<>(FileUtils.readLines(new File(NICKNAMES_PATH), Charset.defaultCharset()));
        List<String> passwords = new ArrayList<>(FileUtils.readLines(new File(PASSWORDS_PATH), Charset.defaultCharset()));
        List<String> emails = new ArrayList<>(FileUtils.readLines(new File(EMAILS_PATH), Charset.defaultCharset()));
        List<String> firstNames = new ArrayList<>(FileUtils.readLines(new File(EMAILS_PATH), Charset.defaultCharset()));
        List<String> lastNames = new ArrayList<>(FileUtils.readLines(new File(EMAILS_PATH), Charset.defaultCharset()));

        long timeFrom = 1420070400; // 01/01/2015 @ 12:00am (UTC)
        long timeTo = 1483228800; // 01/01/2017 @ 12:00am (UTC)



        String query = "INSERT INTO country (country.id, country.name, country.code)\n" +
                "VALUES (1,'a',3),\n" +
                "    (4,'5',6),\n" +
                "    (7,'8',9);";

        FileUtils.write(file, query, Charset.defaultCharset(), true);
    }
}



