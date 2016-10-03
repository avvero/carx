package com.avvero.carx.dao.mongo;

import org.bson.Document;

/**
 * @author Avvero
 */
public interface CustomerDataRepositoryExt {

    void save(String uuid, Document doc);

    void update(String uuid, Document doc);

    Document findOneByUuid(String uuid);
}
