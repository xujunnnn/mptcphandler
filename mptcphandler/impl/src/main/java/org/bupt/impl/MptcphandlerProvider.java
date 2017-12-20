/*
 * Copyright © 2017 xujun and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.bupt.impl;


import org.bupt.impl.flow.InitialFlowWriter;
import org.bupt.impl.flow.ReactiveFlowWriter;
import org.bupt.impl.packethandler.PacketHandler;
import org.bupt.impl.topo.NetWorkGraphService;
import org.bupt.impl.topo.NetworkGraphImpl;
import org.bupt.impl.topo.TopologyLinkDataChangeHandler;
import org.bupt.impl.topo.TopologyNodeDataChangeHandler;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.DataChangeListener;
import org.opendaylight.controller.sal.binding.api.NotificationProviderService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.service.rev130819.SalFlowService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.mptcp.handler.config.rev140528.MptcpHandlerConfig;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.service.rev130709.PacketProcessingService;
import org.opendaylight.yangtools.concepts.ListenerRegistration;
import org.opendaylight.yangtools.concepts.Registration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MptcphandlerProvider {
    private static final Logger LOG = LoggerFactory.getLogger(MptcphandlerProvider.class);
    private static final String topoid="flow:1";
    private final DataBroker dataBroker;
    private final NotificationProviderService notificationProviderService;
    private final SalFlowService salFlowService;
    private final PacketProcessingService packetProcessingService;
    private final MptcpHandlerConfig mptcpHandlerConfig;
    private ListenerRegistration<DataChangeListener> registration=null;
    private Registration listenerRegistration = null;
    private Registration topoNodeListenerReg = null; 
    private Registration NodeListenerReg = null; 
    public MptcphandlerProvider(final DataBroker dataBroker,final NotificationProviderService notificationProviderService,final SalFlowService salFlowService,final PacketProcessingService packetProcessingService,final MptcpHandlerConfig mptcpHandlerConfig) {
        this.dataBroker = dataBroker;       
        this.notificationProviderService=notificationProviderService;
        this.salFlowService=salFlowService;
        this.packetProcessingService=packetProcessingService;
        this.mptcpHandlerConfig=mptcpHandlerConfig;
    }

    /**
     * Method called when the blueprint container is created.
     */
    public void init() {
        LOG.info("MptcphandlerProvider Session Initiated");
        NetWorkGraphService netWorkGraphService=new NetworkGraphImpl();
        TopologyLinkDataChangeHandler handler=new TopologyLinkDataChangeHandler(dataBroker, netWorkGraphService);
        handler.setTopologyId(topoid);
        registration=handler.registerAsDataChangeListener();
        TopologyNodeDataChangeHandler nodeDataChangeHandler=new TopologyNodeDataChangeHandler(dataBroker, netWorkGraphService);
        nodeDataChangeHandler.setTopologyId(null);
        NodeListenerReg=nodeDataChangeHandler.registerAsDataChangeListener();
        InitialFlowWriter initialFlowWriter=new InitialFlowWriter(salFlowService);
        initialFlowWriter.setFlowTableId(mptcpHandlerConfig.getMptcpFlowTableId());
        initialFlowWriter.setFlowPriority(mptcpHandlerConfig.getMptcpFlowPriority());
        initialFlowWriter.setFlowHardTimeout(mptcpHandlerConfig.getMptcpFlowHardTimeout());
        initialFlowWriter.setFlowIdleTimeout(mptcpHandlerConfig.getMptcpFlowIdleTimeout());
        initialFlowWriter.setVlanId(mptcpHandlerConfig.getMptcpFlowVlan());
        topoNodeListenerReg=initialFlowWriter.registerAsDataChangeListener(dataBroker);
        
        ReactiveFlowWriter reactiveFlowWriter=new ReactiveFlowWriter(salFlowService);
        reactiveFlowWriter.setFlowPriority(mptcpHandlerConfig.getRouteFlowPriority());
        reactiveFlowWriter.setFlowIdleTimeout(mptcpHandlerConfig.getRouteFlowIdleTimeout());
        reactiveFlowWriter.setFlowHardTimeout(mptcpHandlerConfig.getRouteFlowHardTimeout());
        
        PacketHandler packetHandler=new PacketHandler(netWorkGraphService,reactiveFlowWriter,packetProcessingService);
        listenerRegistration=notificationProviderService.registerNotificationListener(packetHandler);    
    
        
    }

    /**
     * Method called when the blueprint container is destroyed.
     */
    public void close() {
        LOG.info("MptcphandlerProvider Closed");
        registration.close();
        try {
			listenerRegistration.close();
			topoNodeListenerReg.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }
}