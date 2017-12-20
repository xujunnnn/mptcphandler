/*
 * Copyright © 2017 xujun and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.bupt.impl.packethandler;

import org.opendaylight.l2switch.packethandler.decoders.utils.BitBufferHelper;
import org.opendaylight.l2switch.packethandler.decoders.utils.BufferException;
import org.opendaylight.l2switch.packethandler.decoders.utils.NetUtils;

import io.netty.util.NetUtil;

public class TcpOptionParser {
	private byte []payload;
	private MptcpOption option;
	public TcpOptionParser(byte[] payload) {
		// TODO Auto-generated constructor stub
		this.payload=payload;
		try {
			this.option=getContent();
		} catch (BufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public MptcpOption getContent() throws BufferException{
		int point=0;
		while (point<payload.length*NetUtils.NumBitsInAByte) {
 			int kind=BitBufferHelper.getInt(BitBufferHelper.getBits(payload, point, 8));
			point+=8;
			if(kind==0 || kind==1){
				continue;
			}
			int length=BitBufferHelper.getInt(BitBufferHelper.getBits(payload, point, 8));
			point+=8;
			byte[] content=BitBufferHelper.getBits(payload, point, (length-2)*NetUtils.NumBitsInAByte);
			point+=(length-2)*NetUtils.NumBitsInAByte;
			if(kind==30){
				MptcpOption mptcpOption=new MptcpOption();
				mptcpOption.setKind(kind);
				mptcpOption.setLength(length);
				mptcpOption.setContent(content);
				return mptcpOption;
			}
		}
		return null;
		
		
	} 
	
	public boolean isCable() throws BufferException{
		
		if(option==null)
			return false;
		int subType=BitBufferHelper.getInt(BitBufferHelper.getBits(option.getContent(), 0, 4));
		if(subType==0)
			return true;
		return false;
	}

	public boolean isJoin() throws BufferException{
		if(option==null)
			return false;
		int subType=BitBufferHelper.getInt(BitBufferHelper.getBits(option.getContent(), 0, 4));
		if(subType==1)
			return true;
		return false;
	}
	
	public MptcpCableOption getCable() throws BufferException{
		MptcpCableOption cableOption=new MptcpCableOption(option);
		byte[] content=option.getContent();
		int size=content.length*8;
		int point=4;
		short version=BitBufferHelper.getShort(BitBufferHelper.getBits(content,point,4));
		point+=12;
		cableOption.setVersion(version);
		if(point<=size-64)
			cableOption.setSenderKey(BitBufferHelper.getBits(content,point,64));
		point+=64;
		if(point<=size-64)
			cableOption.setReceKey(BitBufferHelper.getBits(content,76,64));
		return cableOption;
	}
	
	public MptcpJoinOption getJoin() throws BufferException{
		MptcpOption option=getContent();
		MptcpJoinOption joinOption=new MptcpJoinOption(option);
		byte[] content=option.getContent();
		int size=content.length*8;
		int point=16;
		joinOption.setRecToken(BitBufferHelper.getBits(content, point, 32));
		point+=32;
		if(point<=size-32){
			joinOption.setSeRanumb(BitBufferHelper.getBits(content, point, 32));
		}
		
		return joinOption;
	}
	

	
	
	

}
