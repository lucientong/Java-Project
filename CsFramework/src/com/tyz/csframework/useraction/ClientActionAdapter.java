package com.tyz.csframework.useraction;

/**
 * {@link IClientAction} 接口的适配器，用户可以通过覆盖本类的
 * 方法，自行配置需要的功能
 *
 * @author tyz
 */
public class ClientActionAdapter implements IClientAction {
    @Override
    public void dealServerAbnormalDisconnected() {
    }

    @Override
    public void afterConnectedSuccessfully() {
    }

    @Override
    public void afterConnectFailed() {
    }

    @Override
    public void dealPrivateMessage(String source, String message) {
    }

    @Override
    public void dealTargetIsNotExist(String target) {
    }

    @Override
    public void dealPublicMessage(String source, String message) {
    }

    @Override
    public boolean beSureOffline() {
        return true;
    }

    @Override
    public void beforeOffline() {

    }

    @Override
    public void afterOffline() {

    }

    @Override
    public void dealServerExecuteForceDown() {
    }

    @Override
    public void killedByServer(String reason) {
    }
}
