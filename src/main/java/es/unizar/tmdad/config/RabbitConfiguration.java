package es.unizar.tmdad.config;

import es.unizar.tmdad.listener.MessageListener;
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

    @Value("${chat.queues.output}")
    private String queueName;

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

    @Bean
    Queue usersQueue(){
        return new Queue(getQueueName(appName, usersExchangeName), true, false, false);
    }
    
    @Bean
    Binding binding(Queue usersQueue, @Qualifier("users-exchange") FanoutExchange usersExchange){
        return BindingBuilder.bind(usersQueue).to(usersExchange);
    }

    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                             MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(
                getQueueName(appName, usersExchangeName));
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(MessageListener receiver) {
        return new MessageListenerAdapter(receiver, "apply");
    }

    private String getQueueName(String appName, String exchangeName) {
        return appName + "." + exchangeName;
    }
}
