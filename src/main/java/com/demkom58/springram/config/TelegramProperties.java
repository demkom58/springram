package com.demkom58.springram.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Component
@ConfigurationProperties("telegram")
public class TelegramProperties {
    @Nullable
    private String externalUrl;
    @Nullable
    private String internalUrl;
    @Nullable
    private String keyStore;
    @Nullable
    private String keyStorePassword;
    @Nullable
    private String pathToCertificate;

    @Nullable
    public String getExternalUrl() {
        return externalUrl;
    }

    public void setExternalUrl(@Nullable String externalUrl) {
        this.externalUrl = externalUrl;
    }

    @Nullable
    public String getInternalUrl() {
        return internalUrl;
    }

    public void setInternalUrl(@Nullable String internalUrl) {
        this.internalUrl = internalUrl;
    }

    @Nullable
    public String getKeyStore() {
        return keyStore;
    }

    public void setKeyStore(@Nullable String keyStore) {
        this.keyStore = keyStore;
    }

    @Nullable
    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public void setKeyStorePassword(@Nullable String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    @Nullable
    public String getPathToCertificate() {
        return pathToCertificate;
    }

    public void setPathToCertificate(@Nullable String pathToCertificate) {
        this.pathToCertificate = pathToCertificate;
    }

    public boolean hasKeyStore() {
        return StringUtils.hasText(keyStore) || StringUtils.hasText(keyStorePassword);
    }

    public boolean hasInternalUrl() {
        return StringUtils.hasText(internalUrl);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TelegramProperties that = (TelegramProperties) o;
        return Objects.equals(externalUrl, that.externalUrl)
                && Objects.equals(internalUrl, that.internalUrl)
                && Objects.equals(keyStore, that.keyStore)
                && Objects.equals(keyStorePassword, that.keyStorePassword)
                && Objects.equals(pathToCertificate, that.pathToCertificate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(externalUrl, internalUrl, keyStore, keyStorePassword, pathToCertificate);
    }

    @Override
    public String toString() {
        return "TelegramProperties{" +
                "externalUrl='" + externalUrl + '\'' +
                ", internalUrl='" + internalUrl + '\'' +
                ", keyStore='" + keyStore + '\'' +
                ", keyStorePassword='" + keyStorePassword + '\'' +
                ", pathToCertificate='" + pathToCertificate + '\'' +
                '}';
    }
}
