package com.avvero.carx.dao.jpa;

import com.avvero.carx.domain.Activity;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Avvero
 */
public interface ActivityRepository extends CrudRepository<Activity, Integer> {
}
