package com.tes.server.global.openFeign.config;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import feign.Feign;
import feign.Retryer;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.JsonFormWriter;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@Configuration
public class OpenFeignConfig {

    @Bean
    public Retryer retryer(){
        return new Retryer.Default(1, 5000, 7);
    }

//    @Bean
//    public Encoder multipartFormEncoder() {
//        return new SpringFormEncoder(new SpringEncoder(new ObjectFactory<HttpMessageConverters>() {
//            @Override
//            public HttpMessageConverters getObject() throws BeansException {
//                return new HttpMessageConverters(new RestTemplate().getMessageConverters());
//            }
//        }));
//    }
    @Bean
    public Feign.Builder feignBuilder() {
        ObjectMapper objectMapper = new ObjectMapper()
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        Encoder encoder = new SpringEncoder(() -> new HttpMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper)));
        return Feign.builder().encoder(encoder);
    }
    @Bean
    public Encoder feignFormEncoder(ObjectFactory<HttpMessageConverters> messageConverters) {
        return new SpringFormEncoder(new SpringEncoder(messageConverters));
    }

    @Bean
    public JsonFormWriter jsonFormWriter() {
        return new JsonFormWriter();
    }

}
