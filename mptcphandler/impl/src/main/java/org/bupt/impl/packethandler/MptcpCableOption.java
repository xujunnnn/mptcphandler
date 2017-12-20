/*
 * Copyright © 2017 xujun and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.bupt.impl.packethandler;

public class MptcpCableOption extends MptcpOption{
	private short version;
	private byte[] senderKey;
	private byte[] receKey;
	public MptcpCableOption(MptcpOption option) {
		// TODO Auto-generated constructor stub
		this.kind=option.getKind();
		this.length=option.getLength();
		this.content=option.getContent();
	}
	public MptcpCableOption() {
		// TODO Auto-generated constructor stub
	}
	public short getVersion() {
		return version;
	}
	public void setVersion(short version) {
		this.version = version;
	}
	public byte[] getSenderKey() {
		return senderKey;
	}
	public void setSenderKey(byte[] senderKey) {
		this.senderKey = senderKey;
	}
	public byte[] getReceKey() {
		return receKey;
	}
	public void setReceKey(byte[] receKey) {
		this.receKey = receKey;
	}
	
}
