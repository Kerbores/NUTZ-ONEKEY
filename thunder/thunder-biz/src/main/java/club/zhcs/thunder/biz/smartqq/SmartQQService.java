package club.zhcs.thunder.biz.smartqq;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.webqq.callback.MessageCallback;
import org.nutz.plugins.webqq.client.WebQQClient;
import org.nutz.plugins.webqq.model.Discuss;
import org.nutz.plugins.webqq.model.DiscussMessage;
import org.nutz.plugins.webqq.model.Group;
import org.nutz.plugins.webqq.model.GroupInfo;
import org.nutz.plugins.webqq.model.GroupMessage;
import org.nutz.plugins.webqq.model.Message;

/**
 * @author kerbores
 *
 * @email kerbores@gmail.com
 *
 */
@IocBean
public class SmartQQService {

	private final Log LOGGER = Logs.getLog(getClass());

	private WebQQClient kerbores;

	@Inject
	PropertiesProxy config;

	private static final Set<Long> UNPUSH_GROUPS = new CopyOnWriteArraySet<>();

	private final Map<Long, Group> QQ_GROUPS = new ConcurrentHashMap<>();

	private final Map<Long, Long> GROUP_AD_TIME = new ConcurrentHashMap<>();

	private final Map<Long, Discuss> QQ_DISCUSSES = new ConcurrentHashMap<>();

	private final Map<Long, Long> DISCUSS_AD_TIME = new ConcurrentHashMap<>();

	private int PUSH_GROUP_USER_COUNT = 1;

	private static final int PUSH_GROUP_COUNT = 5;

	/**
	 * 发送消息到所有群
	 * 
	 * @param msg
	 *            消息
	 */
	public void sendToPushQQGroups(final String msg) {
		try {
			final String pushGroupsConf = config.get("qq.bot.pushGroups");
			if (Strings.isBlank(pushGroupsConf)) {
				return;
			}

			// Push to all groups
			if (Strings.equals(pushGroupsConf, "*")) {
				int totalUserCount = 0;
				int groupCount = 0;

				if (UNPUSH_GROUPS.isEmpty()) { // 如果没有可供推送的群（群都推送过了）
					reloadGroups();
				}

				for (final Map.Entry<Long, Group> entry : QQ_GROUPS.entrySet()) {
					long groupId = 0;
					int userCount = 0;

					try {
						final Group group = entry.getValue();
						groupId = group.getId();

						final GroupInfo groupInfo = kerbores.getGroupInfo(group.getCode());
						userCount = groupInfo.getUsers().size();
						if (userCount < PUSH_GROUP_USER_COUNT) {
							// 把人不多的群过滤掉
							UNPUSH_GROUPS.remove(groupId);
							continue;
						}

						if (!UNPUSH_GROUPS.contains(groupId)) {
							// 如果该群已经被推送过则跳过本次推送
							continue;
						}

						if (groupCount >= PUSH_GROUP_COUNT) { // 如果本次群推操作已推送群数大于设定的阈值
							break;
						}
						LOGGER.info("群发 [" + msg + "] 到 QQ 群 [" + group.getName() + ", 成员数=" + userCount + "]");
						kerbores.sendMessageToGroup(groupId, msg); // 发送消息

						UNPUSH_GROUPS.remove(groupId); // 从未推送中移除（说明已经推送过）

						totalUserCount += userCount;
						groupCount++;

						Thread.sleep(1000 * 10);
					} catch (final Exception e) {
						LOGGER.error(e);
					}
				}
				LOGGER.info("一共推送了 [" + groupCount + "] 个群，覆盖 [" + totalUserCount + "] 个 QQ");
				return;
			}

			// Push to the specified groups
			final String[] groups = pushGroupsConf.split(",");
			Arrays.sort(groups);
			for (final Map.Entry<Long, Group> entry : QQ_GROUPS.entrySet()) {
				final Group group = entry.getValue();
				final String name = group.getName();

				if (Arrays.binarySearch(groups, name) > 0) {
					final GroupInfo groupInfo = kerbores.getGroupInfo(group.getCode());
					final int userCount = groupInfo.getUsers().size();
					if (userCount < 100) {
						continue;
					}

					LOGGER.info("Pushing [msg=" + msg + "] to QQ qun [" + group.getName() + "]");
					kerbores.sendMessageToGroup(group.getId(), msg); // 发送消息
					Thread.sleep(1000 * 10);
				}
			}
		} catch (final Exception e) {
			LOGGER.debug("Push message [" + msg + "] to groups failed", e);
		}
	}

	/**
	 * 重新加载群
	 */
	public void reloadGroups() {
		final List<org.nutz.plugins.webqq.model.Group> groups = kerbores.getGroupList();
		QQ_GROUPS.clear();
		GROUP_AD_TIME.clear();
		UNPUSH_GROUPS.clear();

		final StringBuilder msgBuilder = new StringBuilder();
		msgBuilder.append("Reloaded groups: \n");
		for (final Group g : groups) {
			QQ_GROUPS.put(g.getId(), g);
			GROUP_AD_TIME.put(g.getId(), 0L);
			UNPUSH_GROUPS.add(g.getId());
			msgBuilder.append("    ").append(g.getName()).append(": ").append(g.getId()).append("\n");
		}

		LOGGER.debug(msgBuilder.toString());
	}

	public void initQQClient() {
		LOGGER.info("开始初始化小薇");

		kerbores = new WebQQClient(new MessageCallback() {
			@Override
			public void onMessage(final Message message) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(500 + R.random(0, 1000));

							final String content = message.getContent();
							final String key = config.get("qq.bot.key");

							if (!content.startsWith(key)) { // 不是管理命令，只是普通的私聊
								kerbores.sendMessageToFriend(message.getUserId(), "我是 kerbores!");
								return;
							}

							final String msg = content.replaceAll(key, "");
							LOGGER.info("Received admin message: " + msg);
							sendToPushQQGroups(msg);
						} catch (final Exception e) {
							LOGGER.debug("XiaoV on group message error", e);
						}
					}

				}).start();
			}

			@Override
			public void onGroupMessage(final GroupMessage message) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(500 + R.random(0, 1000));
							System.err.println(message.getContent());
						} catch (final Exception e) {
							LOGGER.error("XiaoV on group message error", e);
						}
					}

				}).start();
			}

			@Override
			public void onDiscussMessage(final DiscussMessage message) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(500 + R.random(0, 1000));
							System.err.println(message.getContent());
						} catch (final Exception e) {
							LOGGER.error("XiaoV on group message error", e);
						}
					}
				}).start();
			}
		});

		// Load groups & disscusses
		reloadGroups();
		reloadDiscusses();

		LOGGER.info("小薇初始化完毕");

		LOGGER.info("小薇 QQ 机器人服务开始工作！");
	}

	/**
	 * 
	 */
	private void reloadDiscusses() {
		final List<Discuss> discusses = kerbores.getDiscussList();
		QQ_DISCUSSES.clear();
		DISCUSS_AD_TIME.clear();

		final StringBuilder msgBuilder = new StringBuilder();
		msgBuilder.append("Reloaded discusses: \n");
		for (final Discuss d : discusses) {
			QQ_DISCUSSES.put(d.getId(), d);
			DISCUSS_AD_TIME.put(d.getId(), 0L);

			msgBuilder.append("    ").append(d.getName()).append(": ").append(d.getId()).append("\n");
		}

		LOGGER.debug(msgBuilder.toString());
	}

}
