/*
 * Copyright 2002-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.team.stars;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GithubStarsApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(GithubStarsApplication.class)
				.web(WebApplicationType.NONE)
				.run(args);
	}

	@Bean
	public ApplicationRunner statisticsRunner(GithubStars stars) {
		return args -> {
			LocalDate start = LocalDate.of(2020, 1, 1);
			LocalDate end = LocalDate.of(2020, 12, 31);
			Map<LocalDate, Long> dates = stars.getStarsByMonth(start, end);
			for (Map.Entry<LocalDate, Long> date : dates.entrySet()) {
				System.out.printf("%s\t%s%n", date.getKey(), date.getValue());
			}
		};
	}
}
