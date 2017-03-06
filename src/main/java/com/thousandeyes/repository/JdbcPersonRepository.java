package com.thousandeyes.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.thousandeyes.models.Person;

@Repository
public class JdbcPersonRepository implements PersonRepository {
	
	@Autowired
	private NamedParameterJdbcTemplate db;

	@Override
	public List<Person> all() {
		return db
				.query("select * from person", (row, number) -> new Person(row.getLong("id"), row.getString("name")));
	}

	@Override
	public Person byId(long id) {
		Map<String, Object> params = new HashMap<>();
		params.put("id", id);
		List<Person> found = db
				.query("select * from person where id = :id", params, (rs, rowid) -> new Person(rs.getLong("id"), rs.getString("name")));
		return found.isEmpty() ? null : found.get(0);
	}

	@Override
	public List<Person> followers(Person p) {
		Map<String, Object> params = new HashMap<>();
		params.put("id", p.getId());
		return db
				.query("select p.* from person p join followers f on p.id = f.follower_person_id where f.person_id = :id",
						params,
						(rs, rowid) -> new Person(rs.getLong("id"), rs.getString("name")));
	}

	@Override
	public List<Person> following(Person p) {
		Map<String, Object> params = new HashMap<>();
		params.put("id", p.getId());
		return db
				.query("select p.* from person p join followers f on p.id = f.person_id where f.follower_person_id = :id",
						params,
						(rs, rowid) -> new Person(rs.getLong("id"), rs.getString("name")));

	}

	@Override
	public boolean follow(Person user, Person target) {
		Map<String, Long> params = new HashMap<>();
		params.put("person_id", target.getId());
		params.put("follower_person_id", user.getId());
		return db.update("insert into followers (person_id, follower_person_id) values (:person_id, :follower_person_id)", params) == 1;
	}
	
	@Override
	public boolean unfollow(Person user, Person target) {
		Map<String, Long> params = new HashMap<>();
		params.put("person_id", target.getId());
		params.put("follower_person_id", user.getId());
		return db.update("delete from followers where follower_person_id = :follower_person_id and person_id = :person_id", params) == 1;
	}

	@Override
	public boolean isFollowing(Person user, Person target) {
		Map<String, Long> params = new HashMap<>();
		params.put("person_id", target.getId());
		params.put("follower_person_id", user.getId());
		return db
				.query("select * from followers where follower_person_id = :follower_person_id and person_id = :person_id",
						params,
						(row, rowid) -> 1)
				.size() == 1;
	}

}
