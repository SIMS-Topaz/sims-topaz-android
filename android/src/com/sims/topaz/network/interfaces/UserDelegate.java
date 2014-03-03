package com.sims.topaz.network.interfaces;

import com.sims.topaz.network.modele.User;

public interface UserDelegate {
	public void afterGetUserInfo(User user);
	public void afterPostUserInfo(User user);
}
