package com.thousandeyes.controllers;

import com.thousandeyes.errors.BadRequestException;
import com.thousandeyes.errors.NotFoundException;
import com.thousandeyes.models.Person;
import com.thousandeyes.models.PersonWithPopularFollower;
import com.thousandeyes.models.Status;
import com.thousandeyes.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/people")
public class PersonController {

    @Autowired
    private PersonRepository persons;

    @RequestMapping("/")
    public List<Person> all() {
        return persons.all();
    }

    @RequestMapping("/popular_followers")
    public List<PersonWithPopularFollower> popularFollowers() {
        return persons.withPopularFollowers();
    }

    @RequestMapping("/{id}")
    public Person get(@PathVariable("id") long id) {
        Person p = persons.byId(id);
        if (p == null) {
            throw new NotFoundException("Could not find this person");
        }
        return p;
    }

    @RequestMapping("/{id}/followers")
    public List<Person> followers(@PathVariable("id") long id) {
        return persons.followers(get(id));
    }

    @RequestMapping("/{id}/following")
    public List<Person> following(@PathVariable("id") long id) {
        return persons.following(get(id));
    }

    @RequestMapping(path = "/{id}/follow", method = RequestMethod.POST)
    public Status follow(@AuthenticationPrincipal final Person user, @PathVariable("id") long targetId) {
        if (user.getId() == targetId) {
            throw new BadRequestException("Cannot follow yourself");
        }

        Person existing = following(user.getId()).stream().filter(p -> p.getId() == targetId).findFirst().orElse(null);
        if (existing != null) {
            throw new BadRequestException("You are already following this user");
        }

        persons.follow(user, get(targetId));
        return new Status("ok");
    }

    @RequestMapping(path = "/{id}/follow", method = RequestMethod.DELETE)
    public Status unfollow(@AuthenticationPrincipal final Person user, @PathVariable("id") long targetId) {
        if (user.getId() == targetId) {
            throw new BadRequestException("Cannot unfollow yourself");
        }

        Person target = get(targetId);
        if (!persons.isFollowing(user, target)) {
            throw new BadRequestException("You are not following this user.");
        }

        persons.unfollow(user, target);
        return new Status("ok");
    }
}
