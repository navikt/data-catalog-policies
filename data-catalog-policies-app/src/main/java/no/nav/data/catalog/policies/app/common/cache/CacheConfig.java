package no.nav.data.catalog.policies.app.common.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    public static final String CODELIST_CACHE = "codelistCache";
    public static final String DATASET_BY_TITLE_CACHE = "datasetByTitleCache";
    public static final String DATASET_BY_ID_CACHE = "datasetByIdCache";

    @Bean
    CacheManager cacheManager() {
        SimpleCacheManager manager = new SimpleCacheManager();
        manager.setCaches(Arrays.asList(
                new CaffeineCache(CODELIST_CACHE, Caffeine.newBuilder()
                        .expireAfterWrite(2, TimeUnit.DAYS)
                        .maximumSize(10000).build()),
                new CaffeineCache(DATASET_BY_TITLE_CACHE, Caffeine.newBuilder()
                        .expireAfterWrite(2, TimeUnit.DAYS)
                        .maximumSize(10000).build()),
                new CaffeineCache(DATASET_BY_ID_CACHE, Caffeine.newBuilder()
                        .expireAfterWrite(2, TimeUnit.DAYS)
                        .maximumSize(10000).build())
        ));
        return manager;
    }
}