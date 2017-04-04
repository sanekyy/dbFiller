import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by ihb on 01.04.17.
 */

public class App {

    private static File file;

    private static String SCRIPT_PATH = "src/main/resources/script";

    private static String USER_NICKNAMES_PATH = "src/main/resources/user/nicknames";
    private static String USER_EMAILS_PATH = "src/main/resources/user/emails";
    private static String USER_PASSWORDS_PATH = "src/main/resources/user/passwords";
    private static String USER_FIRST_NAMES_PATH = "src/main/resources/user/firstNames";
    private static String USER_LAST_NAMES_PATH = "src/main/resources/user/lastNames";

    private static String COUNTRY_NAMES_AND_CODES_PATH = "src/main/resources/country/namesAndCodes";

    private static String PREFERENCE_NAMES_PATH = "src/main/resources/preference/names";

    private static String POST_DESCRIPTIONS_PATH = "src/main/resources/post/descriptions";

    private static String COMMENT_TEXT_PATH = POST_DESCRIPTIONS_PATH;



    private static int USER_COUNT = 100;
    private static int COUNTRY_COUNT = 10;
    private static int QUESTIONNAIRE_COUNT = USER_COUNT;
    private static int PREFERENCE_COUNT = 5;
    private static int POST_COUNT = 0; // calculate in runtime
    private static int VARIANT_COUNT = 0; // calculate in runtime
    private static int COMMENT_COUNT = 0; // calculate in runtime

    private static int USER_PREFERENCES_COUNT_DELTA = 5;
    private static int USER_PREFERENCES_COUNT_FROM = 1;

    private static int POST_COUNT_DELTA = 51;
    private static int POST_COUNT_FROM = 25;

    private static int VARIANT_COUNT_DELTA = 5;
    private static int VARIANT_COUNT_FROM = 1;

    private static int VOTE_COUNT_DELTA = 11;
    private static int VOTE_COUNT_FROM = 10;

    private static int COMMENT_COUNT_DELTA = 10;
    private static int COMMENT_COUNT_FROM = 1;


    private static Random rand = new Random(System.currentTimeMillis());

    private static long timeFrom = 1262304000; // 01/01/2010 @ 12:00am (UTC)
    private static long timeTo = 1483228800; // 01/01/2017 @ 12:00am (UTC)

    public static void main(String[] args) throws IOException {

        initFile();

        FileUtils.write(new File(POST_DESCRIPTIONS_PATH+1),
                FileUtils.readFileToString(new File(POST_DESCRIPTIONS_PATH), Charset.defaultCharset()).replaceAll("[\\[\\]\"{}()']",""),
                Charset.defaultCharset(), false);


        clearDB();
        createTables();

        generateUsers();
        generateCountries();
        generateQuestionnaire();
        generatePreferences();
        generateUserPreferences();
        generatePosts();
        generateVariants();
        generateVotes();
        generateComment();



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
                "        `path` TEXT(300) NOT NULL,\n" +
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
        List<String> nicknames = new ArrayList<>(FileUtils.readLines(new File(USER_NICKNAMES_PATH), Charset.defaultCharset()));
        List<String> passwords = new ArrayList<>(FileUtils.readLines(new File(USER_PASSWORDS_PATH), Charset.defaultCharset()));
        List<String> emails = new ArrayList<>(FileUtils.readLines(new File(USER_EMAILS_PATH), Charset.defaultCharset()));
        List<String> firstNames = new ArrayList<>(FileUtils.readLines(new File(USER_FIRST_NAMES_PATH), Charset.defaultCharset()));
        List<String> lastNames = new ArrayList<>(FileUtils.readLines(new File(USER_LAST_NAMES_PATH), Charset.defaultCharset()));

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        StringBuilder query = new StringBuilder("INSERT INTO user (id, nickname, password, email, first_name, last_name, date_of_registration, date_of_last_visit)\n"
                + "VALUES ");


        for(int i=0; i<USER_COUNT; i++){


            long regTime = Math.abs(rand.nextLong())%(timeTo-timeFrom)+timeFrom;
            long lastTime = (regTime+Math.abs(rand.nextLong())%(timeTo-regTime));

            String regTimeStr = dateFormat.format(new Date(regTime*1000));
            String lastTimeStr = dateFormat.format(new Date(lastTime*1000));

            query.append("('").append(i+1).append("','").append(nicknames.get(i * 45)).append("','")
                    .append(passwords.get(i * 10 % passwords.size())).append("','")
                    .append(emails.get(i * 10 % emails.size())).append("','")
                    .append(firstNames.get(i * 10 % firstNames.size())).append("','")
                    .append(lastNames.get(i * 10 % lastNames.size())).append("','")
                    .append(regTimeStr).append("','").append(lastTimeStr).append("')");

            if(i!=USER_COUNT-1){
                query.append(",\n");
            }
        }

        query.append(";");

        FileUtils.write(file, query.toString(), Charset.defaultCharset(), true);
    }

    private static void generateCountries() throws IOException {
        List<String> namesAndCodes = new ArrayList<>(FileUtils.readLines(new File(COUNTRY_NAMES_AND_CODES_PATH), Charset.defaultCharset()));

        StringBuilder query = new StringBuilder("INSERT INTO country (id, name, code)\n"
                + "VALUES ");

        for(int i=0; i<COUNTRY_COUNT; i++){

            query.append("('").append(i+1).append("','").append(namesAndCodes.get(i).split("\\|")[0]).append("','")
                    .append(namesAndCodes.get(i).split("\\|")[1]).append("')");

            if(i!=COUNTRY_COUNT-1){
                query.append(",\n");
            }
        }

        query.append(";");

        FileUtils.write(file, query.toString(), Charset.defaultCharset(), true);
    }

    private static void generateQuestionnaire() throws IOException {

        int age, sex, country;

        StringBuilder query = new StringBuilder("INSERT INTO questionnaire (id, user_id, age, sex, country_id)\n"
                + "VALUES ");

        for(int i=0; i<QUESTIONNAIRE_COUNT; i++){

            age = Math.abs(rand.nextInt())%20+16;
            sex = Math.abs(rand.nextInt())%2;
            country = Math.abs(rand.nextInt())%COUNTRY_COUNT+1;


            query.append("('").append(i+1).append("','").append(i+1).append("','")
                    .append(age).append("','").append(sex).append("','")
                    .append(country).append("')");

            if(i!=QUESTIONNAIRE_COUNT-1){
                query.append(",\n");
            }
        }

        query.append(";");

        FileUtils.write(file, query.toString(), Charset.defaultCharset(), true);
    }

    private static void generatePreferences() throws IOException {
        List<String> names = new ArrayList<>(FileUtils.readLines(new File(PREFERENCE_NAMES_PATH), Charset.defaultCharset()));

        StringBuilder query = new StringBuilder("INSERT INTO preference (id, name)\n"
                + "VALUES ");

        for(int i=0; i<PREFERENCE_COUNT; i++){

            query.append("('").append(i+1).append("','").append(names.get(i)).append("')");

            if(i!=PREFERENCE_COUNT-1){
                query.append(",\n");
            }
        }

        query.append(";");

        FileUtils.write(file, query.toString(), Charset.defaultCharset(), true);
    }

    private static void generateUserPreferences() throws IOException {

        int userPrefCount;
        int id=1;

        String res = "";

        StringBuilder query = new StringBuilder("INSERT INTO user_preferences (id, preference_id, user_id)\n"
                + "VALUES ");

        for(int i=0; i<USER_COUNT; i++){
            userPrefCount = Math.abs(rand.nextInt())%USER_PREFERENCES_COUNT_DELTA+USER_PREFERENCES_COUNT_FROM;
            Set<Integer> preferences = new HashSet<>();

            while(preferences.size()<userPrefCount){
                preferences.add(Math.abs(rand.nextInt())%PREFERENCE_COUNT+1);
            }


            for(Integer pref : preferences){

                query.append("('").append(id).append("','").append(pref).append("','")
                        .append(i+1).append("'),\n");

                id++;
            }
            if(i==USER_COUNT-1){
                res = query.toString().substring(0, query.toString().length()-2);
                res+=";";
            }
        }

        query.append(";");

        FileUtils.write(file, res, Charset.defaultCharset(), true);
    }

    private static void generatePosts() throws IOException {

        int userPostCount;
        int id=1;
        List<String> descriptions = FileUtils.readLines(new File(POST_DESCRIPTIONS_PATH), Charset.defaultCharset());

        String res = "";

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        StringBuilder query = new StringBuilder("INSERT INTO post (id, user_id, description, end_time)\n"
                + "VALUES ");

        for(int i=0; i<USER_COUNT; i++){
            userPostCount = Math.abs(rand.nextInt())%POST_COUNT_DELTA+POST_COUNT_FROM;
            Set<String> userPostDescriptions = new HashSet<>();

            POST_COUNT+=userPostCount;

            while(userPostDescriptions.size()<userPostCount){
                userPostDescriptions.add(descriptions.get(Math.abs(rand.nextInt())%descriptions.size()));
            }


            for(String description : userPostDescriptions){
                long endTime = Math.abs(rand.nextLong())%(timeTo-timeFrom)+timeFrom;
                String endTimeStr = dateFormat.format(new Date(endTime*1000));

                query.append("('").append(id).append("','").append(i+1).append("','")
                .append(description.trim()).append("','").append(endTimeStr).append("'),\n");

                id++;
            }
            if(i==USER_COUNT-1){
                res = query.toString().substring(0, query.toString().length()-2);
                res+=";";
            }
        }

        query.append(";");

        FileUtils.write(file, res, Charset.defaultCharset(), true);
    }

    private static void generateVariants() throws IOException {

        int postVariantCount;
        int id=1;
        //List<String> paths = FileUtils.readLines(new File(VARIANT_PATHS_PATH), Charset.defaultCharset());

        String res = "";

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        StringBuilder query = new StringBuilder("INSERT INTO variant (id, path, publication_time, post_id)\n"
                + "VALUES ");

        for(int i=0; i<POST_COUNT; i++){
            postVariantCount = Math.abs(rand.nextInt())%VARIANT_COUNT_DELTA+VARIANT_COUNT_FROM;
            Set<String> postVariantPaths = new HashSet<>();

            VARIANT_COUNT+=postVariantCount;

            while(postVariantPaths.size()<postVariantCount){
                postVariantPaths.add("C://Variant/"+ new BigInteger(130, rand).toString(32));//paths.get(Math.abs(rand.nextInt())%paths.size()));
            }


            for(String path : postVariantPaths){
                long publicationTime = Math.abs(rand.nextLong())%(timeTo-timeFrom)+timeFrom;
                String publicationDateStr = dateFormat.format(new Date(publicationTime*1000));

                query.append("('").append(id).append("','").append(path.trim()).append("','")
                        .append(publicationDateStr).append("','").append(i+1).append("'),\n");

                id++;
            }
            if(i==POST_COUNT-1){
                res = query.toString().substring(0, query.toString().length()-2);
                res+=";";
            }
        }

        query.append(";");

        FileUtils.write(file, res, Charset.defaultCharset(), true);
    }

    private static void generateVotes() throws IOException {

        int variantVoteCount;
        int id=1;
        
        String res = "";
        
        StringBuilder query = new StringBuilder("INSERT INTO vote (id, user_id, variant_id)\n"
                + "VALUES ");

        for(int i=0; i<VARIANT_COUNT; i++){
            variantVoteCount = Math.abs(rand.nextInt())%VOTE_COUNT_DELTA+VOTE_COUNT_FROM;
            Set<Integer> variantVoteUserId = new HashSet<>();

            while(variantVoteUserId.size()<variantVoteCount){
                variantVoteUserId.add(Math.abs(rand.nextInt())%USER_COUNT+1);
            }


            for(Integer userId : variantVoteUserId){

                query.append("('").append(id).append("','").append(userId).append("','")
                        .append(i+1).append("'),\n");

                id++;
            }
            if(i==VARIANT_COUNT-1){
                res = query.toString().substring(0, query.toString().length()-2);
                res+=";";
            } else {
                res = query.toString();
            }

            FileUtils.write(file, res, Charset.defaultCharset(), true);

            res="";
            query = new StringBuilder();
        }
    }

    private static void generateComment() throws IOException {

        int variantCommentCount, userId, commentId;
        List<String> descriptions = FileUtils.readLines(new File(POST_DESCRIPTIONS_PATH), Charset.defaultCharset());
        int id=1;

        String res = "";

        StringBuilder query = new StringBuilder("INSERT INTO comment (id, user_id, text, comment_id)\n"
                + "VALUES ");

        for(int i=0; i<VARIANT_COUNT; i++){
            variantCommentCount = Math.abs(rand.nextInt())%COMMENT_COUNT_DELTA+COMMENT_COUNT_FROM;
            Set<String> commentTexts = new HashSet<>();

            while(commentTexts.size()<variantCommentCount){
                commentTexts.add(descriptions.get(Math.abs(rand.nextInt())%descriptions.size()));
            }


            for(String commentText : commentTexts){
                userId = Math.abs(rand.nextInt())%USER_COUNT+1;
                commentId = Math.abs(rand.nextInt())%(COMMENT_COUNT+1);

                query.append("('").append(id).append("','").append(userId).append("','")
                        .append(commentText).append("','");

                if(commentId==0){

                }


                id++;
            }

            if(i==VARIANT_COUNT-1){
                res = query.toString().substring(0, query.toString().length()-2);
                res+=";";
            } else {
                res = query.toString();
            }

            FileUtils.write(file, res, Charset.defaultCharset(), true);

            res = "";
            query = new StringBuilder();

            COMMENT_COUNT += variantCommentCount;
        }
    }
}



