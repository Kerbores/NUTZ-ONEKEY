package club.zhcs.nail.sigar;

import java.io.File;

import org.hyperic.sigar.Sigar;
import org.springframework.stereotype.Component;

import com.google.common.io.Resources;

@Component
public class SigarService {

	public Sigar load() {
		String path = System.getProperty("java.library.path");
		path += File.pathSeparator + Resources.getResource("sigar").getFile();
		System.setProperty("java.library.path", path);
		return new Sigar();
	}
}
