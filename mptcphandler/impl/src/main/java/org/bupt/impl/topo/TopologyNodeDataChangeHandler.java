/*
 * Copyright © 2017 xujun and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.bupt.impl.topo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.bupt.impl.util.InstanceIdentifierUtils;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.DataChangeListener;
import org.opendaylight.controller.md.sal.binding.api.ReadOnlyTransaction;
import org.opendaylight.controller.md.sal.common.api.data.AsyncDataBroker.DataChangeScope;
import org.opendaylight.controller.md.sal.common.api.data.AsyncDataChangeEvent;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NetworkTopology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.TopologyId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.Topology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.TopologyKey;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Node;
import org.opendaylight.yangtools.concepts.ListenerRegistration;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

public class TopologyNodeDataChangeHandler implements DataChangeListener{
	private static final Logger LOG = LoggerFactory.getLogger(TopologyNodeDataChangeHandler.class);
    private static final String DEFAULT_TOPOLOGY_ID = "flow:1";
    private static final long DEFAULT_GRAPH_REFRESH_DELAY = 1000;
    private ScheduledExecutorService topologyDataChangeEventProcessor=Executors.newScheduledThreadPool(1);
    private final NetWorkGraphService netWorkGraphService;
    private boolean nodeRefreshSchedule=false;
    private boolean threadReschedule=false;
    private long graphRefreshDelay;
    private String topologyId;
    private final DataBroker dataBroker;
    
    public TopologyNodeDataChangeHandler(DataBroker dataBroker,NetWorkGraphService netWorkGraphService) {
		// TODO Auto-generated constructor stub
    	Preconditions.checkNotNull(dataBroker, "dataBroker should not be null.");
        Preconditions.checkNotNull(netWorkGraphService, "networkGraphService should not be null.");
    	this.dataBroker=dataBroker;
    	this.netWorkGraphService=netWorkGraphService;
	}
    
    public void setGraphRefreshDelay(long graphRefreshDelay) {
        if (graphRefreshDelay < 0) {
            this.graphRefreshDelay = DEFAULT_GRAPH_REFRESH_DELAY;
        } else
            this.graphRefreshDelay = graphRefreshDelay;
    }

    public void setTopologyId(String topologyId) {
        if (topologyId == null || topologyId.isEmpty()) {
            this.topologyId = DEFAULT_TOPOLOGY_ID;
        } else
            this.topologyId = topologyId;
    }
    
    public ListenerRegistration<DataChangeListener> registerAsDataChangeListener() {
        InstanceIdentifier<Node> nodeInstance = InstanceIdentifier.builder(NetworkTopology.class)
                .child(Topology.class, new TopologyKey(new TopologyId(topologyId))).child(Node.class).build();
        return dataBroker.registerDataChangeListener(LogicalDatastoreType.OPERATIONAL, nodeInstance, this,
                DataChangeScope.BASE);
    }
    
    
	@Override
	public void onDataChanged(AsyncDataChangeEvent<InstanceIdentifier<?>, DataObject> dataChangeEvent) {
		// TODO Auto-generated method stub
		    Map<InstanceIdentifier<?>, DataObject> createdData = dataChangeEvent.getCreatedData();
	        Set<InstanceIdentifier<?>> removedPaths = dataChangeEvent.getRemovedPaths();
	        Map<InstanceIdentifier<?>, DataObject> originalData = dataChangeEvent.getOriginalData();
	        boolean isNodeUpdated = false;
	        if (createdData != null && !createdData.isEmpty()) {
	            Set<InstanceIdentifier<?>> nodesIds = createdData.keySet();
	            for (InstanceIdentifier<?> nodeId : nodesIds) {
	                if (Node.class.isAssignableFrom(nodeId.getTargetType())) {
	                    Node node = (Node) createdData.get(nodeId);
	                    if (node.getNodeId().getValue().contains("host")) {
	                        isNodeUpdated = true;
	                        LOG.debug("Node is updated! Added Link {}", node.getNodeId().getValue());
	                        break;
	                    }
	                }
	            }
	        }
	        if (removedPaths != null && !removedPaths.isEmpty()) {
	        
	            for (InstanceIdentifier<?> nodeId : removedPaths) {
	                if (Node.class.isAssignableFrom(nodeId.getTargetType())) {
	                    Node node = (Node) createdData.get(nodeId);
	                    if (node.getNodeId().getValue().contains("host")) {
	                        isNodeUpdated = true;
	                        LOG.debug("Node is updated! Added Link {}", node.getNodeId().getValue());
	                        break;
	                    }
	                }
	            }
	        }
	        if(!isNodeUpdated){
	        	return;
	        }
	        if (!nodeRefreshSchedule) {
	            synchronized (this) {
	                if (!nodeRefreshSchedule) {
	                    topologyDataChangeEventProcessor.schedule(new TopologyNodeChangeEventProcessor(), graphRefreshDelay,
	                            TimeUnit.MILLISECONDS);
	                    nodeRefreshSchedule = true;
	                    LOG.debug("Scheduled Graph for refresh.");
	                }
	            }
	        } else {
	            LOG.debug("Already scheduled for network graph refresh.");
	            threadReschedule = true;
	        }
		
	}
	private class TopologyNodeChangeEventProcessor implements Runnable{

        @Override
        public void run() {
            if (threadReschedule) {
                topologyDataChangeEventProcessor.schedule(this, graphRefreshDelay, TimeUnit.MILLISECONDS);
                threadReschedule = false;
                return;
            }
            LOG.debug("In network graph refresh thread.");
            nodeRefreshSchedule = false;
            List<Node> nodes = getNodesFromTopology();
            if (nodes== null || nodes.isEmpty()) {
                return;
            }
            netWorkGraphService.setNodes(nodes);
            LOG.debug("Done with network graph refresh thread.");
        }
		private List<Node> getNodesFromTopology(){
			 InstanceIdentifier<Topology> topologyInstanceIdentifier = InstanceIdentifierUtils
	                    .generateTopologyInstanceIdentifier(topologyId);
			 Topology topology=null;
			 ReadOnlyTransaction readOnlyTransaction=dataBroker.newReadOnlyTransaction();
			 try {
				Optional<Topology> topologyOptional=readOnlyTransaction.read(LogicalDatastoreType.OPERATIONAL, topologyInstanceIdentifier).get();
				if(topologyOptional.isPresent()){
					topology=topologyOptional.get();
				}
			} catch (InterruptedException | ExecutionException e) {
				LOG.error("Error reading topology {}", topologyInstanceIdentifier);
               readOnlyTransaction.close();
               throw new RuntimeException(
                       "Error reading from operational store, topology : " + topologyInstanceIdentifier, e);
			}
			  readOnlyTransaction.close();
	          if (topology == null) {
	              return null;
	          }
	          List<Node> nodes=topology.getNode();
	          
	            if (nodes == null || nodes.isEmpty()) {
	                return null;
	            }
	            List<Node> hostNodes = new ArrayList<>();
	            for (Node node : nodes) {
	                if (node.getNodeId().getValue().contains("host")) {
	                	hostNodes.add(node);
	                }
	            }
	            return hostNodes;
	          
			 
		}
		
	}
    

}
