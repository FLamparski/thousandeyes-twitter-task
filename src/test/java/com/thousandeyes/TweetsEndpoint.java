package com.thousandeyes;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
public class TweetsEndpoint {
	@Autowired
	private TestRestTemplate api;

	@Test
	public void tweets_shouldFetch() {
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> result = api.withBasicAuth("1", "thousandeyes").getForObject("/tweets/", List.class);
		
		assertTrue(result.stream().allMatch(p -> p.containsKey("id") && p.containsKey("person_id") && p.containsKey("content")));
	}
	
	@Test
	public void tweets_shouldAllowSearch() {
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> result = api.withBasicAuth("1", "thousandeyes").getForObject("/tweets/?search=lorem", List.class);
		
		// Only tweets that contain "lorem" should be returned
		assertTrue(result.stream().allMatch(p -> p.get("content").toString().contains("lorem")));
	}
}
