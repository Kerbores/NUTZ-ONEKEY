package club.zhcs.thunder;

import org.nutz.lang.Lang;

public class ThunderLauncher {

	public static void main(String[] args) {
		String path = System.getProperty("java.library.path");
		if (Lang.isWin()) {
			System.setProperty("java.library.path", path + ";src/main/webapp/WEB-INF/lib");// TODO
																							// 扩展的路径通过配置加载进来
		} else {
			System.setProperty("java.library.path", path + ":src/main/webapp/WEB-INF/lib");
		}
		// WebLauncher.start(args);
	}
}
