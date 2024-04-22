package org.example.bootstrapnode.Configration;


import org.example.bootstrapnode.Models.NodeUserCount;
import org.example.bootstrapnode.Models.Nodes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public NodeUserCount nodeUserCount() {
        return new NodeUserCount();
    }

    @Bean
    public Nodes nodes(){
        return new Nodes();
    }
}

