package club.zhcs.nail.sigar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hyperic.sigar.OperatingSystem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.stereotype.Component;

@Component
public class SigarMetrics implements PublicMetrics {

	@Autowired
	SigarService sigarService;

	@Override
	public Collection<Metric<?>> metrics() {
		List<Metric<?>> metrics = new ArrayList<Metric<?>>();
		try {
			Sigar sigar = sigarService.load();
			// CPU
			metrics.add(new Metric<Number>("cpu.total", sigar.getCpu().getTotal()));
			metrics.add(new Metric<Number>("cpu.idle", sigar.getCpu().getIdle()));
			metrics.add(new Metric<Number>("cpu.irq", sigar.getCpu().getIrq()));
			metrics.add(new Metric<Number>("cpu.nice", sigar.getCpu().getNice()));
			metrics.add(new Metric<Number>("cpu.soft.irq", sigar.getCpu().getSoftIrq()));
			metrics.add(new Metric<Number>("cpu.stolen", sigar.getCpu().getStolen()));
			metrics.add(new Metric<Number>("cpu.sys", sigar.getCpu().getSys()));
			metrics.add(new Metric<Number>("cpu.user", sigar.getCpu().getUser()));
			metrics.add(new Metric<Number>("cpu.wait", sigar.getCpu().getWait()));
			// MEM
			metrics.add(new Metric<Number>("mem.free", sigar.getMem().getFree()));
			metrics.add(new Metric<Number>("mem.actual.free", sigar.getMem().getActualFree()));
			metrics.add(new Metric<Number>("mem.actual.used", sigar.getMem().getActualUsed()));
			metrics.add(new Metric<Number>("mem.free.percent", sigar.getMem().getFreePercent()));
			metrics.add(new Metric<Number>("mem.ram", sigar.getMem().getRam()));
			metrics.add(new Metric<Number>("mem.total", sigar.getMem().getTotal()));
			metrics.add(new Metric<Number>("mem.used", sigar.getMem().getUsed()));
			metrics.add(new Metric<Number>("mem.user.percent", sigar.getMem().getUsedPercent()));
			// NET
			metrics.add(new Metric<Number>("tcp.active.opens", sigar.getTcp().getActiveOpens()));
			metrics.add(new Metric<Number>("tcp.attempt.fails", sigar.getTcp().getAttemptFails()));
			metrics.add(new Metric<Number>("tcp.curre.stab", sigar.getTcp().getCurrEstab()));
			metrics.add(new Metric<Number>("tcp.estab.resets", sigar.getTcp().getEstabResets()));
			metrics.add(new Metric<Number>("tcp.in.errs", sigar.getTcp().getInErrs()));
			metrics.add(new Metric<Number>("tcp.in.segs", sigar.getTcp().getInSegs()));
			metrics.add(new Metric<Number>("tcp.out.rsts", sigar.getTcp().getOutRsts()));
			metrics.add(new Metric<Number>("tcp.out.segs", sigar.getTcp().getOutSegs()));
			metrics.add(new Metric<Number>("tcp.passive.opens", sigar.getTcp().getPassiveOpens()));
			metrics.add(new Metric<Number>("tcp.retrans.segs", sigar.getTcp().getRetransSegs()));
			// SWAP
			metrics.add(new Metric<Number>("swap.free", sigar.getSwap().getFree()));
			metrics.add(new Metric<Number>("swap.page.in", sigar.getSwap().getPageIn()));
			metrics.add(new Metric<Number>("swap.page.out", sigar.getSwap().getPageOut()));
			metrics.add(new Metric<Number>("swap.total", sigar.getSwap().getTotal()));
			metrics.add(new Metric<Number>("swap.used", sigar.getSwap().getUsed()));
			// JVM
			Runtime r = Runtime.getRuntime();
			metrics.add(new Metric<Number>("jvm.max", r.maxMemory()));
			metrics.add(new Metric<Number>("jvm.free", r.freeMemory()));
			metrics.add(new Metric<Number>("jvm.total", r.totalMemory()));
			if (OperatingSystem.IS_WIN32) {

			} else {
				float ioUsage = 0.0f;
				Process pro = null;
				String command = "iostat -d -x";
				try {
					pro = r.exec(command);
					BufferedReader in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
					String line = null;
					int count = 0;
					while ((line = in.readLine()) != null) {
						if (++count >= 4) {
							String[] temp = line.split("\\s+");
							if (temp.length > 1) {
								float util = Float.parseFloat(temp[temp.length - 1]);
								ioUsage = (ioUsage > util) ? ioUsage : util;
							}
						}
					}
					if (ioUsage > 0) {
						ioUsage /= 100;
					}
					in.close();
					pro.destroy();
					metrics.add(new Metric<Number>("disk.io", ioUsage));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (SigarException e) {
			e.printStackTrace();
		}
		return metrics;
	}

}
