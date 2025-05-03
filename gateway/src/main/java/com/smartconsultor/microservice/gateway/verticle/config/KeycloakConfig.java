package com.smartconsultor.microservice.gateway.verticle.config;

public class KeycloakConfig {
    private String realm;
    private String realm_public_key;
    private String ssl_required;
    private String resource;
    private Credentials credentials;
    private String callback_url;
    private String site_url; 

    public String getRealm() { return realm; }
    public String getRealmPublicKey() { return realm_public_key; }
    public String getSslRequired() { return ssl_required; }
    public String getResource() { return resource; }
    public Credentials getCredentials() { return credentials; }
    public String getCallbackUrl() { return callback_url; }
    public String getSiteUrl() { return site_url; } 

    public static class Credentials {
        private String secret;

        public String getSecret() { return secret; }

        public Credentials() {}
    }

    public KeycloakConfig() {}
}
