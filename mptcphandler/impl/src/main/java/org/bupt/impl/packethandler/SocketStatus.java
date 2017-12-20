/*
 * Copyright © 2017 xujun and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.bupt.impl.packethandler;

public class SocketStatus {
	private TcpStatus tcpStatus;
	private RoutePath path;
	public TcpStatus getTcpStatus() {
		return tcpStatus;
	}
	public void setTcpStatus(TcpStatus tcpStatus) {
		this.tcpStatus = tcpStatus;
	}
	public RoutePath getPath() {
		return path;
	}
	public void setPath(RoutePath path) {
		this.path = path;
	}
	@Override
	public String toString() {
		return "SocketStatus [tcpStatus=" + tcpStatus + ", path=" + path + "]";
	}
	

}
