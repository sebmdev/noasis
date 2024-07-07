package dev.sebm.noasis.controller;

import dev.sebm.noasis.util.SpringFXMLLoader;
import javafx.fxml.Initializable;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

@Component
public class StudySetAdd implements Initializable {
    private final CookieStore httpCookieStore = new BasicCookieStore();
    private final SpringFXMLLoader springFXMLLoader;
    private final Preferences preferences;


    public StudySetAdd(Preferences preferences, SpringFXMLLoader springFXMLLoader) {
        this.springFXMLLoader = springFXMLLoader;
        this.preferences = preferences.node("session");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String sessionCookieValue = preferences.get("connect.sid", "none");
        System.out.println("LOADED SESSION COOKIE: " + sessionCookieValue);
        if (sessionCookieValue != null) {
            BasicClientCookie sessionCookie = new BasicClientCookie("connect.sid", sessionCookieValue);
            sessionCookie.setPath("/");
            sessionCookie.setDomain("localhost");
            httpCookieStore.addCookie(sessionCookie);
        }
    }
}
