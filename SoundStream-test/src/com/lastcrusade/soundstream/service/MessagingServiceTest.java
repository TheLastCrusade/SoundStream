/*
 * Copyright 2013 The Last Crusade ContactLastCrusade@gmail.com
 * 
 * This file is part of SoundStream.
 * 
 * SoundStream is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SoundStream is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SoundStream.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.lastcrusade.soundstream.service;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.test.ServiceTestCase;
import android.util.Log;

import com.lastcrusade.soundstream.net.message.IMessage;
import com.lastcrusade.soundstream.net.message.PauseMessage;
import com.lastcrusade.soundstream.net.message.PlayMessage;
import com.lastcrusade.soundstream.net.message.SkipMessage;
import com.lastcrusade.soundstream.service.MessagingService;
import com.lastcrusade.soundstream.util.BroadcastRegistrar;
import com.lastcrusade.soundstream.util.IBroadcastActionHandler;

public class MessagingServiceTest extends ServiceTestCase {

    private static final String TAG = MessagingServiceTest.class.getName();
    
    private class CommandMessageTestHandler implements IBroadcastActionHandler {

        private boolean receiveActionCalled = false;

        @Override
        public void onReceiveAction(Context context, Intent intent) {
            this.receiveActionCalled = true;
            Log.i(TAG, "onReceiveAction called");
        }
        
        public boolean isReceiveActionCalled() {
            return receiveActionCalled;
        }
    }

    private class InappropriateActionTestHandler implements IBroadcastActionHandler {

        @Override
        public void onReceiveAction(Context context, Intent intent) {
            fail(intent.getAction() + " action should not be sent.");
        }
    }

    public MessagingServiceTest() {
        super(MessagingService.class);
    }
    
    private MessagingService getTheService() {
        Intent intent = new Intent();
        intent.setClassName(MessagingService.class.getPackage().getName(), MessagingService.class.getName());
        super.bindService(intent);
        MessagingService service = (MessagingService) super.getService();
        return service;
    }

    private void doACommandMessageTest(IMessage message, String action, String ... inappropriateActions) throws InterruptedException {
        MessagingService   service   = getTheService();
        BroadcastRegistrar registrar = new BroadcastRegistrar();                
        try {
            final CommandMessageTestHandler handler = new CommandMessageTestHandler();
            registrar.addAction(action, handler);
            
            for (String a : inappropriateActions) {
                registrar.addAction(a, new InappropriateActionTestHandler());
            }
            registrar.register(this.mContext);
            service.receiveMessage(1, message, "00:11:22:33:44:55");
            Thread.sleep(1000);
            assertTrue(handler.isReceiveActionCalled());
        } finally {
            registrar.unregister();
        }
    }
    
    public void testPauseMessageSupport() throws InterruptedException {
        doACommandMessageTest(new PauseMessage(),
                MessagingService.ACTION_PAUSE_MESSAGE,
                MessagingService.ACTION_PLAY_MESSAGE,
                MessagingService.ACTION_SKIP_MESSAGE);
    }
    
    public void testPlayMessageSupport() throws InterruptedException {
        doACommandMessageTest(new PlayMessage(),
                MessagingService.ACTION_PLAY_MESSAGE,
                MessagingService.ACTION_PAUSE_MESSAGE,
                MessagingService.ACTION_SKIP_MESSAGE);
    }
    
    public void testSkipMessageSupport() throws InterruptedException {
        doACommandMessageTest(new SkipMessage(),
                MessagingService.ACTION_SKIP_MESSAGE,
                MessagingService.ACTION_PAUSE_MESSAGE,
                MessagingService.ACTION_PLAY_MESSAGE);
    }
    
}
