/*
 * Copyright © 2017 xujun and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.bupt.impl.packethandler;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class HostPair {
	private String srcHost;
	private String destHost;
	
	private MasterSock masterSock;
	private List<RoutePath> routePaths;
	private AtomicInteger ableFlows=new AtomicInteger();
	public String getSrcHost() {
		return srcHost;
	}
	public void setSrcHost(String srcHost) {
		this.srcHost = srcHost;
	}
	public String getDestHost() {
		return destHost;
	}
	public void setDestHost(String destHost) {
		this.destHost = destHost;
	}
	public MasterSock getMasterSock() {
		return masterSock;
	}
	public void setMasterSock(MasterSock masterSock) {
		this.masterSock = masterSock;
	}
	public List<RoutePath> getRoutePaths() {
		return routePaths;
	}
	public void setRoutePaths(List<RoutePath> routePaths) {
		this.routePaths = routePaths;
	}
	public AtomicInteger getAbleFlows() {
		return ableFlows;
	}
	public void setAbleFlows(AtomicInteger ableFlows) {
		this.ableFlows = ableFlows;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((destHost == null) ? 0 : destHost.hashCode());
		result = prime * result + ((masterSock == null) ? 0 : masterSock.hashCode());
		result = prime * result + ((srcHost == null) ? 0 : srcHost.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HostPair other = (HostPair) obj;
		if (destHost == null) {
			if (other.destHost != null)
				return false;
		} else if (!destHost.equals(other.destHost))
			return false;
		if (masterSock == null) {
			if (other.masterSock != null)
				return false;
		} else if (!masterSock.equals(other.masterSock))
			return false;
		if (srcHost == null) {
			if (other.srcHost != null)
				return false;
		} else if (!srcHost.equals(other.srcHost))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "HostPair [srcHost=" + srcHost + ", destHost=" + destHost + ", masterSock=" + masterSock
				+ ", routePaths=" + routePaths + ", ableFlows=" + ableFlows + "]";
	}
	
	
	

}
