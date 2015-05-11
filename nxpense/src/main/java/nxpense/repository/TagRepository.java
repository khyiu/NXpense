package nxpense.repository;

import nxpense.domain.Tag;
import nxpense.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface TagRepository extends JpaRepository<Tag, Integer> {

    /**
     * Retrieves the tags that are associated to the specified {@code User} object
     * @param user Owner of the tags to be returned
     * @return List of tags belonging to {@code user}, alphabetically ordered by tag name
     */
    // @Query("select t from Tag t join User u where u = :user and t in u.tags order by t.name")
    @Query("select t from User u join u.tags t where u = :user order by t.name")
    public List<Tag> findByUserOrderByName(@Param(value = "user") User user);
}
