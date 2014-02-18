package com.sims.topaz.network.interfaces;

import java.util.List;

import com.sims.topaz.network.modele.Message;
import com.sims.topaz.network.modele.Preview;

public interface MessageDelegate {

	public void afterPostMessage(Message message);
	public void afterGetMessage(Message message);
	public void afterGetPreviews(List<Preview> list);

	
}