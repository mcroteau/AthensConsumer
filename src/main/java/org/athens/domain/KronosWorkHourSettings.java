package org.athens.domain;

import org.springframework.beans.factory.annotation.Value;

public class KronosWorkHourSettings {

    @Value("${api.key}")
    private String apiKey;

    @Value("${api.username}")
    private String username;

    @Value("${api.password}")
    private String password;

    @Value("${api.company}")
    private String company;


    public String getApiKey() {
        return apiKey;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getCompany() {
        return company;
    }


}
