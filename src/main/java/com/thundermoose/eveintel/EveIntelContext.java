package com.thundermoose.eveintel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.thundermoose.eveintel.api.ApiCommon;
import com.thundermoose.eveintel.api.EveApiClient;
import com.thundermoose.eveintel.api.EveStaticData;
import com.thundermoose.eveintel.api.ZKillApiClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.io.IOException;
import java.util.List;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"com.thundermoose.eveintel"})
public class EveIntelContext extends WebMvcConfigurerAdapter {
  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    converters.add(jacksonConverter());
  }

  @Bean
  public ApiCommon apiCommon() {
    return new ApiCommon();
  }

  @Bean
  public EveApiClient eveClient() {
    return new EveApiClient(apiCommon());
  }

  @Bean
  public EveStaticData eveStaticData() throws IOException {
    return new EveStaticData();
  }

  @Bean
  public ZKillApiClient zkillClient() throws IOException {
    return new ZKillApiClient(eveStaticData(), apiCommon());
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
    o.registerModule(new JodaModule());
//    o.enable(SerializationFeature.INDENT_OUTPUT);
    return o;
  }
}
