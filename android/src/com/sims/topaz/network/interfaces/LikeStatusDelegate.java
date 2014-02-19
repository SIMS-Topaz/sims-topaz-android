package com.sims.topaz.network.interfaces;

import com.sims.topaz.network.modele.Message;

public interface LikeStatusDelegate {
	public void afterPostLikeStatus(Message message);

}
