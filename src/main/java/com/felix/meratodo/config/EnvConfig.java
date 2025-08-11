package com.felix.meratodo.config;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvConfig {

    private static final Dotenv dotenv=Dotenv.load();

    private EnvConfig(){};

    public static String get(String key){
        return dotenv.get(key);
    }
}
