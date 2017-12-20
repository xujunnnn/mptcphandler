/*
 * Copyright © 2017 xujun and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.bupt.impl.packethandler;

import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeConnectorId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.node.NodeConnector;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NodeId;

public class RouteNode {
	private NodeConnectorId outport;
	private NodeId nodeId;
	private NodeConnectorId inport;
	public RouteNode() {
		// TODO Auto-generated constructor stub
	}
	
	public RouteNode(NodeConnectorId inport,NodeConnectorId outport,NodeId nodeId){
		this.inport=inport;
		this.outport=outport;
		this.nodeId=nodeId;
	}
	public NodeConnectorId getOutport() {
		return outport;
	}
	public void setOutport(NodeConnectorId outport) {
		this.outport = outport;
	}
	public NodeId getNodeId() {
		return nodeId;
	}
	public void setNodeId(NodeId nodeId) {
		this.nodeId = nodeId;
	}
	public NodeConnectorId getInport() {
		return inport;
	}
	public void setInport(NodeConnectorId inport) {
		this.inport = inport;
	}
	
	public RouteNode reverse(){
		return new RouteNode(outport, inport, nodeId);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((inport == null) ? 0 : inport.hashCode());
		result = prime * result + ((nodeId == null) ? 0 : nodeId.hashCode());
		result = prime * result + ((outport == null) ? 0 : outport.hashCode());
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
		RouteNode other = (RouteNode) obj;
		if (inport == null) {
			if (other.inport != null)
				return false;
		} else if (!inport.equals(other.inport))
			return false;
		if (nodeId == null) {
			if (other.nodeId != null)
				return false;
		} else if (!nodeId.equals(other.nodeId))
			return false;
		if (outport == null) {
			if (other.outport != null)
				return false;
		} else if (!outport.equals(other.outport))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "RouteNode [outport=" + outport + ", nodeId=" + nodeId + ", inport=" + inport + "]";
	}
	

	

}
