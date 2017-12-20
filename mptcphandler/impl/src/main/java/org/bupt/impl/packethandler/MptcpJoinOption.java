/*
 * Copyright © 2017 xujun and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.bupt.impl.packethandler;

import java.util.Arrays;

public class MptcpJoinOption extends MptcpOption {
	private byte[] recToken;
	private byte[] seRanumb;
	public MptcpJoinOption(MptcpOption option) {
		// TODO Auto-generated constructor stub
		this.kind=option.getKind();
		this.length=option.getLength();
		this.content=option.getContent();
	}
	public byte[] getRecToken() {
		return recToken;
	}
	public void setRecToken(byte[] recToken) {
		this.recToken = recToken;
	}
	public byte[] getSeRanumb() {
		return seRanumb;
	}
	public void setSeRanumb(byte[] seRanumb) {
		this.seRanumb = seRanumb;
	}
	@Override
	public String toString() {
		return "MptcpJoinOption [recToken=" + Arrays.toString(recToken) + ", seRanumb=" + Arrays.toString(seRanumb)
				+ "]";
	}

	

}
