package com.fusesource.forge.jmstest.rrd;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.text.DateFormatter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rrd4j.ConsolFun;
import org.rrd4j.core.Archive;
import org.rrd4j.core.RrdDb;
import org.rrd4j.graph.RrdGraph;
import org.rrd4j.graph.RrdGraphDef;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class RRDGraphGenerator implements ApplicationContextAware, GraphGenerator {
	
	private ApplicationContext appContext;
	private String baseDir = "target";

	private Log log = null;
	
	public void createGraphs() {
		if (!checkGraphDir()) {
			log().error(getBaseDir() + " is not accessible. No graphs will be generated.");
			return;
		}
		
		String[] beanNames = appContext.getBeanNamesForType(RRDController.class);
		for (String name: beanNames) {
			RRDController controller = (RRDController)appContext.getBean(name);
			RrdDb db = controller.getDatabase();
			try {
				for(int i=0; i<db.getArcCount(); i++) {
					Archive arch = db.getArchive(i);
						for(String dsName: db.getDsNames()) {
							renderGraph(controller, arch, dsName);
						}
				}
			} catch (Exception e) {
				log().error("Error while generating graphs for : " + controller.getFileName(), e);
			}
		}
	}
	
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.appContext = applicationContext;
	}

	public String getBaseDir() {
		return baseDir;
	}

	public void setBaseDir(String baseDir) {
		this.baseDir = baseDir;
	}

	private Log log() {
		if (log == null) {
			log = LogFactory.getLog(this.getClass());
		}
		return log;
	}

	private boolean checkGraphDir() {
		File dir = new File(getBaseDir());
		
		if (dir.exists()) {
			if (!dir.isDirectory()) {
				log().error(getBaseDir() + " is not a directory.");
				return false;
			}
			if (!dir.canWrite()) {
				log().error(getBaseDir() + " is not writable.");
				return false;
			}
		} else {
			return dir.mkdirs();
		}
		
		return true;
	}
	
	private void renderGraph(RRDController controller, Archive arch, String dsName) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");

		StringBuffer title = new StringBuffer(dsName);
		title.append(" ");
		title.append(sdf.format(new Date(arch.getStartTime() * 1000)));
		title.append("-");
		title.append(sdf.format(new Date(arch.getEndTime() * 1000)));
		
		log().debug("Creating Graph: " + title);

		RrdGraphDef graphDef = new RrdGraphDef();
		
		graphDef.setTitle(title.toString());
		graphDef.setTimeSpan(arch.getStartTime(), arch.getEndTime());
		graphDef.datasource(dsName, controller.getFileName(), dsName, ConsolFun.AVERAGE);
		graphDef.setStep(arch.getArcStep());
		graphDef.setWidth(800);
		graphDef.setHeight(600);
		graphDef.line(dsName, Color.BLACK, dsName);
		graphDef.setImageFormat("PNG");
		graphDef.setFilename(getBaseDir() + "/" + dsName + ".png");
		RrdGraph graph = new RrdGraph(graphDef);
		BufferedImage bi = new BufferedImage(100,100,BufferedImage.TYPE_INT_RGB);
		graph.render(bi.getGraphics());	
	}
}