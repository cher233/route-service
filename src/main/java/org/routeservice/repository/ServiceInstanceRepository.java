package org.routeservice.repository;

import org.routeservice.entity.ServiceInstanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceInstanceRepository  extends JpaRepository<ServiceInstanceEntity,Integer>{

    ServiceInstanceEntity findFirstByServiceId(String ServiceId);
}
