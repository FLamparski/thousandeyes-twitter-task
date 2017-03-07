package com.thousandeyes.repository;

import com.thousandeyes.models.Person;
import com.thousandeyes.models.PersonWithPopularFollower;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	@Override
	public List<PersonWithPopularFollower> withPopularFollowers() {
		return db
				.query("select p.id, p.name, max(popf.id) as popular_follower_id, max(popf.name) as popular_follower_name, max(popf.follower_count) as popular_follower_follower_count from person p join followers f on p.id = f.person_id join (select p1.id, p1.name, count(f1.id) as follower_count from person p1 join followers f1 on p1.id = f1.person_id group by p1.id order by follower_count desc) popf on f.follower_person_id = popf.id group by p.id",
						(row, rowid) -> new PersonWithPopularFollower(
								new Person(row.getLong("id"), row.getString("name")),
								new Person(row.getLong("popular_follower_id"), row.getString("popular_follower_name")),
								row.getInt("popular_follower_follower_count")));
	}

}
