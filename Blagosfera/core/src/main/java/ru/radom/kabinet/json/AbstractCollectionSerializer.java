package ru.radom.kabinet.json;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Transactional
abstract public class AbstractCollectionSerializer<T> {

	private static final Logger logger = LoggerFactory.getLogger(AbstractCollectionSerializer.class);

	@Autowired
	protected SerializationManager serializationManager;

	public JSONArray serialize(Collection<T> collection) {

		assert (collection != null);
		assert (collection.size() > 0);

		long start = System.currentTimeMillis();

		JSONArray jsonArray = serializeInternal(collection);
		long stop = System.currentTimeMillis();
		long time = (stop - start);
//		logger.info("collection of " + collection.size() + " " + collection.toArray()[0].getClass().getSimpleName() + " serialized in " + time + " milliseconds");
		return jsonArray;

	}

	abstract public JSONArray serializeInternal(Collection<T> collection);

}
