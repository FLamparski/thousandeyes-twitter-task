package com.thousandeyes;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PeopleEndpoint {	
	@Autowired
	private TestRestTemplate api;
	
	@Test
	public void t_0_people_shouldRequireAuthentication() {
		ResponseEntity<Object> e = api.getForEntity("/people/1", Object.class);
		
		assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());
	}
	
	@Test
	public void t_1_people_shouldGetFollowers() {
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> result = (List<Map<String, Object>>) api.withBasicAuth("1", "thousandeyes").getForObject("/people/1/followers", List.class);
		
		assertEquals(6, result.size());
		assertEquals(10, result.get(0).get("id"));
	}
	
	@Test
	public void t_2_people_shouldCreateFollower() {
		@SuppressWarnings("unchecked")
		Map<String, Object> result = (Map<String, Object>) api.withBasicAuth("1", "thousandeyes").postForObject("/people/9/follow", null, Map.class);
		assertEquals("ok", result.get("status"));
		
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> ninesFollowers = (List<Map<String, Object>>) api.withBasicAuth("1", "thousandeyes").getForObject("/people/9/followers", List.class);
		// The current user, 1, should now be a follower of 9
		assertEquals(1, ninesFollowers.get(ninesFollowers.size() - 1).get("id"));
	}
	
	@Test
	public void t_3_people_getFollowing() {
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> result = (List<Map<String, Object>>) api.withBasicAuth("1", "thousandeyes").getForObject("/people/1/following", List.class);
		
		assertEquals(6, result.size());
		// Check that 1 is following 9
		assertTrue(result.stream().anyMatch(p -> (Integer) p.get("id") == 9));
	}

	@Test
	public void t_4_people_shouldDeleteFollower() {
		api.withBasicAuth("1", "thousandeyes").delete("/people/9/follow");
		
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> ninesFollowers = (List<Map<String, Object>>) api.withBasicAuth("1", "thousandeyes").getForObject("/people/9/followers", List.class);
		// User 1 should no longer be a follower of 9
		assertEquals(7, ninesFollowers.get(ninesFollowers.size() - 1).get("id"));
	}

	@Test
	public void t_5_people_popularFollower() {
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> peopleWithFollowers = api.withBasicAuth("1", "thousandeyes").getForObject("/people/popular_followers", List.class);

		//noinspection unchecked
        Map<String, Object> person1 = peopleWithFollowers.stream().filter(p -> ((Map<String, Object>) p.get("person")).get("id").equals(1)).findFirst().orElse(null);
		assertTrue(person1 != null);
		assertEquals(6, person1.get("popularFollowerFollowerCount"));
        //noinspection unchecked
        assertEquals(5, ((Map<String, Object>) person1.get("popularFollower")).get("id"));
	}
}
