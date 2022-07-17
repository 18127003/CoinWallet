package me.app.coinwallet.ui.listeners;

/***
 * For query state UI update only, not for data
  */
public interface RepositoryQueryListener {
    default void onSucceed(){}
    default void onFailed(){}
}
