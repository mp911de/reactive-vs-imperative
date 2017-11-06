/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package reactive;

import io.netty.channel.nio.NioEventLoopGroup;
import reactor.core.publisher.Mono;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.embedded.netty.NettyServerCustomizer;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author Mark Paluch
 */
@SpringBootApplication
public class ReactiveApp {

	static final NioEventLoopGroup EVENT_LOOP = new NioEventLoopGroup(16);

	public static void main(String[] args) {

		Thread monitor = new Thread("monitor") {

			@Override
			public void run() {

				ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

				while (!isInterrupted()) {

					System.out.println("Thread count: " + threadMXBean.getThreadCount());

					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						return;
					}
				}
			}
		};

		monitor.setDaemon(true);
		monitor.start();

		SpringApplication.run(ReactiveApp.class, args);
	}

	@Bean
	WebServerFactoryCustomizer<NettyReactiveWebServerFactory> customizer() {

		return netty -> {
			netty.addContextCustomizers((NettyServerCustomizer) builder -> builder.eventLoopGroup(EVENT_LOOP));
		};
	}
}

@RestController
class Endpoint {

	private final WebClient client = WebClient.builder().clientConnector(new ReactorClientHttpConnector(it -> {

		it.eventLoopGroup(ReactiveApp.EVENT_LOOP);
	})).build();

	@GetMapping("/")
	public Mono<String> gatewayMethod() {
		return client.get().uri("http://localhost:9000").exchange()
				.flatMap(it -> it.bodyToMono(String.class).map(body -> "Received: " + body));
	}
}
