package com.thousandeyes.repository;

import com.thousandeyes.models.Person;
import com.thousandeyes.models.PersonWithPopularFollower;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
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
        // Query returns a cross product of all users and their followers with their own follower counts,
        // ordered by first the user ID, then the user's followers' follower counts, and finally to break
        // ties the follower's user ID.
        List<PersonWithPopularFollower> peopleWithFollowers = db
                .query("select p.id, p.name, popf.id as popf_id, popf.name as popf_name, popf.fc as popf_fc\n" +
                                "from person p\n" +
                                "join followers f on p.id = f.person_id\n" +
                                "join (select p.*, count(f.id) as fc\n" +
                                "  from person p\n" +
                                "  join followers f on p.id = f.person_id\n" +
                                "  group by p.id, p.name\n" +
                                "  order by fc desc) popf on f.follower_person_id = popf.id\n" +
                                "order by p.id asc, fc desc, popf_id asc",
                        (row, rowid) -> new PersonWithPopularFollower(
                                new Person(row.getLong("id"), row.getString("name")),
                                new Person(row.getLong("popf_id"), row.getString("popf_name")),
                                row.getInt("popf_fc")));

        // I make use of these orderings here to easily grab the top follower for each user.
        // I was not sure on how I could accomplish this in SQL (or at least in (the version of) H2 as provided),
        // however I think it is still possible to improve on the query and not need to do any further
        // processing here.
        // I made some more attempts but ended up crashing H2 instead; see "database fail.png"
        Map<Long, PersonWithPopularFollower> index = new HashMap<>();
        for (PersonWithPopularFollower p : peopleWithFollowers) {
            index.putIfAbsent(p.getPerson().getId(), p);
        }

        return new ArrayList<>(index.values());
    }

}
