package com.globallogic.bcttt;

import scala.sys.Prop;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class Main {
    public static void main(String... args) throws IOException {
        Properties props = new Properties();
        if (args.length > 0) {
            readFromFile(props, new File(args[0]));
            System.out.println("using consumer settings from file " + args[0]);
        } else {
            File file = new File("consumer.properties");
            if (file.exists()) {
                readFromFile(props, file);
                System.out.println("using consumer settings from file " + file);
            } else {
                try (InputStream inputStream = Main.class.getResourceAsStream("/consumer.properties")) {
                    props.load(inputStream);
                    System.out.println("using internal consumer settings");
                }
                try (OutputStream outputStream = new FileOutputStream(file)) {
                    props.store(outputStream, "automatically generated from internally stored configuration");
                    System.out.println("default settings generated to " + file);
                }

                createFileIfNotExists("create-app-topic.sh").setExecutable(true);
                createFileIfNotExists("delete-app-topic.sh").setExecutable(true);
            }
        }

        new BtcTxMessageConsumer(props, props.getProperty("app.topic"), 10).run();
    }

    private static void readFromFile(Properties props, File file) throws IOException {
        try (FileReader fileReader = new FileReader(file)) {
            props.load(fileReader);
        }
    }

    private static File createFileIfNotExists(String resourceName) throws IOException {
        File file = new File(resourceName);
        if (!file.exists()) {
            try (InputStream inputStream = Main.class.getResourceAsStream("/" + resourceName)) {
                Files.copy(inputStream, file.toPath());
            }
        }
        return file;
    }
}
