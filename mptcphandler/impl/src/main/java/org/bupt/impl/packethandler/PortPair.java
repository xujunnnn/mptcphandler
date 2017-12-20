/*
 * Copyright © 2017 xujun and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.bupt.impl.packethandler;

public class PortPair {
	private int srcport;
	private int destport;
	public int getSrcport() {
		return srcport;
	}
	public void setSrcport(int srcport) {
		this.srcport = srcport;
	}
	public int getDestport() {
		return destport;
	}
	public void setDestport(int destport) {
		this.destport = destport;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + destport;
		result = prime * result + srcport;
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
		PortPair other = (PortPair) obj;
		if (destport != other.destport)
			return false;
		if (srcport != other.srcport)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "PortPair [srcport=" + srcport + ", destport=" + destport + "]";
	}
	

}
