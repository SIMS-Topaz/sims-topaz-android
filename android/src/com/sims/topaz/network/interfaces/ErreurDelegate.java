package com.sims.topaz.network.interfaces;

import com.sims.topaz.network.modele.ApiError;

public interface ErreurDelegate {
	public void apiError(ApiError error);
	public void networkError();
	
}
