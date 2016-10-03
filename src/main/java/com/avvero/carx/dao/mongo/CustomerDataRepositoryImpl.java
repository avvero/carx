package com.avvero.carx.dao.mongo;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;

import static java.lang.String.format;

/**
 * @author Avvero
 */
public class CustomerDataRepositoryImpl implements CustomerDataRepositoryExt {

    public static final String COLLECTION_NAME = "customerData";
    public static final String FIND_BY_ID_QUERY = "{_id: \"%s\" }";

    @Autowired
    private MongoTemplate mongoTemplate;

        @Override
    public void save(String uuid, Document doc) {
        doc.put("_id", uuid); //TODO required to use something like @Id
        mongoTemplate.insert(doc, COLLECTION_NAME);
    }

    @Override
    public void update(String uuid, Document doc) {
        doc.put("_id", uuid); //TODO required to use something like @Id
        mongoTemplate.save(doc, COLLECTION_NAME);
    }

    @Override
    public Document findOneByUuid(String uuid) {
        BasicQuery query = new BasicQuery(format(FIND_BY_ID_QUERY, uuid));
        Document document = mongoTemplate.findOne(query, Document.class, COLLECTION_NAME);
        if (document != null) {
            document.remove("_id"); //TODO redundant information or not
        }
        return document;
    }
}
