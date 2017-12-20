/*
 * Copyright © 2017 xujun and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.bupt.impl.packethandler;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Ipv4Address;

public class MasterSock extends Socket{
	private Host srcHost;
	private Host destHost;
	private byte[] senderKey;
	private byte[] token;
	private List<RoutePath> routePaths;
	private AtomicInteger ableFlows=new AtomicInteger();
	public MasterSock() {
		// TODO Auto-generated constructor stub
	}
	
	public MasterSock(Host srcHost,Host destHost,Ipv4Address srcAddress,int srcPort,Ipv4Address destAddress,int destPort) {
		// TODO Auto-generated constructor stub
		super(srcHost, destHost, srcAddress, srcPort, destAddress, destPort);
	
	}
	
	
	public void flowAdd(){
		this.ableFlows.getAndIncrement();
	}
	
	public Host getSrcHost() {
		return srcHost;
	}

	public void setSrcHost(Host srcHost) {
		this.srcHost = srcHost;
	}

	public Host getDestHost() {
		return destHost;
	}

	public void setDestHost(Host destHost) {
		this.destHost = destHost;
	}

	public byte[] getSenderKey() {
		return senderKey;
	}

	public void setSenderKey(byte[] senderKey) {
		this.senderKey = senderKey;
	}

	public byte[] getToken() {
		return token;
	}

	public void setToken(byte[] token) {
		this.token = token;
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
	public String toString() {
		return "MasterSock [srcHost=" + srcHost + ", destHost=" + destHost + ", senderKey=" + Arrays.toString(senderKey)
				+ ", token=" + Arrays.toString(token) + ", routePaths=" + routePaths + ", ableFlows=" + ableFlows + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Arrays.hashCode(senderKey);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		MasterSock other = (MasterSock) obj;
		if (!Arrays.equals(senderKey, other.senderKey))
			return false;
		return true;
	}
	


	

	
	
	

}
