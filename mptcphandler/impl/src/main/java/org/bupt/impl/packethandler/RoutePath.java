/*
 * Copyright © 2017 xujun and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.bupt.impl.packethandler;

import java.util.ArrayList;
import java.util.List;

public class RoutePath {
	private List<RouteNode> path=new ArrayList<RouteNode>();

	public List<RouteNode> getPath() {
		return path;
	}

	public void setPath(List<RouteNode> path) {
		this.path = path;
	}
	public RoutePath addNode(RouteNode node){
		path.add(node);
		return this;
	}
	

}
