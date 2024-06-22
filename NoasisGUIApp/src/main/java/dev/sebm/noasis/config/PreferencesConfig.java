package dev.sebm.noasis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.prefs.Preferences;

@Configuration
public class PreferencesConfig {

    @Bean
    public Preferences preferences() {
        return Preferences.userRoot().node("/dev/sebm/noasis");
    }

}
