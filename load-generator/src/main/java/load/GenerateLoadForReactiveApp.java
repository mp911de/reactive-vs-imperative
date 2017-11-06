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
package load;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author Mark Paluch
 */
@Slf4j
public class GenerateLoadForReactiveApp {

	public static void main(String[] args) {

		WebClient client = WebClient.create();

		Mono<String> mono = client.get().uri("http://localhost:9100").exchange()
				.flatMap(it -> it.bodyToMono(String.class).map(body -> "Received: " + body));

		log.info("Starting requests...");
		List<CompletableFuture<?>> futures = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			futures.add(mono.toFuture());
		}

		log.info("Running...");

		for (CompletableFuture<?> future : futures) {
			future.join();
		}

		log.info("Done!");
	}
}
