/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.controller.visualization.freemarker;

import java.util.Map;

import edu.cornell.mannlib.vitro.webapp.visualization.freemarker.visutils.VisualizationRequestHandler;

public class VisualizationInjector {
	private Map<String, VisualizationRequestHandler> visualizationIDToClass;

	public Map<String, VisualizationRequestHandler> getVisualizationIDToClass() {
		
		System.out.println("giving the vis id class " + visualizationIDToClass);
		return visualizationIDToClass;
	}

	public void setVisualizations(Map<String, VisualizationRequestHandler> visualizationIDToClass) {
		this.visualizationIDToClass = visualizationIDToClass;
		System.out.println("setting the vis id class " + visualizationIDToClass);
	}

}