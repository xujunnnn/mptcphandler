/*
 * Copyright © 2017 xujun and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.bupt.impl.packethandler;

import java.util.Arrays;

import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Ipv4Address;

public class SlaveSocket extends Socket{
	private byte[] token;
	
	public SlaveSocket(Host srcHost,Host destHost,Ipv4Address srcAddress,int srcPort,Ipv4Address destAddress,int destPort) {
		// TODO Auto-generated constructor stub
		super(srcHost, destHost, srcAddress, srcPort, destAddress, destPort);
	}

	public byte[] getToken() {
		return token;
	}

	public void setToken(byte[] token) {
		this.token = token;
	}

	@Override
	public String toString() {
		return "SlaveSocket [token=" + Arrays.toString(token) + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Arrays.hashCode(token);
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
		SlaveSocket other = (SlaveSocket) obj;
		if (!Arrays.equals(token, other.token))
			return false;
		return true;
	}
	
	

	
	
	

}
