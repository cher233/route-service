package org.routeservice.repository;

import org.routeservice.entity.FilterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilterRepository extends JpaRepository<FilterEntity, Integer> {
    Long countByFilterId();
}
