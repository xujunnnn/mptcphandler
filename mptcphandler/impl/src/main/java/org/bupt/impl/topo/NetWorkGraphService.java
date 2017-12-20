/*
 * Copyright © 2017 xujun and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.bupt.impl.topo;
import java.util.List;

import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Node;
import org.bupt.impl.packethandler.RoutePath;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeConnectorId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NodeId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Link;
public interface NetWorkGraphService {
	public void setNodes(List<Node> nodes);
	/**
     * Adds links to existing graph or creates new graph with given links if
     * graph was not initialized.
     *
     * @param links
     */
	public void addLinks(List<Link> links);
	/**
     * removes links from existing graph.
     *
     * @param links
     */
	public void removeLinks(List<Link> links);
	/**
     * returns a path between 2 nodes. Implementation should ideally return
     * shortest path.
     *
     * @param sourceNodeId
     * @param destinationNodeId
     * @return
     */
    // public List<Link> getPath(NodeId sourceNodeId, NodeId destinationNodeId);

    /**
     * Forms MST(minimum spanning tree) from network graph and returns links
     * that are not in MST.
     *
     * @return
     */
	public List<Link> getLinkInMst();
    /**
     * returns all the links in current network graph.
     *
     * @return
     */
	public List<Link> getAllLinks();
	/**
     * Clears the prebuilt graph, in case same service instance is required to
     * process a new graph.
     */
	public List<Node> getAllNodes();
	/**
	 * 
	 * @param src
	 * @param dest
	 * @return
	 */
	public List<RoutePath> getAllRoutePaths(NodeId src,NodeId dest);
	/**
	 * 
	 * @param hostId
	 * @return
	 */
	public NodeId getNodeId(String hostId);
	/**
	 * 
	 * @param hostId
	 * @return
	 */
	public NodeConnectorId getNodeConnectorId(String hostId);
	
	public void clear();

}
