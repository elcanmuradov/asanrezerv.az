package com.asanrezerv.subscriptionservice.config;

import com.asanrezerv.subscriptionservice.dto.kafka.BranchCreationEvent;
import com.asanrezerv.subscriptionservice.dto.kafka.RestaurantDto;
import com.asanrezerv.subscriptionservice.dto.kafka.TableCreationEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.producer.bootstrap-servers}")
    private String bootstrapServers;



    @Bean("uuid")
    public ConcurrentKafkaListenerContainerFactory<String, UUID> uuidConcurrentKafkaListenerContainerFactory () {
        ConcurrentKafkaListenerContainerFactory<String, UUID> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(uuidConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, UUID> uuidConsumerFactory() {
        Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "uuid-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);

        props.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JacksonJsonDeserializer.VALUE_DEFAULT_TYPE,"java.util.UUID");
        props.put(JacksonJsonDeserializer.USE_TYPE_INFO_HEADERS, false);

        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JacksonJsonDeserializer.class);
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
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "restaurant-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);

        props.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JacksonJsonDeserializer.VALUE_DEFAULT_TYPE,"com.asanrezerv.subscriptionservice.dto.kafka.RestaurantDto");
        props.put(JacksonJsonDeserializer.USE_TYPE_INFO_HEADERS, false);

        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JacksonJsonDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props);

    }

    @Bean("table")
    public ConcurrentKafkaListenerContainerFactory<String,TableCreationEvent> tableCreationEventConcurrentKafkaListenerContainerFactory(){
        ConcurrentKafkaListenerContainerFactory<String, TableCreationEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(tableCreationEventConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, TableCreationEvent> tableCreationEventConsumerFactory() {
        Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "table-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);

        props.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JacksonJsonDeserializer.VALUE_DEFAULT_TYPE,"com.asanrezerv.subscriptionservice.dto.kafka.TableCreationEvent");
        props.put(JacksonJsonDeserializer.USE_TYPE_INFO_HEADERS, false);

        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JacksonJsonDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props);

    }


    @Bean("branch")
    public ConcurrentKafkaListenerContainerFactory<String,BranchCreationEvent> branchCreationEventConcurrentKafkaListenerContainerFactory(){
        ConcurrentKafkaListenerContainerFactory<String, BranchCreationEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(branchCreationEventConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, BranchCreationEvent> branchCreationEventConsumerFactory() {
        Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "branch-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);

        props.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JacksonJsonDeserializer.VALUE_DEFAULT_TYPE,"com.asanrezerv.subscriptionservice.dto.kafka.BranchCreationEvent");
        props.put(JacksonJsonDeserializer.USE_TYPE_INFO_HEADERS, false);


        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JacksonJsonDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(props);

    }





    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        HashMap<String, Object> props = new HashMap<>();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(props);


    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

}
