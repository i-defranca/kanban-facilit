package dev.challenge.repository;

import dev.challenge.entity.Member;
import dev.challenge.sluggable.SluggableRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends SluggableRepository<Member> {
}
