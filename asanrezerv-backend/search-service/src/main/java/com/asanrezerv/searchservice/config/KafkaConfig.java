package com.asanrezerv.searchservice.config;

import com.asanrezerv.searchservice.dto.kafka.branch.BranchDto;
import com.asanrezerv.searchservice.dto.kafka.restaurant.RestaurantDto;
import com.asanrezerv.searchservice.dto.kafka.subscription.RestaurantSubscribed;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.producer.bootstrap-servers}")
    private String bootstrapServers;

    // "branch.search.deleted" (raw UUID payload) üçün
    @Bean("uuid")
    public ConcurrentKafkaListenerContainerFactory<String, UUID> uuidConcurrentKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, UUID> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(uuidConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, UUID> uuidConsumerFactory() {
        Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "search-uuid-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);

        props.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JacksonJsonDeserializer.VALUE_DEFAULT_TYPE, "java.util.UUID");
        props.put(JacksonJsonDeserializer.USE_TYPE_INFO_HEADERS, false);

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean("branch")
    public ConcurrentKafkaListenerContainerFactory<String, BranchDto> branchConcurrentKafkaListenerContainerFactory () {
        ConcurrentKafkaListenerContainerFactory<String, BranchDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(branchConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, BranchDto> branchConsumerFactory() {
        Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "search-branch-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);

        props.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JacksonJsonDeserializer.VALUE_DEFAULT_TYPE,"com.asanrezerv.searchservice.dto.kafka.branch.BranchDto");
        props.put(JacksonJsonDeserializer.USE_TYPE_INFO_HEADERS, false);

        return new DefaultKafkaConsumerFactory<>(props);

    }


    @Bean("restaurant")
    public ConcurrentKafkaListenerContainerFactory<String, RestaurantDto> restaurantConcurrentKafkaListenerContainerFactory () {
        ConcurrentKafkaListenerContainerFactory<String, RestaurantDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(restaurantConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, RestaurantDto> restaurantConsumerFactory() {
        Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "search-restaurant-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);

        props.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JacksonJsonDeserializer.VALUE_DEFAULT_TYPE,"com.asanrezerv.searchservice.dto.kafka.restaurant.RestaurantDto");
        props.put(JacksonJsonDeserializer.USE_TYPE_INFO_HEADERS, false);

        return new DefaultKafkaConsumerFactory<>(props);

    }

    @Bean("subscription")
    public ConcurrentKafkaListenerContainerFactory<String, RestaurantSubscribed> subscriptionConcurrentKafkaListenerContainerFactory () {
        ConcurrentKafkaListenerContainerFactory<String, RestaurantSubscribed> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(subscriptionConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, RestaurantSubscribed> subscriptionConsumerFactory() {
        Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "subscription-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);

        props.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JacksonJsonDeserializer.VALUE_DEFAULT_TYPE,"com.asanrezerv.searchservice.dto.kafka.subscription.RestaurantSubscribed");
        props.put(JacksonJsonDeserializer.USE_TYPE_INFO_HEADERS, false);

        return new DefaultKafkaConsumerFactory<>(props);

    }
}
