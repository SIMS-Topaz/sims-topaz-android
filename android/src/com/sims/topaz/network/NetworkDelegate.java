package com.sims.topaz.network;

import java.util.List;

import com.sims.topaz.network.modele.Message;
import com.sims.topaz.network.modele.Preview;

public interface NetworkDelegate {

	public void afterPostMessage(Message message);
	public void afterGetMessage(Message message);
	public void afterGetPreviews(List<Preview> list);
	public void networkError();
	
}
