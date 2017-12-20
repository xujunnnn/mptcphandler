/*
 * Copyright © 2017 xujun and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.bupt.impl.packethandler;

import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Ipv4Address;

public class Socket {
	private Host srcHost;
	private Host destHost;
	private Ipv4Address srcAddress;
	private int srcPort;
	private Ipv4Address destAddress;
	private int destPort;
	private RoutePath ablePath;
	public Socket() {
		// TODO Auto-generated constructor stub
	}
	
	public Socket(Host srcHost,Host destHost,Ipv4Address srcAddress,int srcPort,Ipv4Address destAddress,int destPort) {
		// TODO Auto-generated constructor stub
		this.setSrcHost(srcHost);
		this.setDestHost(destHost);
		this.srcAddress=srcAddress;
		this.srcPort=srcPort;
		this.destAddress=destAddress;
		this.destPort=destPort;
	}
	
	public Ipv4Address getSrcAddress() {
		return srcAddress;
	}

	public void setSrcAddress(Ipv4Address srcAddress) {
		this.srcAddress = srcAddress;
	}

	public int getSrcPort() {
		return srcPort;
	}

	public void setSrcPort(int srcPort) {
		this.srcPort = srcPort;
	}

	public Ipv4Address getDestAddress() {
		return destAddress;
	}

	public void setDestAddress(Ipv4Address destAddress) {
		this.destAddress = destAddress;
	}

	public int getDestPort() {
		return destPort;
	}

	public void setDestPort(int destPort) {
		this.destPort = destPort;
	}
	 
	public Socket reverse(){
		Socket socket=new Socket(destHost,srcHost,destAddress, destPort, srcAddress, srcPort);
		return socket;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((destAddress == null) ? 0 : destAddress.hashCode());
		result = prime * result + destPort;
		result = prime * result + ((srcAddress == null) ? 0 : srcAddress.hashCode());
		result = prime * result + srcPort;
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
		Socket other = (Socket) obj;
		if (destAddress == null) {
			if (other.destAddress != null)
				return false;
		} else if (!destAddress.equals(other.destAddress))
			return false;
		if (destPort != other.destPort)
			return false;
		if (srcAddress == null) {
			if (other.srcAddress != null)
				return false;
		} else if (!srcAddress.equals(other.srcAddress))
			return false;
		if (srcPort != other.srcPort)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Socket [srcAddress=" + srcAddress + ", srcPort=" + srcPort + ", destAddress=" + destAddress
				+ ", destPort=" + destPort + "]";
	}

	public RoutePath getAblePath() {
		return ablePath;
	}

	public void setAblePath(RoutePath ablePath) {
		this.ablePath = ablePath;
	}

	public Host getDestHost() {
		return destHost;
	}

	public void setDestHost(Host destHost) {
		this.destHost = destHost;
	}

	public Host getSrcHost() {
		return srcHost;
	}

	public void setSrcHost(Host srcHost) {
		this.srcHost = srcHost;
	}
	
	
}
