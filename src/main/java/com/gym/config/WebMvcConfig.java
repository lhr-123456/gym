package com.gym.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = new ObjectMapper();

        JavaTimeModule javaTimeModule = new JavaTimeModule();

        // LocalDate 支持多种格式解析
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(
            DateTimeFormatter.ofPattern("yyyy-MM-dd[ HH:mm:ss]")
        ));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(
            DateTimeFormatter.ofPattern("yyyy-MM-dd")
        ));

        // LocalDateTime 支持多种格式解析
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(
            DateTimeFormatter.ofPattern("yyyy-MM-dd[ HH:mm:ss]")
        ));
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        ));

        objectMapper.registerModule(javaTimeModule);

        // 序列化配置
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        converter.setObjectMapper(objectMapper);
        return converter;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(0, mappingJackson2HttpMessageConverter());
    }
}
