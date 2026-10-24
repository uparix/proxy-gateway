package com.uparix.proxy.gateway;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;

@SpringBootTest
class GatewayApplicationTests {

	@Autowired
	private RouteLocator routeLocator;

	@Test
	void loadsRoutesFromApplicationConfig() {
		List<Route> routes = routeLocator.getRoutes().collectList().block();

		assertThat(routes)
				.extracting(Route::getId)
				.containsExactlyInAnyOrder("one", "two");
		assertThat(routes)
				.allSatisfy(route -> assertThat(route.getUri().getHost()).isEqualTo("example.org"));
	}

}
