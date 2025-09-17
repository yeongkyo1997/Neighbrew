package com.ssafy.backend.authentication.domain.factory;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ssafy.backend.authentication.domain.oauth.OAuthApiClient;
import com.ssafy.backend.authentication.domain.oauth.OAuthProvider;

@Component
public class OAuthClientFactory {
    private final Map<OAuthProvider, OAuthApiClient> clients;

    public OAuthClientFactory(List<OAuthApiClient> oAuthApiClients) {
        this.clients = oAuthApiClients.stream().collect(
                Collectors.toMap(OAuthApiClient::oAuthProvider, Function.identity()));
    }

    public OAuthApiClient getClient(OAuthProvider provider) {
        OAuthApiClient client = clients.get(provider);
        if (client == null) {
            throw new IllegalArgumentException("Unsupported OAuth provider: " + provider);
        }
        return client;
    }

    public boolean isSupported(OAuthProvider provider) {
        return clients.containsKey(provider);
    }
}