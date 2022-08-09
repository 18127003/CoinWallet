package me.app.coinwallet.data.configuration;

public class ConfigurationOption <T> {
    public String label;
    public T code;

    public ConfigurationOption(String label, T code){
        this.label = label;
        this.code = code;
    }
}
