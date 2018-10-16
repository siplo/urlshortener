package sk.siplo.url.shortener.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Date;
import java.util.UUID;

/**
 * Created by siplo on 11/10/2018.
 */
@JsonIgnoreProperties(ignoreUnknown = true) public class ShortUrl {
    private Date createdAt;
    private Long id;
    private String urlHash;
    private boolean isValid;
    private String createdUrl;
    private String originalUrl;

    public ShortUrl() {
    }

    public ShortUrl(Date createdAt, String urlHash, boolean isValid, String originalUrl) {
        this.createdAt = createdAt;
        this.urlHash = urlHash;
        this.isValid = isValid;
        this.originalUrl = originalUrl;
    }

    public ShortUrl(String url) {
        this.originalUrl = url;
        this.urlHash = UUID.randomUUID().toString();
        this.createdAt = new Date();
        this.isValid = true;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getUrlHash() {
        return urlHash;
    }

    public void setUrlHash(String urlHash) {
        this.urlHash = urlHash;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public String getCreatedUrl() {
        return createdUrl;
    }

    public void setCreatedUrl(String createdUrl) {
        this.createdUrl = createdUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ShortUrl shortUrl = (ShortUrl) o;

        if (isValid != shortUrl.isValid) {
            return false;
        }

        if (id != null ? !id.equals(shortUrl.id) : shortUrl.id != null) {
            return false;
        }
        if (urlHash != null ? !urlHash.equals(shortUrl.urlHash) : shortUrl.urlHash != null) {
            return false;
        }
        if (createdUrl != null ? !createdUrl.equals(shortUrl.createdUrl) : shortUrl.createdUrl != null) {
            return false;
        }
        return originalUrl != null ? originalUrl.equals(shortUrl.originalUrl) : shortUrl.originalUrl == null;
    }

    @Override
    public int hashCode() {
        int result = createdAt != null ? createdAt.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (urlHash != null ? urlHash.hashCode() : 0);
        result = 31 * result + (isValid ? 1 : 0);
        result = 31 * result + (createdUrl != null ? createdUrl.hashCode() : 0);
        result = 31 * result + (originalUrl != null ? originalUrl.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ShortUrl{" + "createdAt=" + createdAt + ", id=" + id + ", urlHash='" + urlHash + '\'' + ", isValid="
                + isValid + ", createdUrl='" + createdUrl + '\'' + ", originalUrl='" + originalUrl + '\'' + '}';
    }
}
