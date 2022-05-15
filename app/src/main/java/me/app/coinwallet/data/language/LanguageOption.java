package me.app.coinwallet.data.language;

public class LanguageOption {
    String label;
    String code;
    int id;
    public LanguageOption(int id, String label, String code){
        this.label = label;
        this.id = id;
        this.code = code;
    }

    public String getLabel() {
        return label;
    }

    public String getCode() {
        return code;
    }
}
