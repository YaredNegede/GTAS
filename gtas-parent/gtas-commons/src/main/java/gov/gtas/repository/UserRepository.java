/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import gov.gtas.model.User;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, String> {

	default User findOne(String userId) {
		return findById(userId).orElse(null);
	}

	@Query("select u from User u left join fetch u.userGroups where u.userId = :userId")
	Optional<User> userAndGroups(@Param("userId") String userId);

	@Query( "select u.email from User u inner join u.roles r where r.roleId in :roleIds" )
	List<String> findEmailsByRoleIds(@Param("roleIds") List<Integer> roleIds);

	@Query("select u from User u where u.archived = false  OR u.archived IS NULL")
	Iterable<User> getNonArchivedUsers();

	@Query("select u.userId from User u where u.email = :userEmail")
	String findUserByEmail(@Param("userEmail") String userEmail);
}