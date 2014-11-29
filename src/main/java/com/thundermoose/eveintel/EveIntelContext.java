package com.thundermoose.eveintel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.thundermoose.eveintel.api.EveApiClient;
import com.thundermoose.eveintel.api.EveStaticData;
import com.thundermoose.eveintel.api.ZKillApiClient;
import com.thundermoose.eveintel.dao.CacheNames;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.io.IOException;
import java.util.List;

/**
 * Created by thundermoose on 11/24/14.
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"com.thundermoose.eveintel"})
public class EveIntelContext extends WebMvcConfigurerAdapter {
  public static final Integer CACHE_SIZE = 10000;
  public static final Long CACHE_TTL = 3600L;
  public static final Long CACHE_IDLE_TTL = 0L;

  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    converters.add(jacksonConverter());
  }

  @Bean
  public EveApiClient eveClient() {
    return new EveApiClient();
  }

  @Bean
  public EveStaticData eveStaticData() throws IOException {
    return new EveStaticData();
  }

  @Bean
  public ZKillApiClient zkillClient() throws IOException {
    return new ZKillApiClient(eveStaticData());
  }

  @Bean
  public CacheManager cacheManager() {
    CacheManager cacheManager = CacheManager.create();
    cacheManager.addCache(new Cache(CacheNames.PILOT_CACHE, CACHE_SIZE, false, false, CACHE_TTL, CACHE_IDLE_TTL));
    cacheManager.addCache(new Cache(CacheNames.RECENT_ACTIVITY_CACHE, CACHE_SIZE, false, false, CACHE_TTL, CACHE_IDLE_TTL));
    return cacheManager;
  }

  @Bean
  public MappingJackson2HttpMessageConverter jacksonConverter() {
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
    converter.setObjectMapper(objectMapper());
    return converter;
  }

  @Bean
  public ObjectMapper objectMapper() {
    ObjectMapper o = new ObjectMapper();
    o.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    o.enable(SerializationFeature.INDENT_OUTPUT);
    return o;
  }
}
