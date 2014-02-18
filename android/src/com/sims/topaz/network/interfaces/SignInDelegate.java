package com.sims.topaz.network.interfaces;

import com.sims.topaz.network.modele.User;

public interface SignInDelegate {
	public void afterSignIn(User user);
}
