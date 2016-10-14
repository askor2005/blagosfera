package ru.radom.kabinet.json;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
abstract public class AbstractSerializer<T> {

	private static final Logger logger = LoggerFactory.getLogger(AbstractSerializer.class);

	@Autowired
	protected SerializationManager serializationManager;

	public JSONObject serialize(T object) {
		long start = System.currentTimeMillis();
		JSONObject jsonObject = serializeInternal(object);
		long stop = System.currentTimeMillis();
		long time = (stop - start);
//		logger.info(object.toString() + " serialized in " + time + " milliseconds");
		return jsonObject;
	}

	abstract public JSONObject serializeInternal(T object);

}
