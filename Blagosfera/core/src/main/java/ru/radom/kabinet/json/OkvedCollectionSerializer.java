package ru.radom.kabinet.json;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.model.OkvedEntity;

import java.util.Collection;

@Component("okvedCollectionSerializer")
public class OkvedCollectionSerializer extends AbstractCollectionSerializer<OkvedEntity> {

	@Autowired
	private OkvedSerializer okvedSerialiazer;
	
	@Override
	public JSONArray serializeInternal(Collection<OkvedEntity> collection) {
		JSONArray result = new JSONArray();
		
		for(OkvedEntity o : collection) {
			result.put(okvedSerialiazer.serialize(o));
		}
		
		return result;
	}

}
