/*
 * Copyright © 2017 xujun and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.bupt.impl.topo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Node;
import org.bupt.impl.packethandler.RouteNode;
import org.bupt.impl.packethandler.RoutePath;
import org.opendaylight.yang.gen.v1.urn.opendaylight.host.tracker.rev140624.HostNode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeConnectorId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NodeId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Link;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import edu.uci.ics.jung.algorithms.shortestpath.PrimMinimumSpanningTree;
import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
public class NetworkGraphImpl implements NetWorkGraphService{
	private static final Logger LOG=LoggerFactory.getLogger(NetworkGraphImpl.class);
	private List<Node> nodes=new ArrayList<>();
	public List<Node> getNodes() {
		return nodes;
	}

	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}

	Graph<NodeId, Link> networkGraph=null;
	Set<String> linkAdded=new HashSet<>();
	 // Enable following lines when shortest path functionality is required.
    // DijkstraShortestPath<NodeId, Link> shortestPath = null;

    /**
     * Adds links to existing graph or creates new directed graph with given
     * links if graph was not initialized.
     *
     * @param links
     *            The links to add.
     */
	@Override
	public void addLinks(List<Link> links) {
		// TODO Auto-generated method stub
		if(links==null || links.isEmpty()){
			 LOG.info("In addLinks: No link added as links is null or empty.");
	         return;
		}
		if(networkGraph==null){
			networkGraph=new SparseMultigraph<>();
		}
		for(Link link:links){
			if(linkAlreadyAdded(link)){
				continue;
			}
			
			NodeId sourceNodeId=link.getSource().getSourceNode();
			NodeId destinationNodeId=link.getDestination().getDestNode();
			networkGraph.addVertex(sourceNodeId);
			networkGraph.addVertex(destinationNodeId);
			networkGraph.addEdge(link, sourceNodeId, destinationNodeId,EdgeType.DIRECTED);
		}
	}
	
	 private boolean linkAlreadyAdded(Link link) {
	        String linkAddedKey = null;
	        
	   
	           linkAddedKey = link.getDestination().getDestTp().getValue() + link.getSource().getSourceTp().getValue();
	        
	        if (linkAdded.contains(linkAddedKey)) {
	            return true;
	        } else {
	            linkAdded.add(linkAddedKey);
	            return false;
	        }
	    }


	    /**
	     * Removes links from existing graph.
	     *
	     * @param links
	     *            The links to remove.
	     */
	    @Override
	    public synchronized void removeLinks(List<Link> links) {
	        Preconditions.checkNotNull(networkGraph, "Graph is not initialized, add links first.");

	        if (links == null || links.isEmpty()) {
	            LOG.info("In removeLinks: No link removed as links is null or empty.");
	            return;
	        }

	        for (Link link : links) {
	            networkGraph.removeEdge(link);
	        }
	        /*
	         * if(shortestPath == null) { shortestPath = new
	         * DijkstraShortestPath<>(networkGraph); } else { shortestPath.reset();
	         * }
	         */

	    }

	    /**
	     * returns a path between 2 nodes. Uses Dijkstra's algorithm to return
	     * shortest path.
	     *
	     * @param sourceNodeId
	     * @param destinationNodeId
	     * @return
	     */
	    // @Override
	    /*
	     * public synchronized List<Link> getPath(NodeId sourceNodeId, NodeId
	     * destinationNodeId) { Preconditions.checkNotNull(shortestPath,
	     * "Graph is not initialized, add links first.");
	     *
	     * if(sourceNodeId == null || destinationNodeId == null) { _logger.info(
	     * "In getPath: returning null, as sourceNodeId or destinationNodeId is null."
	     * ); return null; }
	     *
	     * return shortestPath.getPath(sourceNodeId, destinationNodeId); }
	     */

	    /**
	     * Clears the prebuilt graph, in case same service instance is required to
	     * process a new graph.
	     */
	    @Override
	    public synchronized void clear() {
	        networkGraph = null;
	        linkAdded.clear();
	        // shortestPath = null;
	    }
	@Override
	public List<Link> getLinkInMst() {
		// TODO Auto-generated method stub
		List<Link> linksInMst=new ArrayList<>();
		if(networkGraph!=null){
			PrimMinimumSpanningTree<NodeId, Link> networkMst=new PrimMinimumSpanningTree<>(DelegateTree.<NodeId, Link>getFactory());
			Graph<NodeId, Link> mstGraph=networkMst.transform(networkGraph);
			Collection<Link> mstLinks=mstGraph.getEdges();
			linksInMst.addAll(mstLinks);
			
		}
		return linksInMst;
	}

	@Override
	public List<Link> getAllLinks() {
		// TODO Auto-generated method stub
		 List<Link> allLinks = new ArrayList<>();
	        if (networkGraph != null) {
	            allLinks.addAll(networkGraph.getEdges());
	        }
	        return allLinks;
	}

	@Override
	public List<Node> getAllNodes() {
		// TODO Auto-generated method stub
		return nodes;
	}

	

	@Override
	public List<RoutePath> getAllRoutePaths(NodeId src, NodeId dest) {
		// TODO Auto-generated method stub
		List<RoutePath> result=new ArrayList<>();
		List<List<Link>> allPaths=new ArrayList();
		List<Link> currentPath = new ArrayList();
		findAllUniquePaths (src, src,dest,currentPath,networkGraph, 20, 0, allPaths); 
		for(List<Link> path:allPaths){
			RoutePath routePath=new RoutePath();
			for(int i=0;i<path.size();i++){
				RouteNode routeNode=new RouteNode();
				routeNode.setNodeId(path.get(i).getSource().getSourceNode());
				routeNode.setOutport(new NodeConnectorId(path.get(i).getSource().getSourceTp()));
				if(i!=0)
					routeNode.setInport(new NodeConnectorId(path.get(i-1).getDestination().getDestTp()));
				routePath.addNode(routeNode);				
			}
			RouteNode destnode=new RouteNode();
			destnode.setNodeId(dest);
			destnode.setInport(new NodeConnectorId(path.get(path.size()-1).getDestination().getDestTp()));
			routePath.addNode(destnode);
			result.add(routePath);
			
			
		}
		return result;
		
	}
	private void findAllPath(NodeId src,NodeId dest,ArrayList<NodeId> path){
		if(src==dest)
			return;
		for(NodeId nodeId:networkGraph.getNeighbors(src)){
			if(networkGraph.getEndpoints(networkGraph.findEdge(src, nodeId))!=null){
				path.add(nodeId);
				findAllPath(nodeId, dest, path);
				path.remove(nodeId);
			}
		}
	}
	
	private  void  findAllUniquePaths(NodeId currentNode, NodeId startNode, NodeId endNode,List<Link> currentPath, Graph<NodeId, Link> graph,
	        int maxDepth, int currentDepth,List<List<Link>> allPaths)
	{
	    Collection<Link> outgoingEdges = graph.getOutEdges(currentNode);

	    if (currentDepth < maxDepth)
	    {
	        for (Link outEdge : outgoingEdges)
	        {	
	        	if(LinkContains(currentPath, outEdge)){
	        		continue;
	        	}
	            NodeId outNode =outEdge.getDestination().getDestNode(); 
	            //String outNode = outEdge.getSupertype();
	            
	            if (outNode.getValue().equals(startNode.getValue()))
	            {
	                List<Link> cyclePath = new ArrayList(currentPath);
	                cyclePath.add(outEdge);
	                System.out.println("Found cycle provoked by path " + cyclePath);
	                continue;
	            }

	            List<Link> newPath = new ArrayList(currentPath);
	            newPath.add(outEdge);

	            if (outNode.equals(endNode))
	            {
	                //Check for unique-ness before adding.
	                boolean unique = true;
	                //Check each existing path.
	        
	                //After we check all the edges, in all the paths,
	                //if it is still unique, we add it.
	                if (unique)
	                {
	                    allPaths.add(newPath);
	                }
	                continue;
	            }
	            findAllUniquePaths(outNode, startNode, endNode, newPath, graph, maxDepth, currentDepth + 1,allPaths);
	        }
	    }
	}

	@Override
	public NodeId getNodeId(String hostId) {
		// TODO Auto-generated method stub
		for(Node node:nodes){
			if(hostId.equals(node.getNodeId().getValue())){
				String tpid=node.getAugmentation(HostNode.class).getAttachmentPoints().get(0).getTpId().getValue();
				String tpinfo[]=tpid.split(":");
				String nodestr=tpinfo[0]+":"+tpinfo[1];
				return new NodeId(nodestr);
			}
		}
		return null;
	}
	
	public boolean LinkContains(List<Link> links,Link link){
		for(Link l:links){
			if(l.getSource().getSourceNode().equals(l.getSource().getSourceNode()) 
					&& l.getDestination().getDestNode().equals(link.getDestination().getDestNode())
					|| l.getSource().getSourceNode().equals(link.getDestination().getDestNode()) 
					&& l.getDestination().getDestNode().equals(link.getSource().getSourceNode())
					){
				return true;
			}
			
		}
		return false;
	}
	@Override
	public NodeConnectorId getNodeConnectorId(String hostId) {
		// TODO Auto-generated method stub
		for(Node node:nodes){
			if(hostId.equals(node.getNodeId().getValue())){
				String tpid=node.getAugmentation(HostNode.class).getAttachmentPoints().get(0).getTpId().getValue();
				return new NodeConnectorId(tpid);
			}
		}
		return null;
	}



}
