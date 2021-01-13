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

import java.io.UncheckedIOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

@Service
public class GithubStars {
	private static final String GITHUB_STAR_API = "/stargazers?per_page=100";

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final ObjectMapper mapper;
	private final RestTemplate rest;

	public GithubStars(ObjectMapper mapper, RestTemplateBuilder rest) {
		this.mapper = mapper;
		this.rest = rest.defaultHeader("Accept", "application/vnd.github.v3.star+json").build();
	}

	private static final Pattern LINK_PATTERN = Pattern.compile("<(.*?)>; rel=\"next\"");

	public Map<LocalDate, Long> getStarsByMonth(LocalDate start, LocalDate end) {
		List<Stargazer> all = new ArrayList<>();
		String link = GITHUB_STAR_API;
		do {
			ResponseEntity<String> entity = this.rest.execute(link, HttpMethod.GET,
					this.rest.acceptHeaderRequestCallback(null),
					this.rest.responseEntityExtractor(String.class));
			link = nextLink(CollectionUtils.firstElement(entity.getHeaders().get("Link")));
			List<Stargazer> gazers = readValue(entity.getBody(), new TypeReference<>() {});
			all.addAll(gazers);
		} while (link != null);
		logger.debug("Retrieved {} stars", all.size());
		all.sort(Comparator.comparing((gazer) -> gazer.starredAt));
		Map<LocalDate, Long> dates = new LinkedHashMap<>();
		LocalDate s = start;
		while (!s.isAfter(end)) {
			LocalDate e = s.plusMonths(1).minusDays(s.getDayOfMonth());
			if (e.isAfter(end)) {
				e = end;
			}
			dates.put(s, count(all, e));
			s = e.plusDays(1);
		}
		return dates;
	}

	private Long count(List<Stargazer> gazers, LocalDate end) {
		return gazers.stream()
				.map((gazer) -> gazer.starredAt.toLocalDate())
				.filter((date) -> !date.isAfter(end))
				.count();
	}

	private <T> T readValue(String body, TypeReference<T> type) {
		try {
			return this.mapper.readValue(body, type);
		} catch (JsonProcessingException e) {
			throw new UncheckedIOException(e);
		}
	}

	String nextLink(String header) {
		if (header == null) {
			return null;
		}
		String[] links = header.split(",");
		for (String link : links) {
			Matcher m = LINK_PATTERN.matcher(link);
			if (m.find()) {
				return m.group(1);
			}
		}
		return null;
	}

	static class Stargazer {
		private final LocalDateTime starredAt;

		Stargazer(@JsonProperty("starred_at") LocalDateTime starredAt) {
			this.starredAt = starredAt;
		}
	}
}
