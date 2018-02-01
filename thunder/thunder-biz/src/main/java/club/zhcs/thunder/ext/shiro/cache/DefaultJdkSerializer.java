package club.zhcs.thunder.ext.shiro.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * 
 * @author kerbores@gmail.com
 *
 */
public class DefaultJdkSerializer implements CacheSerializer {

	private static final Log log = Logs.get();

	@Override
	public Object fromObject(Object obj) {
		try {
			ByteArrayOutputStream bao = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bao);
			oos.writeUnshared(obj);
			oos.close();
			return bao.toByteArray();
		} catch (Exception e) {
			log.info("Object to bytes fail", e);
			return null;
		}
	}

	@Override
	public Object toObject(Object obj) {
		if (obj == null) {
			return null;
		}
		try {
			ObjectInputStream ins = new ObjectInputStream(new ByteArrayInputStream((byte[]) obj));
			Object tmp = ins.readUnshared();
			ins.close();
			return tmp;
		} catch (Exception e) {
			log.info("bytes to Object fail", e);
			return null;
		}
	}
}
