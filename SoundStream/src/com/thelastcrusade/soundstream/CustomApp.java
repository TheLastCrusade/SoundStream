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

package com.thelastcrusade.soundstream;

import org.acra.ACRA;
import org.acra.ACRAConfiguration;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;
import android.content.res.AssetManager;
import android.util.Log;

import com.thelastcrusade.soundstream.service.ConnectionService;
import com.thelastcrusade.soundstream.service.MessagingService;
import com.thelastcrusade.soundstream.service.MusicLibraryService;
import com.thelastcrusade.soundstream.service.PlaylistService;
import com.thelastcrusade.soundstream.service.ServiceLocator;
import com.thelastcrusade.soundstream.service.ServiceNotBoundException;
import com.thelastcrusade.soundstream.service.TransferService;
import com.thelastcrusade.soundstream.service.UserListService;
import com.thelastcrusade.soundstream.util.SoundStreamPrefs;

@ReportsCrashes(
        formKey = "",
        formUri = "https://thelastcrusade.cloudant.com/acra-soundstream/_design/acra-storage/_update/report",
        reportType = org.acra.sender.HttpSender.Type.JSON,
        httpMethod = org.acra.sender.HttpSender.Method.PUT,
        // Your usual ACRA configuration
        mode = ReportingInteractionMode.DIALOG,
        resDialogText = R.string.crash_dialog_text,
        resDialogTitle = R.string.crash_dialog_title,
        resDialogCommentPrompt = R.string.crash_dialog_comment_prompt,
        resDialogOkToast = R.string.crash_dialog_ok_toast
        )
public class CustomApp extends Application {
    private final String TAG = CustomApp.class.getSimpleName();
    
    private static AssetManager assetManager;

    //NOTE: These are not "UNUSED"...they're used to bind services globally
    // to ensure they're started with the app
    //TODO: move to XML file, using an intent filter, such as:
    //  <service android:name=".PlaylistUpdaterService">
    //      <intent-filter>
    //          <action android:name="android.intent.action.MAIN" />
    //          <category android:name="android.intent.category.LAUNCHER" />
    //      </intent-filter>
    //  </service>

    @SuppressWarnings("unused")
    private ServiceLocator<ConnectionService>   connectionServiceLocator;
    @SuppressWarnings("unused")
    private ServiceLocator<MessagingService>    messagingServiceLocator;
    @SuppressWarnings("unused")
    private ServiceLocator<MusicLibraryService> musicLibraryLocator;
    @SuppressWarnings("unused")
    private ServiceLocator<PlaylistService>     playlistServiceLocator;
    @SuppressWarnings("unused")
    private ServiceLocator<TransferService>     transferServiceLocator;
    @SuppressWarnings("unused")
    private ServiceLocator<UserListService>     userListServiceLocator;


    public CustomApp() {
        super();
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        assetManager = getResources().getAssets();
        ACRAConfig();
        createServiceLocators();
    }
    
    private void ACRAConfig(){
        ACRAConfiguration conf = ACRA.getNewDefaultConfig(this);
        conf.setFormUriBasicAuthLogin(SoundStreamPrefs.getAcraUsername());
        conf.setFormUriBasicAuthPassword(SoundStreamPrefs.getAcraPassword());
        ACRA.setConfig(conf);

        ACRA.init(this);
    }
    
    @Override
    public void onTerminate() {
        
        super.onTerminate();
    }

    public void createServiceLocators() {
        //All of these services should exist for the lifetime of the application
        // bind them here so that you can quickly bind to them in the fragments
        connectionServiceLocator = new ServiceLocator<ConnectionService>(
                this, ConnectionService.class, ConnectionService.ConnectionServiceBinder.class);

        messagingServiceLocator = new ServiceLocator<MessagingService>(
                this, MessagingService.class, MessagingService.MessagingServiceBinder.class);

        musicLibraryLocator = new ServiceLocator<MusicLibraryService>(
                this, MusicLibraryService.class, MusicLibraryService.MusicLibraryServiceBinder.class);

        playlistServiceLocator = new ServiceLocator<PlaylistService>(
                this, PlaylistService.class, PlaylistService.PlaylistServiceBinder.class);

        transferServiceLocator = new ServiceLocator<TransferService>(
                this, TransferService.class, TransferService.TransferServiceBinder.class);

        userListServiceLocator = new ServiceLocator<UserListService>(
                this, UserListService.class, UserListService.UserListServiceBinder.class);
    }

    public static AssetManager getAssetManager(){
        return assetManager;
    }
}
