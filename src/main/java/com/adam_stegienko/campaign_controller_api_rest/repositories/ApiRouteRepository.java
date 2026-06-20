package com.adam_stegienko.campaign_controller_api_gateway.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.adam_stegienko.campaign_controller_api_gateway.model.ApiRoute;

@Repository
public interface ApiRouteRepository extends JpaRepository<ApiRoute, UUID> {

    Optional<ApiRoute> findByRouteIdAndEnabledTrue(String routeId);
}
