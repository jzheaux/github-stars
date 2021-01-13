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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.assertj.core.api.Assertions.assertThat;

@RestClientTest(GithubStars.class)
class GithubStarsTests {

	@Autowired
	MockRestServiceServer server;

	@Autowired
	GithubStars stars;

	@Autowired
	ObjectMapper mapper;

	@Test
	void getStarredAtByMonth() throws Exception {
		List<GithubStars.Stargazer> gazers = new ArrayList<>();
		gazers.add(new GithubStars.Stargazer(LocalDateTime.now()));
		byte[] body = this.mapper.writeValueAsBytes(gazers);
		this.server.expect((request) -> request.getURI().toString().contains("/stargazers"))
				.andRespond((request) -> new MockClientHttpResponse(body, HttpStatus.OK));
		Map<LocalDate, Long> dates = this.stars.getStarsByMonth(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 12, 31));
		assertThat(dates).isNotNull();
	}
}