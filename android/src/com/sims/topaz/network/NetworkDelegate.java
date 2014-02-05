package com.sims.topaz.network;

import java.util.List;

import com.sims.topaz.network.modele.Message;
import com.sims.topaz.network.modele.Preview;

public interface NetworkDelegate {

	public void displayMessage(Message message);
	public void displayPreviews(List<Preview> list);
	public void networkError();
	
}
