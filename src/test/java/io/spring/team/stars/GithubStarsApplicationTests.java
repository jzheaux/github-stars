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

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest("project.name=spring-ldap")
class GithubStarsApplicationTests {
	@Autowired
	GithubStars stars;

	@Test
	void getStarsByMonth() {
		Map<LocalDate, Long> dates = this.stars.getStarsByMonth(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 12, 31));
		assertThat(dates).isNotNull();
	}
}
