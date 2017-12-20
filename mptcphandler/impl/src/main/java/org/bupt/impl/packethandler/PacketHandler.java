/*
 * Copyright © 2017 xujun and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.bupt.impl.packethandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import org.bupt.impl.flow.ReactiveFlowWriter;
import org.bupt.impl.topo.NetWorkGraphService;
import org.bupt.impl.util.InstanceIdentifierUtils;
import org.opendaylight.l2switch.packethandler.decoders.utils.BitBufferHelper;
import org.opendaylight.l2switch.packethandler.decoders.utils.BufferException;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Ipv4Address;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Uri;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev130715.MacAddress;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.OutputActionCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.output.action._case.OutputActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.list.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.list.ActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.OutputPortValues;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeConnectorId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeConnectorRef;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeRef;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.Node;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.basepacket.rev140528.packet.chain.grp.PacketChain;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.basepacket.rev140528.packet.chain.grp.packet.chain.packet.RawPacket;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.ethernet.rev140528.ethernet.packet.received.packet.chain.packet.EthernetPacket;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.ipv4.rev140528.ipv4.packet.received.packet.chain.packet.Ipv4Packet;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.service.rev130709.PacketProcessingService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.service.rev130709.TransmitPacketInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.service.rev130709.TransmitPacketInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.tcp.rev140528.TcpPacketListener;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.tcp.rev140528.TcpPacketReceived;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.tcp.rev140528.tcp.packet.received.packet.chain.packet.TcpPacket;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NodeId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.TpId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Link;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;

public class PacketHandler implements TcpPacketListener {
	private PacketProcessingService packetProcessingService;
	private static final String HOST_PRFIX="host:";
	private ReactiveFlowWriter reactiveFlowWriter;
	private Map<MasterSock,List<SlaveSocket>> map=new ConcurrentHashMap();

	private final NetWorkGraphService service;
	public PacketHandler(NetWorkGraphService service,ReactiveFlowWriter reactiveFlowWriter,PacketProcessingService packetProcessingService) {
		// TODO Auto-generated constructor stub
		this.service=service;
		this.reactiveFlowWriter=reactiveFlowWriter;
		this.packetProcessingService=packetProcessingService;
	}
	
	
	@Override
	public void onTcpPacketReceived(TcpPacketReceived packetReceived) {
		// TODO Auto-generated method stub
		if(packetReceived==null || packetReceived.getPacketChain()==null){
			return;
		}
		RawPacket rawPacket=null;
		EthernetPacket ethernetPacket=null;
		Ipv4Packet ipv4Packet=null;
		TcpPacket tcpPacket=null;
 		for(PacketChain packetChain:packetReceived.getPacketChain()){
			 if (packetChain.getPacket() instanceof RawPacket) {
	                rawPacket = (RawPacket) packetChain.getPacket();
	            } else if (packetChain.getPacket() instanceof EthernetPacket) {
	                ethernetPacket = (EthernetPacket) packetChain.getPacket();
	            } else if (packetChain.getPacket() instanceof Ipv4Packet) {
	                ipv4Packet = (Ipv4Packet) packetChain.getPacket();
	            }else if(packetChain.getPacket() instanceof TcpPacket){
	            	tcpPacket=(TcpPacket)packetChain.getPacket();
	            }
	
			  
		}
		if (rawPacket == null || ethernetPacket == null || ipv4Packet == null || tcpPacket==null) {
            return;
       }
		String srchostname=HOST_PRFIX+ethernetPacket.getSourceMac().getValue();
		String desthostname=HOST_PRFIX+ethernetPacket.getDestinationMac().getValue();
		NodeId src=service.getNodeId(srchostname);
		NodeId dst=service.getNodeId(desthostname);
		int srcport=tcpPacket.getSourcePort();
		int destport=tcpPacket.getDestinationPort();
		Ipv4Address srcAddress=ipv4Packet.getSourceIpv4();
		Ipv4Address destAddress=ipv4Packet.getDestinationIpv4();		
		Host srchost=new Host();
		srchost.setHostname(srchostname);
		srchost.setIpv4Address(srcAddress);
		srchost.setMacAddress(ethernetPacket.getSourceMac());
		srchost.setConnectorId(service.getNodeConnectorId(srchostname));
		srchost.setNodeId(src);
		Host desthost=new Host();
		desthost.setHostname(desthostname);
		desthost.setIpv4Address(destAddress);
		desthost.setMacAddress(ethernetPacket.getDestinationMac());
		desthost.setConnectorId(service.getNodeConnectorId(desthostname));
		desthost.setNodeId(dst);
		
		TcpOptionParser parser=new TcpOptionParser(tcpPacket.getTcpOptions());	
		if(tcpPacket.isSYNFlag() && tcpPacket.isACKFlag()){
				
			try {
				if(parser.isCable()){
					MptcpCableOption cableOption=parser.getCable();
					MasterSock masterSock=new MasterSock(srchost,desthost,srcAddress,srcport,destAddress,destport);
					masterSock.setSenderKey(cableOption.getSenderKey());
					masterSock.setToken(InstanceIdentifierUtils.toToken(cableOption.getSenderKey()));
					handleMasterSock(masterSock, cableOption);
				}
				else if(parser.isJoin()) {
					MptcpJoinOption joinOption=parser.getJoin();
					SlaveSocket slaveSocket=new SlaveSocket(srchost, desthost, srcAddress, srcport, destAddress, destport);
					slaveSocket.setToken(joinOption.getRecToken());
				}
			} catch (BufferException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(tcpPacket.isSYNFlag() && !tcpPacket.isACKFlag()) {
			 try {
				if(parser.isJoin()) {
						MptcpJoinOption joinOption=parser.getJoin();
						SlaveSocket slaveSocket=new SlaveSocket(srchost, desthost, srcAddress, srcport, destAddress, destport);
						slaveSocket.setToken(joinOption.getRecToken());
						handleSlaveSock(slaveSocket, joinOption);
					}
				else if(parser.isCable()){
					sendPacketOut(packetReceived.getPayload(),src,srchost.getConnectorId(), dst,desthost.getConnectorId());
				}
			} catch (BufferException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
		
		


	}
	
	private void handleMasterSock(MasterSock masterSock,MptcpCableOption cableOption){
		if(map.containsKey(masterSock))
			return;
		NodeId src=masterSock.getSrcHost().getNodeId();
		NodeId dst=masterSock.getDestHost().getNodeId();
		if(src==null || dst==null)
			return;
		List<RoutePath>  routePaths=service.getAllRoutePaths(src,dst);
		if(routePaths.size()==0 || routePaths==null)
			return;
		RoutePath ablepath=routePaths.get(routePaths.size()-1);
		
		ablepath.getPath().get(0).setInport(masterSock.getSrcHost().getConnectorId());
		ablepath.getPath().get(ablepath.getPath().size()-1).setOutport(masterSock.getDestHost().getConnectorId());
		masterSock.setAblePath(ablepath);
		routePaths.remove(routePaths.size()-1);
		masterSock.flowAdd();
		reactiveFlowWriter.addFlow(masterSock.getSrcHost().getMacAddress(),masterSock.getDestHost().getMacAddress(),masterSock,ablepath);
		masterSock.setRoutePaths(routePaths);
		List<SlaveSocket> slaveSockets=new ArrayList();
		map.put(masterSock, slaveSockets);
		
		
		
	}
	
	private void handleSlaveSock(SlaveSocket slaveSocket,MptcpJoinOption joinOption){
		MasterSock masterSock=findMaster(slaveSocket);
		if(masterSock!=null){
			NodeId msn=masterSock.getSrcHost().getNodeId();
			NodeId mdn=masterSock.getDestHost().getNodeId();
			NodeId ssn=slaveSocket.getSrcHost().getNodeId();
			NodeId sdn=slaveSocket.getDestHost().getNodeId();
			if(!msn.getValue().equals(sdn.getValue()) || !mdn.getValue().equals(ssn.getValue()))
				return;
			List<RoutePath> paths=masterSock.getRoutePaths();
			if(paths==null || paths.size()==0)
				return;
 			RoutePath ablepath=paths.get(paths.size()-1);
 			ablepath.getPath().get(0).setInport(slaveSocket.getDestHost().getConnectorId());
 			ablepath.getPath().get(ablepath.getPath().size()-1).setOutport(slaveSocket.getSrcHost().getConnectorId());			
 			reactiveFlowWriter.addFlow(slaveSocket.getDestHost().getMacAddress(),slaveSocket.getSrcHost().getMacAddress(),slaveSocket.reverse(),ablepath);
 			slaveSocket.setAblePath(ablepath);
 			paths.remove(paths.size()-1);
 			map.get(masterSock).add(slaveSocket);
 			masterSock.flowAdd();
		}
		
		
	}
	
	private MasterSock findMaster(SlaveSocket slaveSocket){
		for(MasterSock masterSock:map.keySet()){
			if(Arrays.equals(masterSock.getToken(), slaveSocket.getToken())){
				return masterSock;
			}
		}
		return null;
	}
	
	
	
    public void sendPacketOut(byte[] payload,NodeId inNode ,NodeConnectorId inport,NodeId outNode, NodeConnectorId outport) {
        if (inport == null || outport == null)
            return;
        
        NodeConnectorRef ingress=new NodeConnectorRef(InstanceIdentifierUtils.createNodeConnectorIdentifier(inNode.getValue(), inport.getValue()));
        NodeConnectorRef egress=new NodeConnectorRef(InstanceIdentifierUtils.createNodeConnectorIdentifier(outNode.getValue(), outport.getValue()));
        InstanceIdentifier<Node> egressNodePath = getNodePath(egress.getValue());
        List<Action> actions=new ArrayList<>();
        Action outputToControllerAction = new ActionBuilder() //
                .setOrder(0)
                .setAction(new OutputActionCaseBuilder() //
                        .setOutputAction(new OutputActionBuilder() //
                                .setMaxLength(0xffff) //
                                .setOutputNodeConnector(new Uri(outport.getValue())) //
                                .build()) //
                        .build()) //
                .build();
        actions.add(outputToControllerAction);
        TransmitPacketInput input = new TransmitPacketInputBuilder() //
                .setPayload(payload) //
                .setNode(new NodeRef(egressNodePath)) //
                .setEgress(egress) //
               // .setIngress(egress)
                .setAction(actions)//)
                .build();
       
        packetProcessingService.transmitPacket(input);
    }
    
    private InstanceIdentifier<Node> getNodePath(final InstanceIdentifier<?> nodeChild) {
        return nodeChild.firstIdentifierOf(Node.class);
    }
	
	
/**
 * 	//get a path and add flow on this path
	private void handleSocket(MacAddress srcMac,MacAddress dstMac,HostPair hostPair,Socket socket){
 		if(!map.containsKey(hostPair)){
			NodeId src=service.getNodeId(hostPair.getSrcHost());
			NodeId dst=service.getNodeId(hostPair.getDestHost());
			List<RoutePath>  routePaths=service.getAllRoutePaths(src,dst);
			if(routePaths.size()==0)
				return;
			Map<Socket, SocketStatus> mmMap=new HashMap<>();
			SocketStatus socketStatus=new SocketStatus();
			RoutePath ablepath=routePaths.get(routePaths.size()-1);
			ablepath.getPath().get(0).setInport(service.getNodeConnectorId(hostPair.getSrcHost()));
			ablepath.getPath().get(ablepath.getPath().size()-1).setOutport(service.getNodeConnectorId(hostPair.getDestHost()));
			socketStatus.setPath(ablepath);
			routePaths.remove(routePaths.size()-1);
			hostPair.setRoutePaths(routePaths);
			hostPair.flowAdd();
			mmMap.put(socket, socketStatus);
			map.put(hostPair, mmMap);
			reactiveFlowWriter.addFlow(srcMac,dstMac, socket,socketStatus.getPath());
			
		}
		else if (map.containsKey(hostPair)) {
			List<RoutePath> routePaths=null;
			if(map.get(hostPair).containsKey(socket)){
				return;
			}
			else {
				Map<Socket, SocketStatus> mmMap=new HashMap<>();
				SocketStatus socketStatus=new SocketStatus();
				for(HostPair h:map.keySet()){
					if(hostPair.equals(h))
						routePaths=h.getRoutePaths();
				}
				if(routePaths ==null || routePaths.size()==0)
					return;
				RoutePath ablePath=routePaths.get(routePaths.size()-1);
				ablePath.getPath().get(0).setInport(service.getNodeConnectorId(hostPair.getSrcHost()));
				ablePath.getPath().get(ablePath.getPath().size()-1).setOutport(service.getNodeConnectorId(hostPair.getDestHost()));
				socketStatus.setPath(ablePath);
				routePaths.remove(routePaths.get(routePaths.size()-1));
				mmMap.put(socket, socketStatus);
				map.put(hostPair, mmMap);
				reactiveFlowWriter.addFlow(srcMac,dstMac,socket, socketStatus.getPath());
				
			}
			
		}
	}
 */

}
