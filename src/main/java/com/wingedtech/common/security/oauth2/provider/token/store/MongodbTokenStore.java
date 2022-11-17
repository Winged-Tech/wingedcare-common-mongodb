package com.wingedtech.common.security.oauth2.provider.token.store;

import com.wingedtech.common.multitenancy.Tenant;
import com.wingedtech.common.security.SecurityUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.util.SerializationUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of token services that stores tokens in a database.
 *
 * @author 6688 Sun
 */
@Slf4j
public class MongodbTokenStore implements TokenStore {

    private final MongoTemplate mongoTemplate;
    private AuthenticationKeyGenerator authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();

    public MongodbTokenStore(MongoTemplate mongoTemplate) {
        Assert.notNull(mongoTemplate, "MongoTemplate required");
        this.mongoTemplate = mongoTemplate;
    }

    public void setAuthenticationKeyGenerator(AuthenticationKeyGenerator authenticationKeyGenerator) {
        this.authenticationKeyGenerator = authenticationKeyGenerator;
    }

    /**
     * Read the authentication stored under the specified token value.
     *
     * @param token The token value under which the authentication is stored.
     * @return The authentication, or null if none.
     */
    @Override
    public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
        return readAuthentication(token.getValue());
    }

    /**
     * Read the authentication stored under the specified token value.
     *
     * @param token The token value under which the authentication is stored.
     * @return The authentication, or null if none.
     */
    @Override
    public OAuth2Authentication readAuthentication(String token) {
        OAuth2Authentication authentication = null;
        final OauthAccessToken oauthAccessToken = getOauthAccessToken(token);
        if (oauthAccessToken != null) {
            try {
                authentication = deserializeAuthentication(oauthAccessToken.getAuthentication());
            } catch (IllegalArgumentException e) {
                log.warn("Failed to deserialize authentication for " + token, e);
                removeAccessToken(token);
            }
        }
        return authentication;
    }

    /**
     * Store an access token.
     *
     * @param token          The token to store.
     * @param authentication The authentication associated with the token.
     */
    @Override
    public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        String refreshToken = null;
        if (token.getRefreshToken() != null) {
            refreshToken = token.getRefreshToken().getValue();
        }

        if (readAccessToken(token.getValue()) != null) {
            removeAccessToken(token.getValue());
        }

        OauthAccessToken oauthAccessToken = new OauthAccessToken();
        oauthAccessToken.setTokenId(extractTokenKey(token.getValue()));
        oauthAccessToken.setToken(serializeAccessToken(token));
        oauthAccessToken.setAuthenticationId(authenticationKeyGenerator.extractKey(authentication));
        oauthAccessToken.setUsername(authentication.isClientOnly() ? null : authentication.getName());
        oauthAccessToken.setClientId(authentication.getOAuth2Request().getClientId());
        oauthAccessToken.setAuthentication(serializeAuthentication(authentication));
        oauthAccessToken.setRefreshToken(extractTokenKey(refreshToken));
        oauthAccessToken.setTokenValue(token.getValue());
        oauthAccessToken.setExpiresAt(Instant.now().plusSeconds(token.getExpiresIn()));

        mongoTemplate.save(oauthAccessToken);
    }

    /**
     * Read an access token from the store.
     *
     * @param tokenValue The token value.
     * @return The access token to read.
     */
    @Override
    public OAuth2AccessToken readAccessToken(String tokenValue) {
        OAuth2AccessToken accessToken = null;
        final OauthAccessToken oauthAccessToken = getOauthAccessToken(tokenValue);
        if (oauthAccessToken != null) {
            try {
                accessToken = deserializeAccessToken(oauthAccessToken.getToken());
            } catch (IllegalArgumentException e) {
                log.warn("Failed to deserialize access token for " + tokenValue, e);
                removeAccessToken(tokenValue);
            }
        }
        return accessToken;
    }

    /**
     * Remove an access token from the store.
     *
     * @param token The token to remove from the store.
     */
    @Override
    public void removeAccessToken(OAuth2AccessToken token) {
        removeAccessToken(token.getValue());
    }

    /**
     * Store the specified refresh token in the store.
     *
     * @param refreshToken   The refresh token to store.
     * @param authentication The authentication associated with the refresh token.
     */
    @Override
    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        OauthRefreshToken oauthRefreshToken = new OauthRefreshToken();
        oauthRefreshToken.setTokenId(extractTokenKey(refreshToken.getValue()));
        oauthRefreshToken.setToken(serializeRefreshToken(refreshToken));
        oauthRefreshToken.setAuthentication(serializeAuthentication(authentication));
        oauthRefreshToken.setTokenValue(refreshToken.getValue());
        oauthRefreshToken.setUserLogin(SecurityUtils.getUserLoginFromAuthentication(authentication));

        mongoTemplate.save(oauthRefreshToken);
    }

    /**
     * Read a refresh token from the store.
     *
     * @param tokenValue The value of the token to read.
     * @return The token.
     */
    @Override
    public OAuth2RefreshToken readRefreshToken(String tokenValue) {
        OAuth2RefreshToken refreshToken = null;
        final OauthRefreshToken oauthRefreshToken = getOauthRefreshToken(tokenValue);
        if (oauthRefreshToken != null) {
            try {
                refreshToken = deserializeRefreshToken(oauthRefreshToken.getToken());
            } catch (IllegalArgumentException e) {
                log.warn("Failed to deserialize refresh token for token " + tokenValue, e);
                removeRefreshToken(tokenValue);
            }
        }
        return refreshToken;
    }

    public OAuth2AccessToken getAccessTokenByRefreshToken(String tokenValue) {
        OAuth2AccessToken accessToken = null;
        final OauthAccessToken oauthAccessToken = getOauthAccessTokenByRefreshToken(tokenValue);
        if (oauthAccessToken != null) {
            try {
                accessToken = deserializeAccessToken(oauthAccessToken.getToken());
            } catch (IllegalArgumentException e) {
                log.warn("Failed to deserialize access token for " + tokenValue, e);
                removeAccessToken(tokenValue);
            }
        }
        return accessToken;
    }

    /**
     * @param token a refresh token
     * @return the authentication originally used to grant the refresh token
     */
    @Override
    public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
        return readAuthenticationForRefreshToken(token.getValue());
    }

    /**
     * Remove a refresh token from the store.
     *
     * @param token The token to remove from the store.
     */
    @Override
    public void removeRefreshToken(OAuth2RefreshToken token) {
        removeRefreshToken(token.getValue());
    }

    /**
     * Remove an access token using a refresh token. This functionality is necessary so refresh tokens can't be used to
     * create an unlimited number of access tokens.
     *
     * @param refreshToken The refresh token.
     */
    @Override
    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
        removeAccessTokenUsingRefreshToken(refreshToken.getValue());
    }

    /**
     * Retrieve an access token stored against the provided authentication key, if it exists.
     *
     * @param authentication the authentication key for the access token
     * @return the access token or null if there was none
     */
    @Override
    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        OAuth2AccessToken accessToken = null;
        final OauthAccessToken oauthAccessToken = getOauthAccessToken(authentication);
        if (oauthAccessToken != null) {
            try {
                accessToken = deserializeAccessToken(oauthAccessToken.getToken());
            } catch (IllegalArgumentException e) {
                log.error("Could not extract access token for authentication " + authentication, e);
            }
        }
        return accessToken;
    }

    /**
     * @param clientId the client id to search
     * @param userName the user name to search
     * @return a collection of access tokens
     */
    @Override
    public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName) {
        Criteria criteria = Criteria.where("client_id").is(clientId)
            .and("user_name").is(userName);

        final List<OauthAccessToken> accessTokens = mongoTemplate.find(Query.query(criteria), OauthAccessToken.class);
        if (CollectionUtils.isEmpty(accessTokens)) {
            if (log.isInfoEnabled()) {
                log.info("Failed to find access token for clientId " + clientId + " and userName " + userName);
            }
        } else {
            return accessTokens.stream()
                .filter(accessToken -> accessToken.getToken() == null)
                .map(accessToken -> deserializeAccessToken(accessToken.getToken())).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    /**
     * @param clientId the client id to search
     * @return a collection of access tokens
     */
    @Override
    public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
        final List<OauthAccessToken> accessTokens = mongoTemplate.find(Query.query(Criteria.where("client_id").is(clientId)), OauthAccessToken.class);
        if (CollectionUtils.isEmpty(accessTokens)) {
            if (log.isInfoEnabled()) {
                log.info("Failed to find access token for clientId " + clientId);
            }
        } else {
            return accessTokens.stream()
                .filter(accessToken -> accessToken.getToken() == null)
                .map(accessToken -> deserializeAccessToken(accessToken.getToken())).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public void removeAccessToken(String tokenValue) {
        removeAccessTokenByTokenId(tokenValue);
    }

    public void removeAccessTokenUsingRefreshToken(String refreshTokenValue) {
        removeAccessTokenByRefreshToken(refreshTokenValue);
    }

    public void removeAccessTokenForUser(String userName) {
        Criteria criteria = Criteria.where("user_name").is(userName);
        mongoTemplate.remove(Query.query(criteria), OauthAccessToken.class);
        if (log.isInfoEnabled()) {
            log.info("Remove access token for user [{}]", userName);
        }
    }

    public void removeRefreshToken(String tokenValue) {
        removeRefreshTokenByTokenId(tokenValue);
    }

    public OAuth2Authentication readAuthenticationForRefreshToken(String value) {
        final OauthRefreshToken oauthRefreshToken = getOauthRefreshToken(value);
        OAuth2Authentication authentication = null;
        if (oauthRefreshToken != null) {
            try {
                authentication = deserializeAuthentication(oauthRefreshToken.getAuthentication());
            } catch (IllegalArgumentException e) {
                log.warn("Failed to deserialize authentication for " + value, e);
                removeRefreshToken(value);
            }
        }
        return authentication;
    }

    protected byte[] serializeAccessToken(OAuth2AccessToken token) {
        return SerializationUtils.serialize(token);
    }

    protected byte[] serializeRefreshToken(OAuth2RefreshToken token) {
        return SerializationUtils.serialize(token);
    }

    protected byte[] serializeAuthentication(OAuth2Authentication authentication) {
        return SerializationUtils.serialize(authentication);
    }

    protected OAuth2AccessToken deserializeAccessToken(byte[] token) {
        return SerializationUtils.deserialize(token);
    }

    protected OAuth2RefreshToken deserializeRefreshToken(byte[] token) {
        return SerializationUtils.deserialize(token);
    }

    protected OAuth2Authentication deserializeAuthentication(byte[] authentication) {
        return SerializationUtils.deserialize(authentication);
    }

    protected String extractTokenKey(String value) {
        if (value == null) {
            return null;
        }
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("MD5 algorithm not available.  Fatal (should be in the JDK).");
        }

        try {
            byte[] bytes = digest.digest(value.getBytes("UTF-8"));
            return String.format("%032x", new BigInteger(1, bytes));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 encoding not available.  Fatal (should be in the JDK).");
        }
    }

    private OauthAccessToken getOauthAccessToken(OAuth2Authentication authentication) {
        final OauthAccessToken oauthAccessToken = mongoTemplate.findOne(buildQueryByAuthenticationId(authentication), OauthAccessToken.class);
        if (oauthAccessToken == null && log.isInfoEnabled()) {
            log.info("Failed to find access token for authentication " + authentication);
        }
        return oauthAccessToken;
    }

    private OauthAccessToken getOauthAccessToken(String tokenValue) {
        return getAccessTokenByTokenId(tokenValue);
    }

    private OauthAccessToken getOauthAccessTokenByRefreshToken(String refreshTokenValue) {
        return getAccessTokenByRefreshToken2(refreshTokenValue);
    }

    private OauthRefreshToken getOauthRefreshToken(String tokenValue) {
        return getRefreshTokenByTokenId(tokenValue);
    }

    private OauthAccessToken getAccessTokenByTokenId(String tokenValue) {
        String tokenKey = extractTokenKey(tokenValue);
        log.debug("Get access token by token id [{}] for tenant [{}] and raw token [{}]", tokenKey, Tenant.getCurrentTenantId(), tokenValue);
        return mongoTemplate.findOne(buildQueryByTokenId(tokenKey), OauthAccessToken.class);
    }

    private OauthAccessToken getAccessTokenByRefreshToken2(String refreshTokenValue) {
        final String refreshTokenKey = extractTokenKey(refreshTokenValue);
        log.debug("Get access token by refresh token [{}] for tenant [{}] and raw refresh token [{}]", refreshTokenKey, Tenant.getCurrentTenantId(), refreshTokenValue);
        return mongoTemplate.findOne(buildQueryByRefreshToken(refreshTokenKey), OauthAccessToken.class);
    }

    private OauthRefreshToken getRefreshTokenByTokenId(String tokenValue) {
        String tokenKey = extractTokenKey(tokenValue);
        log.debug("Get refresh token by token id [{}] for tenant [{}] and raw token [{}]", tokenKey, Tenant.getCurrentTenantId(), tokenValue);
        return mongoTemplate.findOne(buildQueryByTokenId(tokenKey), OauthRefreshToken.class);
    }

    private void removeAccessTokenByTokenId(String tokenValue) {
        String tokenKey = extractTokenKey(tokenValue);
        mongoTemplate.remove(buildQueryByTokenId(tokenKey), OauthAccessToken.class);
        log.debug("remove access token by token id [{}] for tenant [{}] and raw token [{}]", tokenKey, Tenant.getCurrentTenantId(), tokenValue);
    }

    private void removeAccessTokenByRefreshToken(String refreshTokenValue) {
        String refreshTokenKey = extractTokenKey(refreshTokenValue);
        mongoTemplate.remove(buildQueryByRefreshToken(refreshTokenKey), OauthAccessToken.class);
        log.debug("remove access token by refresh token [{}] for tenant [{}] and raw refresh token [{}]", refreshTokenKey, Tenant.getCurrentTenantId(), refreshTokenValue);
    }

    private void removeRefreshTokenByTokenId(String tokenValue) {
        String tokenKey = extractTokenKey(tokenValue);
        mongoTemplate.remove(buildQueryByTokenId(tokenKey), OauthRefreshToken.class);
        log.debug("remove refresh token by token id [{}] for tenant [{}] and raw token [{}]", tokenKey, Tenant.getCurrentTenantId(), tokenValue);
    }


    private Query buildQueryByTokenId(String tokenValue) {
        return Query.query(Criteria.where("token_id").is(tokenValue));
    }

    private Query buildQueryByRefreshToken(String tokenValue) {
        return Query.query(Criteria.where("refresh_token").is(tokenValue));
    }

    private Query buildQueryByAuthenticationId(OAuth2Authentication authentication) {
        return Query.query(Criteria.where("authentication_id").is(authenticationKeyGenerator.extractKey(authentication)));
    }

    @Data
    @Document(value = "oauth_access_token")
    static class OauthAccessToken implements Serializable {

        private static final long serialVersionUID = 8729317704377939898L;

        @Id
        private String id;

        @Field("token_id")
        private String tokenId;

        @Field("token")
        private byte[] token;

        @Field("authentication_id")
        private String authenticationId;

        @Field("user_name")
        private String username;

        @Field("client_id")
        private String clientId;

        @Field("authentication")
        private byte[] authentication;

        @Field("refresh_token")
        private String refreshToken;

        @Field("token_value")
        private String tokenValue;

        @Field("expires_at")
        private Instant expiresAt;
    }

    @Data
    @Document("oauth_refresh_token")
    static class OauthRefreshToken implements Serializable {

        private static final long serialVersionUID = -4195059312680827502L;

        @Id
        private String id;

        @Field("token_id")
        private String tokenId;

        @Field("token")
        private byte[] token;

        @Field("authentication")
        private byte[] authentication;

        @Field("token_value")
        private String tokenValue;

        private String userLogin;
    }
}
