package com.adam_stegienko.campaign_controller_api_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.adam_stegienko.campaign_controller_api_gateway"})
@EnableScheduling
public class CampaignControllerApiGateway {

  public static void main(String[] args) {
    SpringApplication.run(CampaignControllerApiGateway.class, args);
  }

}
