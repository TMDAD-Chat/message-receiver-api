package es.unizar.tmdad.config;

import es.unizar.tmdad.listener.OldMessageListener;
import es.unizar.tmdad.listener.UserMessageListener;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {

    @Value("${chat.exchanges.output}")
    private String exchangeName;

    @Value("${chat.exchanges.input}")
    private String usersExchangeName;

    @Value("${chat.exchanges.old-messages}")
    private String oldMessagesExchangeName;

    @Value("${spring.application.name}")
    private String appName;

    @Bean
    FanoutExchange exchange() {
        return new FanoutExchange(exchangeName);
    }

    @Bean("users-exchange")
    FanoutExchange usersExchange() {
        return new FanoutExchange(usersExchangeName);
    }

    @Bean("old-messages-exchange")
    FanoutExchange oldMessagesExchange() {
        return new FanoutExchange(oldMessagesExchangeName);
    }

    @Bean("users-queue")
    Queue usersQueue(){
        return new Queue(getQueueName(appName, usersExchangeName), true, false, false);
    }

    @Bean("old-messages-queue")
    Queue oldMessagesQueue(){
        return new Queue(getQueueName(appName, oldMessagesExchangeName), true, false, false);
    }
    
    @Bean
    Binding usersBinding(@Qualifier("users-queue") Queue usersQueue, @Qualifier("users-exchange") FanoutExchange usersExchange){
        return BindingBuilder.bind(usersQueue).to(usersExchange);
    }

    @Bean
    Binding oldMessagesBinding(@Qualifier("old-messages-queue") Queue oldMessagesQueue, @Qualifier("old-messages-exchange") FanoutExchange oldMessagesExchange){
        return BindingBuilder.bind(oldMessagesQueue).to(oldMessagesExchange);
    }

    @Bean
    SimpleMessageListenerContainer usersContainer(ConnectionFactory connectionFactory,
                                             @Qualifier("users-message-listener") MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(getQueueName(appName, usersExchangeName));
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean("users-message-listener")
    MessageListenerAdapter usersListenerAdapter(UserMessageListener receiver) {
        return new MessageListenerAdapter(receiver, "apply");
    }

    @Bean
    SimpleMessageListenerContainer oldMessagesContainer(ConnectionFactory connectionFactory,
                                             @Qualifier("old-messages-listener") MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(getQueueName(appName, oldMessagesExchangeName));
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean("old-messages-listener")
    MessageListenerAdapter oldMessagesListenerAdapter(OldMessageListener receiver) {
        return new MessageListenerAdapter(receiver, "apply");
    }

    private String getQueueName(String appName, String exchangeName) {
        return appName + "." + exchangeName;
    }
}
