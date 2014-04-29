package orz.yanagin.stragengine;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.AsyncDatastoreService;
import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class Datastore {

	private static final String KEY_SEPARATOR = "@";

	private static final String KIND_DATA = "DATA_PIECE";

	private static final String KIND_LIST = "DATA_LIST";

	private static final int ENTITY_SIZE_LIMIT = 10000;

	@SuppressWarnings("unchecked")
	public static Data get(String uri) throws EntityNotFoundException, IOException {
		Key listKey = new KeyFactory.Builder(KIND_LIST, uri).getKey();
		Entity listEntity = getDatastoreService().get(listKey);

		List<Key> pieceKeys = (List<Key>)listEntity.getProperty("pieceKeys");
		Map<Key, Entity> entities = getDatastoreService().get(pieceKeys);

		List<Entity> sortedEntities = sort(entities);

		Entity entity = join(sortedEntities);

		return new Data(
				String.valueOf(listEntity.getProperty("data")),
				String.valueOf(listEntity.getProperty("name")),
				String.valueOf(listEntity.getProperty("contentType")),
				Blob.class.cast(entity.getProperty("data")).getBytes());
	}

	static List<Entity> sort(Map<Key, Entity> entities) {
		List<Entity> result = new ArrayList<Entity>();
		result.addAll(entities.values());

		Collections.sort(result, new Comparator<Entity>() {
			@Override
			public int compare(Entity entity1, Entity entity2) {
				String name1 = entity1.getKey().getName();
				String name2 = entity2.getKey().getName();
				return Integer.parseInt(name1.substring(name1.indexOf("@") + 1)) - Integer.parseInt(name2.substring(name2.indexOf("@") + 1));
			}
		});

		return result;
	}

	static Entity join(List<Entity> entities) throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		for (Entity entity : entities) {
			if (entity == null) {
				break;
			}
			bytes.write(Blob.class.cast(entity.getProperty("data")).getBytes());
		}

		Entity entity = entities.get(0);
		entity.setProperty("data", new Blob(bytes.toByteArray()));
		return entity;
	}

	public static void put(Data data) {
		List<Entity> list = separate(data);

		list.add(createListEntity(data, list));

		getDatastoreService().put(list);
	}

	static List<Entity> separate(Data data) {
		if (data.getData() == null || data.getData().length == 0) {
			return null;
		}

		List<Entity> result = new ArrayList<Entity>();
		while (true) {
			Entity entity = createEntityPiece(result.size(), data);
			if (entity == null) {
				break;
			}
			result.add(entity);
		}

		return result;
	}

	static Entity createEntityPiece(int index, Data data) {
		int size = data.getData().length;

		int from = index * ENTITY_SIZE_LIMIT;
		if (from >= size) {
			return null;
		}

		int to = (index + 1) * ENTITY_SIZE_LIMIT;
		if (to > size) {
			to = size;
		}

		Key key = new KeyFactory.Builder(KIND_DATA, data.getUri() + KEY_SEPARATOR + index).getKey();

		Entity result = new Entity(key);
		result.setProperty("data", new Blob(Arrays.copyOfRange(data.getData(), from, to)));

		return result;
	}

	static Entity createListEntity(Data data, List<Entity> entities) {
		Key key = new KeyFactory.Builder(KIND_LIST, data.getUri()).getKey();

		Entity result = new Entity(key);
		result.setProperty("uri", data.getUri());
		result.setProperty("name", data.getName());
		result.setProperty("contentType", data.getContentType());
		result.setProperty("pieceKeys", getPieceKeys(entities));
		return result;
	}

	static List<Key> getPieceKeys(List<Entity> entities) {
		List<Key> result = new ArrayList<Key>();

		for (Entity entity : entities) {
			result.add(entity.getKey());
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public static void remove(String uri) throws EntityNotFoundException {
		Key listKey = new KeyFactory.Builder(KIND_LIST, uri).getKey();
		Entity listEntity = getDatastoreService().get(listKey);

		List<Key> pieceKeys = (List<Key>)listEntity.getProperty("pieceKeys");

		AsyncDatastoreService datastore = DatastoreServiceFactory.getAsyncDatastoreService();
		datastore.delete(listKey);
		datastore.delete(pieceKeys);
	}

	static DatastoreService getDatastoreService() {
		return DatastoreServiceFactory.getDatastoreService();
	}

}
