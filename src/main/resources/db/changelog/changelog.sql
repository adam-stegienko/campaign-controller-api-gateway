-- liquibase formatted sql

-- changeset liquibase:1
CREATE TABLE api_routes (
    id UUID NOT NULL,
    route_id VARCHAR(255) NOT NULL,
    target_uri VARCHAR(1024) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    description VARCHAR(512),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_api_routes PRIMARY KEY (id),
    CONSTRAINT uk_api_routes_route_id UNIQUE (route_id)
);

