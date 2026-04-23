package com.miapp.MiHoja.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.storage")
public class StorageProperties {

    private String provider = "local";
    private final Supabase supabase = new Supabase();

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public Supabase getSupabase() {
        return supabase;
    }

    public static class Supabase {
        private String url;
        private String serviceKey;
        private String bucket = "mihoja";
        private boolean publicBucket = true;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getServiceKey() {
            return serviceKey;
        }

        public void setServiceKey(String serviceKey) {
            this.serviceKey = serviceKey;
        }

        public String getBucket() {
            return bucket;
        }

        public void setBucket(String bucket) {
            this.bucket = bucket;
        }

        public boolean isPublicBucket() {
            return publicBucket;
        }

        public void setPublicBucket(boolean publicBucket) {
            this.publicBucket = publicBucket;
        }
    }
}
