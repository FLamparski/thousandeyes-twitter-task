package com.thousandeyes.repository;

import com.thousandeyes.models.Person;
import com.thousandeyes.models.Tweet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class JdbcTweetRepository implements TweetRepository {

    @Autowired
    private NamedParameterJdbcTemplate db;

    @Override
    public List<Tweet> feedFor(Person p) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", p.getId());
        return db
                .query("select t.* from tweet t join followers f on t.person_id = f.person_id where f.follower_person_id = :id union all select * from tweet where person_id = :id order by id desc",
                        params,
                        (row, rowid) -> new Tweet(row.getLong("id"), row.getLong("person_id"), row.getString("content")));
    }

}
