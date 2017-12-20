/*
 * Copyright © 2017 xujun and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.bupt.impl.flow;

import java.math.BigInteger;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

import org.bupt.impl.packethandler.RouteNode;
import org.bupt.impl.packethandler.RoutePath;
import org.bupt.impl.packethandler.Socket;
import org.bupt.impl.util.InstanceIdentifierUtils;
import org.opendaylight.openflowplugin.api.OFConstants;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Ipv4Prefix;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.PortNumber;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Uri;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev130715.MacAddress;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.OutputActionCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.output.action._case.OutputActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.list.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.list.ActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.FlowId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.Table;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.TableKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.Flow;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.FlowBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.FlowKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.service.rev130819.AddFlowInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.service.rev130819.AddFlowOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.service.rev130819.FlowTableRef;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.service.rev130819.SalFlowService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.FlowCookie;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.FlowModFlags;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.FlowRef;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.flow.InstructionsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.flow.Match;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.flow.MatchBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.instruction.ApplyActionsCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.instruction.apply.actions._case.ApplyActions;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.instruction.apply.actions._case.ApplyActionsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.list.Instruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.list.InstructionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeConnectorRef;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeRef;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.Nodes;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.node.NodeConnector;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.node.NodeConnectorKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.Node;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.NodeKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.l2.types.rev130827.EtherType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.ethernet.match.fields.EthernetDestinationBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.ethernet.match.fields.EthernetSourceBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.ethernet.match.fields.EthernetType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.ethernet.match.fields.EthernetTypeBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.match.EthernetMatchBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.match.IpMatch;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.match.IpMatchBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.match.Layer3Match;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.match.Layer4Match;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.match.layer._3.match.Ipv4MatchBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.match.layer._4.match.TcpMatchBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev150225.match.entry.value.grouping.match.entry.value.ipv4.dst._case.Ipv4Dst;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev150225.match.entry.value.grouping.match.entry.value.ipv4.dst._case.Ipv4DstBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev150225.match.entry.value.grouping.match.entry.value.ipv4.src._case.Ipv4Src;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev150225.match.entry.value.grouping.match.entry.value.ipv4.src._case.Ipv4SrcBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev150225.match.entry.value.grouping.match.entry.value.tcp.dst._case.TcpDst;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev150225.match.entry.value.grouping.match.entry.value.tcp.dst._case.TcpDstBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev150225.match.entry.value.grouping.match.entry.value.tcp.src._case.TcpSrc;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev150225.match.entry.value.grouping.match.entry.value.tcp.src._case.TcpSrcBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.ethernet.rev140528.KnownEtherType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.ipv4.rev140528.KnownIpProtocols;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.common.RpcResult;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import javassist.expr.NewArray;

public class ReactiveFlowWriter {
	private final String FLOW_ID_PREFIX = "route-";
	private final String MASK="/32";
	private SalFlowService salFlowService;
	private short flowTableId;
	private int flowPriority;
	private int flowIdleTimeout;
	private int flowHardTimeout;
	private AtomicLong flowIdInc = new AtomicLong();
    private AtomicLong flowCookieInc = new AtomicLong(0x5e00000000000000L);
    private final Integer DEFAULT_TABLE_ID = 0;
    private final Integer DEFAULT_PRIORITY = 200;
    private final Integer DEFAULT_HARD_TIMEOUT = 3600;
    private final Integer DEFAULT_IDLE_TIMEOUT = 1800;
    public ReactiveFlowWriter(SalFlowService salFlowService) {
		// TODO Auto-generated constructor stub
    	this.salFlowService=salFlowService;
	}

	public SalFlowService getSalFlowService() {
		return salFlowService;
	}

	public void setSalFlowService(SalFlowService salFlowService) {
		this.salFlowService = salFlowService;
	}

	public short getFlowTableId() {
		return flowTableId;
	}

	public void setFlowTableId(short flowTableId) {
		this.flowTableId = flowTableId;
	}

	public int getFlowPriority() {
		return flowPriority;
	}

	public void setFlowPriority(int flowPriority) {
		this.flowPriority = flowPriority;
	}

	public int getFlowIdleTimeout() {
		return flowIdleTimeout;
	}

	public void setFlowIdleTimeout(int flowIdleTimeout) {
		this.flowIdleTimeout = flowIdleTimeout;
	}

	public int getFlowHardTimeout() {
		return flowHardTimeout;
	}

	public void setFlowHardTimeout(int flowHardTimeout) {
		this.flowHardTimeout = flowHardTimeout;
	}

	public void addFlow(MacAddress src,MacAddress dst,Socket socket,RoutePath routePath){
		for(RouteNode node:routePath.getPath()){
			addFlowOnNode(src,dst,socket, node);
			Socket revsocket=socket.reverse();
			RouteNode revnode=node.reverse();
			addFlowOnNode(dst,src,revsocket, revnode);
		}
		
	}
	
	private void addFlowOnNode(MacAddress src,MacAddress dst,Socket socket,RouteNode routeNode){
		 Preconditions.checkNotNull(socket, "socket should not be null.");
	     Preconditions.checkNotNull(routeNode, "routeNode should not be null.");
	     
	     TableKey flowTableKey = new TableKey((short) flowTableId);
	     
	     InstanceIdentifier<Node> identifier = InstanceIdentifier.builder(Nodes.class).child(Node.class,new NodeKey(new NodeId(routeNode.getNodeId()))).toInstance();
	     InstanceIdentifier<NodeConnector> nodeConnector =InstanceIdentifierUtils.createNodeConnectorIdentifier(routeNode.getNodeId().getValue(), routeNode.getOutport().getValue()); 
	     NodeConnectorRef nodeConnectorRef=new NodeConnectorRef(nodeConnector);
	     
	     InstanceIdentifier<Flow> flowPath = buildFlowPath(nodeConnectorRef, flowTableKey);
	     
	     Flow flowBoy=createRouteFlow(src,dst,socket, nodeConnectorRef);
	     writeFlowToConfigData(flowPath, flowBoy);	     
	}
	 private InstanceIdentifier<Flow> buildFlowPath(NodeConnectorRef nodeConnectorRef, TableKey flowTableKey) {

	        // generate unique flow key
	        FlowId flowId = new FlowId(FLOW_ID_PREFIX+String.valueOf(flowIdInc.getAndIncrement()));
	        FlowKey flowKey = new FlowKey(flowId);
	        
	        return InstanceIdentifierUtils.generateFlowInstanceIdentifier(nodeConnectorRef, flowTableKey, flowKey);
	    }
	 private Flow createRouteFlow(MacAddress src,MacAddress dst,Socket socket,NodeConnectorRef destPort){
		 	FlowBuilder routeFlow = new FlowBuilder() //
	                .setTableId(flowTableId) //
	                .setFlowName("port2port");

	        // use its own hash code for id.
	        routeFlow.setId(new FlowId(Long.toString(routeFlow.hashCode())));

	        // create a match that has mac to mac ethernet match
	        EthernetMatchBuilder ethernetMatchBuilder = new EthernetMatchBuilder()
	        	   .setEthernetSource(new EthernetSourceBuilder().setAddress(src).build())
	        	   .setEthernetDestination(new EthernetDestinationBuilder().setAddress(dst).build())	        	   
	        	.setEthernetType(new EthernetTypeBuilder().setType(new EtherType(Long.valueOf(KnownEtherType.Ipv4.getIntValue()))).build());//
	        IpMatch ipMatch=new IpMatchBuilder().setIpProtocol((short)KnownIpProtocols.Tcp.getIntValue()).build();
	       // Ipv4Src ipv4Src=new Ipv4SrcBuilder().setIpv4Address(socket.getSrcAddress()).build();
	        //Ipv4Dst ipv4Dst=new Ipv4DstBuilder().setIpv4Address(socket.getDestAddress()).build();
	        TcpSrc tcpSrc=new TcpSrcBuilder().setPort(new PortNumber(socket.getSrcPort())).build();
	        TcpDst tcpDst=new TcpDstBuilder().setPort(new PortNumber(socket.getDestPort())).build();
	      /*  Layer3Match layer3Match=new Ipv4MatchBuilder().setIpv4Source(new Ipv4Prefix(socket.getSrcAddress().getValue()+MASK))
	        											  .setIpv4Destination(new Ipv4Prefix(socket.getDestAddress().getValue()+MASK))
	        											  .build();
	        											  */
	        Layer4Match layer4Match=new TcpMatchBuilder().setTcpSourcePort(new PortNumber(socket.getSrcPort()))
	        											 .setTcpDestinationPort(new PortNumber(socket.getDestPort()))
	        											 .build();
	        // set source in the match only if present
	        Match match = new MatchBuilder().setEthernetMatch(ethernetMatchBuilder.build())
	        								.setIpMatch(ipMatch)
	        								
	        								.setLayer4Match(layer4Match)
	        								.build();
	        								

	        Uri destPortUri = destPort.getValue().firstKeyOf(NodeConnector.class, NodeConnectorKey.class).getId();

	        Action outputToControllerAction = new ActionBuilder() //
	                .setOrder(0)
	                .setAction(new OutputActionCaseBuilder() //
	                        .setOutputAction(new OutputActionBuilder() //
	                                .setMaxLength(0xffff) //
	                                .setOutputNodeConnector(destPortUri) //
	                                .build()) //
	                        .build()) //
	                .build();

	        // Create an Apply Action
	        ApplyActions applyActions = new ApplyActionsBuilder().setAction(ImmutableList.of(outputToControllerAction))
	                .build();

	        // Wrap our Apply Action in an Instruction
	        Instruction applyActionsInstruction = new InstructionBuilder() //
	                .setOrder(0)
	                .setInstruction(new ApplyActionsCaseBuilder()//
	                        .setApplyActions(applyActions) //
	                        .build()) //
	                .build();

	        // Put our Instruction in a list of Instructions
	        routeFlow.setMatch(match) //
	                .setInstructions(new InstructionsBuilder() //
	                        .setInstruction(ImmutableList.of(applyActionsInstruction)) //
	                        .build()) //
	                .setPriority(flowPriority) //
	                .setBufferId(OFConstants.OFP_NO_BUFFER) //
	                .setHardTimeout(flowHardTimeout) //
	                .setIdleTimeout(flowIdleTimeout) //
	                .setCookie(new FlowCookie(BigInteger.valueOf(flowCookieInc.getAndIncrement())))
	                .setFlags(new FlowModFlags(false, false, false, false, false));

	        return routeFlow.build();
		 
	 }
	    /**
	     * Starts and commits data change transaction which modifies provided flow
	     * path with supplied body.
	     *
	     * @param flowPath
	     * @param flow
	     * @return transaction commit
	     */
	    private Future<RpcResult<AddFlowOutput>> writeFlowToConfigData(InstanceIdentifier<Flow> flowPath, Flow flow) {
	        final InstanceIdentifier<Table> tableInstanceId = flowPath.<Table>firstIdentifierOf(Table.class);
	        final InstanceIdentifier<Node> nodeInstanceId = flowPath.<Node>firstIdentifierOf(Node.class);
	        final AddFlowInputBuilder builder = new AddFlowInputBuilder(flow);
	        builder.setNode(new NodeRef(nodeInstanceId));
	        builder.setFlowRef(new FlowRef(flowPath));
	        builder.setFlowTable(new FlowTableRef(tableInstanceId));
	        builder.setTransactionUri(new Uri(flow.getId().getValue()));
	        return salFlowService.addFlow(builder.build());
	    }

}
