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
package servlet;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author Mark Paluch
 */
@SpringBootApplication
public class ServletApp {

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

		SpringApplication.run(ServletApp.class, args);
	}
}

@RestController
class Endpoint {

	private final RestTemplate template = new RestTemplate();

	@GetMapping("/")
	public String gatewayMethod() {

		String response = template.getForObject("http://localhost:9000", String.class);

		return "Received: " + response;
	}
}
