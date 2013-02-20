package com.lastcrusade.fanclub;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.lastcrusade.fanclub.audio.SingleFileAudioPlayer;
import com.lastcrusade.fanclub.components.PlaybarFragment;
import com.lastcrusade.fanclub.components.PlaybarFragment.PlayControlListener;
import com.lastcrusade.fanclub.library.MediaStoreWrapper;
import com.lastcrusade.fanclub.model.Song;
import com.lastcrusade.fanclub.model.SongMetadata;

/**
 * Test activity for the playbar.  This will display the playbar in an activity, and respond to play button clicks
 * by playing the first sound it can find on the phone.
 * 
 * @author Jesse Rosalia
 *
 */
public class TestPlaybarActivity extends FragmentActivity {

    private SingleFileAudioPlayer player;
    private MediaStoreWrapper mediaStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_test_playbar);
        
        this.player = new SingleFileAudioPlayer();
        this.mediaStore = new MediaStoreWrapper(this);
        PlaybarFragment pb = (PlaybarFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_playbar);
        
        pb.setPlayControlListener(new PlayControlListener() {

            @Override
            public void onPlay() {
                List<SongMetadata> songMetas = mediaStore.list();
                SongMetadata meta = songMetas.get(0);
                Song song = mediaStore.loadSongData(meta);
//                TestPlaybarActivity.this.bindService(service, conn, flags)
                player.setSongByPath(song.getFilePath());
                player.play();
            }

            @Override
            public void onPause() {
                player.pause();                
            }

            @Override
            public void onSkip() {
                // TODO Auto-generated method stub
                
            }
            
        });
    }
}
