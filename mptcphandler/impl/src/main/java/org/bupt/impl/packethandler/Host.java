/*
 * Copyright © 2017 xujun and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.bupt.impl.packethandler;

import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Ipv4Address;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev130715.MacAddress;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeConnectorId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NodeId;


public class Host {
	private String hostname;
	private MacAddress macAddress;
	private Ipv4Address ipv4Address;
	private NodeConnectorId connectorId;
	private NodeId nodeId;
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public MacAddress getMacAddress() {
		return macAddress;
	}
	public void setMacAddress(MacAddress macAddress) {
		this.macAddress = macAddress;
	}
	public Ipv4Address getIpv4Address() {
		return ipv4Address;
	}
	public void setIpv4Address(Ipv4Address ipv4Address) {
		this.ipv4Address = ipv4Address;
	}
	public NodeConnectorId getConnectorId() {
		return connectorId;
	}
	public void setConnectorId(NodeConnectorId connectorId) {
		this.connectorId = connectorId;
	}
	public NodeId getNodeId() {
		return nodeId;
	}
	public void setNodeId(NodeId nodeId) {
		this.nodeId = nodeId;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((connectorId == null) ? 0 : connectorId.hashCode());
		result = prime * result + ((hostname == null) ? 0 : hostname.hashCode());
		result = prime * result + ((ipv4Address == null) ? 0 : ipv4Address.hashCode());
		result = prime * result + ((macAddress == null) ? 0 : macAddress.hashCode());
		result = prime * result + ((nodeId == null) ? 0 : nodeId.hashCode());
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
		Host other = (Host) obj;
		if (connectorId == null) {
			if (other.connectorId != null)
				return false;
		} else if (!connectorId.equals(other.connectorId))
			return false;
		if (hostname == null) {
			if (other.hostname != null)
				return false;
		} else if (!hostname.equals(other.hostname))
			return false;
		if (ipv4Address == null) {
			if (other.ipv4Address != null)
				return false;
		} else if (!ipv4Address.equals(other.ipv4Address))
			return false;
		if (macAddress == null) {
			if (other.macAddress != null)
				return false;
		} else if (!macAddress.equals(other.macAddress))
			return false;
		if (nodeId == null) {
			if (other.nodeId != null)
				return false;
		} else if (!nodeId.equals(other.nodeId))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Host [hostname=" + hostname + ", macAddress=" + macAddress + ", ipv4Address=" + ipv4Address
				+ ", connectorId=" + connectorId + ", nodeId=" + nodeId + "]";
	}


	

}
